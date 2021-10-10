package com.mayurg.scribblearena.ui.drawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.data.remote.ws.DrawingApi
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.data.remote.ws.models.*
import com.mayurg.scribblearena.data.remote.ws.models.DrawAction.Companion.ACTION_UNDO
import com.mayurg.scribblearena.ui.views.DrawingView
import com.mayurg.scribblearena.util.Constants.TYPE_DRAW_ACTION
import com.mayurg.scribblearena.util.Constants.TYPE_DRAW_DATA
import com.mayurg.scribblearena.util.CoroutineTimer
import com.mayurg.scribblearena.util.DispatcherProvider
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * A viewmodel responsible for business logic of [DrawingActivity]
 *
 * @param dispatchers a provider for [CoroutineDispatcher]. It's a good pattern so that during testing we replace it dummy dispatchers
 * @param gson is a [Gson] instance used for converting string json to a kotlin class
 * @param drawingApi is an api which sends, receives socket events related to drawing game
 *
 * Created On 07/08/2021
 * @author Mayur Gajra
 */
@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val gson: Gson,
    private val drawingApi: DrawingApi
) : ViewModel() {

    sealed class SocketEvent {
        /**
         * [ChatMessageEvent] is sent/received when chat message is sent/received.
         * It contains [data] of type [ChatMessage]
         */
        data class ChatMessageEvent(val data: ChatMessage) : SocketEvent()

        /**
         * [AnnouncementEvent] is received when an announcement like player joined/left is made from
         * server. It contains [data] of type [Announcement]
         */
        data class AnnouncementEvent(val data: Announcement) : SocketEvent()

        /**
         * [GameStateEvent] is received when game is running to display the word & notify current
         * drawing player. It contains [data] of type [GameState]
         */
        data class GameStateEvent(val data: GameState) : SocketEvent()

        /**
         * [DrawDataEvent] is received when the drawing player draws something on the canvas.
         * It contains [data] of type [DrawData]
         */
        data class DrawDataEvent(val data: DrawData) : SocketEvent()

        /**
         * [NewWordsEvent] is received when user has to choose a single word from 3 words for a new
         * round. It contains [data] of type [NewWords]
         */
        data class NewWordsEvent(val data: NewWords) : SocketEvent()

        /**
         * [ChosenWordEvent] is received when user selects a word from the provided options for a
         * current round. It contains [data] of type [ChosenWord]
         */
        data class ChosenWordEvent(val data: ChosenWord) : SocketEvent()

        /**
         * [GameErrorEvent] is received when there is an related to game/room. e.g [GameError.ERROR_ROOM_NOT_FOUND]
         * It contains the [data] of type [GameError]
         */
        data class GameErrorEvent(val data: GameError) : SocketEvent()

        /**
         * [RoundDrawInfoEvent] is received when user joins after the game has begun.
         * So that drawing can be performed for the past part.
         * It contains [data] list of drawing data.
         */
        data class RoundDrawInfoEvent(val data: List<BaseModel>) : SocketEvent()

        /**
         * [UndoEvent] is received when drawing player presses undo button.
         */
        object UndoEvent : SocketEvent()
    }

    /**
     * [_pathData] is the collection of data being drawn on the canvas
     */
    private val _pathData = MutableStateFlow(Stack<DrawingView.PathData>())
    val pathData: StateFlow<Stack<DrawingView.PathData>> = _pathData


    /**
     * [_players] is the list of players in the game room
     */
    private val _players = MutableStateFlow<List<PlayerData>>(listOf())
    val players: StateFlow<List<PlayerData>> = _players

    /**
     * [_newWords] is the list of words for the new round
     */
    private val _newWords = MutableStateFlow(NewWords(listOf()))
    val newWords: StateFlow<NewWords> = _newWords

    /**
     * [_phase] represents the current phase of the game.
     */
    private val _phase = MutableStateFlow(PhaseChange(null, 0L, null))
    val phase: StateFlow<PhaseChange> = _phase

    /**
     * [_phaseTime] is the allocated time for each phase of the game.
     */
    private val _phaseTime = MutableStateFlow(0L)
    val phaseTime: StateFlow<Long> = _phaseTime

    /**
     * [_gameState] represents the drawing player & word being drawn for the current round.
     */
    private val _gameState = MutableStateFlow(GameState("", ""))
    val gameState: StateFlow<GameState> = _gameState

    /**
     * [_chat] is the list of chat messages in the room
     */
    private val _chat = MutableStateFlow<List<BaseModel>>(listOf())
    val chat: StateFlow<List<BaseModel>> = _chat

    /**
     * [_selectedColorButtonId] is the selected color button id by the drawing player.
     * It used to highlight the button.
     */
    private val _selectedColorButtonId = MutableStateFlow(R.id.rbBlack)
    val selectedColorButtonId: StateFlow<Int> = _selectedColorButtonId

    /**
     * [_connectionProgressbarVisible] is used to manage state of progress bar visibility
     */
    private val _connectionProgressbarVisible = MutableStateFlow(true)
    val connectionProgressbarVisible: StateFlow<Boolean> = _connectionProgressbarVisible

    /**
     * [_chooseWordOverlayVisible] is used to manage whether word selection layout should be shown.
     */
    private val _chooseWordOverlayVisible = MutableStateFlow(false)
    val chooseWordOverlayVisible: StateFlow<Boolean> = _chooseWordOverlayVisible

    /**
     * [_speechToTextEnabled] is used to manage the icon to display whether speech to text functionality
     * is enabled by user or not.
     */
    private val _speechToTextEnabled = MutableStateFlow(false)
    val speechToTextEnabled: StateFlow<Boolean> = _speechToTextEnabled

    /**
     * [connectionEventChannel] is used to manage events related to connections with the server.
     * [WebSocket.Event] are sent to notify whether sockets are connected or closed.
     */
    private val connectionEventChannel = Channel<WebSocket.Event>()
    val connectionEvent = connectionEventChannel.receiveAsFlow().flowOn(dispatchers.io)

    /**
     * [socketEventChannel] is used to pass the events of the game namely [SocketEvent]
     * When client is connected to the server via [WebSocket]
     */
    private val socketEventChannel = Channel<SocketEvent>()
    val socketEvent = socketEventChannel.receiveAsFlow().flowOn(dispatchers.io)

    private val timer = CoroutineTimer()
    private var timerJob: Job? = null

    init {
        observeEvents()
        observeBaseModels()
    }

    private fun setTimer(duration: Long) {
        timerJob?.cancel()
        timerJob = timer.timeAndEmit(duration, viewModelScope) {
            _phaseTime.value = it
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
    }

    fun startListening() {
        _speechToTextEnabled.value = true
    }

    fun stopListening() {
        _speechToTextEnabled.value = false
    }

    fun setPathData(stack: Stack<DrawingView.PathData>) {
        _pathData.value = stack
    }

    fun setChooseWordOverlayVisibility(isVisible: Boolean) {
        _chooseWordOverlayVisible.value = isVisible
    }

    fun setConnectionProgressbarVisibility(isVisible: Boolean) {
        _connectionProgressbarVisible.value = isVisible
    }

    fun checkRadioButton(id: Int) {
        _selectedColorButtonId.value = id
    }

    private fun observeEvents() {
        viewModelScope.launch(dispatchers.io) {
            drawingApi.observeEvents().collect { event ->
                connectionEventChannel.send(event)
            }
        }
    }

    private fun observeBaseModels() {
        viewModelScope.launch(dispatchers.io) {
            drawingApi.observeBaseModels().collect { data ->
                when (data) {
                    is DrawData -> {
                        socketEventChannel.send(SocketEvent.DrawDataEvent(data))
                    }
                    is DrawAction -> {
                        when (data.action) {
                            ACTION_UNDO -> socketEventChannel.send(SocketEvent.UndoEvent)
                        }
                    }

                    is ChatMessage -> {
                        socketEventChannel.send(SocketEvent.ChatMessageEvent(data))
                    }
                    is Announcement -> {
                        socketEventChannel.send(SocketEvent.AnnouncementEvent(data))
                    }
                    is GameError -> {
                        socketEventChannel.send(SocketEvent.GameErrorEvent(data))
                    }

                    is NewWords -> {
                        _newWords.value = data
                        socketEventChannel.send(SocketEvent.NewWordsEvent(data))
                    }

                    is ChosenWord -> {
                        socketEventChannel.send(SocketEvent.ChosenWordEvent(data))
                    }

                    is PhaseChange -> {
                        data.phase?.let {
                            _phase.value = data
                        }
                        _phaseTime.value = data.time
                        if (data.phase != Room.Phase.WAITING_FOR_PLAYERS) {
                            setTimer(data.time)
                        }
                    }

                    is GameState -> {
                        _gameState.value = data
                        socketEventChannel.send(SocketEvent.GameStateEvent(data))
                    }

                    is PlayersList -> {
                        _players.value = data.players
                    }

                    is RoundDrawInfo -> {
                        val drawActions = mutableListOf<BaseModel>()
                        data.data.forEach { drawAction ->
                            val jsonObject = JsonParser.parseString(drawAction).asJsonObject
                            val type = when (jsonObject.get("type").asString) {
                                TYPE_DRAW_DATA -> DrawData::class.java
                                TYPE_DRAW_ACTION -> DrawAction::class.java
                                else -> BaseModel::class.java
                            }
                            drawActions.add(gson.fromJson(drawAction, type))
                        }
                        socketEventChannel.send(SocketEvent.RoundDrawInfoEvent(drawActions))
                    }

                    is Ping -> {
                        sendBaseModel(Ping())
                    }
                }
            }
        }
    }

    fun disconnect() {
        sendBaseModel(DisconnectRequest())
    }

    fun chooseWord(word: String, roomName: String) {
        val chosenWord = ChosenWord(word, roomName)
        sendBaseModel(chosenWord)
    }

    fun sendBaseModel(data: BaseModel) {
        viewModelScope.launch(dispatchers.io) {
            drawingApi.sendBaseModel(data)
        }
    }

    fun sendChatMessage(message: ChatMessage) {
        if (message.message.isEmpty()) {
            return
        }
        viewModelScope.launch(dispatchers.io) {
            drawingApi.sendBaseModel(message)
        }
    }


}