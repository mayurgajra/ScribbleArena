package com.mayurg.scribblearena.ui.drawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.data.remote.ws.DrawingApi
import com.mayurg.scribblearena.data.remote.ws.models.*
import com.mayurg.scribblearena.data.remote.ws.models.DrawAction.Companion.ACTION_UNDO
import com.mayurg.scribblearena.util.DispatcherProvider
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
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
        data class ChatMessageEvent(val data: ChatMessage) : SocketEvent()
        data class AnnouncementEvent(val data: Announcement) : SocketEvent()
        data class GameStateEvent(val data: GameState) : SocketEvent()
        data class DrawDataEvent(val data: DrawData) : SocketEvent()
        data class NewWordsEvent(val data: NewWords) : SocketEvent()
        data class ChosenWordEvent(val data: ChosenWord) : SocketEvent()
        data class GameErrorEvent(val data: GameError) : SocketEvent()
        data class RoundDrawInfoEvent(val data: RoundDrawInfo) : SocketEvent()
        object UndoEvent : SocketEvent()
    }

    private val _newWords = MutableStateFlow(NewWords(listOf()))
    val newWords: StateFlow<NewWords> = _newWords

    private val _chat = MutableStateFlow<List<BaseModel>>(listOf())
    val chat: StateFlow<List<BaseModel>> = _chat

    private val _selectedColorButtonId = MutableStateFlow(R.id.rbBlack)
    val selectedColorButtonId: StateFlow<Int> = _selectedColorButtonId

    private val _connectionProgressbarVisible = MutableStateFlow(true)
    val connectionProgressbarVisible: StateFlow<Boolean> = _connectionProgressbarVisible

    private val _chooseWordOverlayVisible = MutableStateFlow(false)
    val chooseWordOverlayVisible: StateFlow<Boolean> = _chooseWordOverlayVisible

    private val connectionEventChannel = Channel<WebSocket.Event>()
    val connectionEvent = connectionEventChannel.receiveAsFlow().flowOn(dispatchers.io)

    private val socketEventChannel = Channel<SocketEvent>()
    val socketEvent = socketEventChannel.receiveAsFlow().flowOn(dispatchers.io)

    init {
        observeEvents()
        observeBaseModels()
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

                    is Ping -> {
                        sendBaseModel(Ping())
                    }
                }
            }
        }
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