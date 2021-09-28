package com.mayurg.scribblearena.ui.drawing

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.*
import androidx.navigation.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.adapters.ChatMessageAdapter
import com.mayurg.scribblearena.adapters.PlayerAdapter
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.data.remote.ws.models.*
import com.mayurg.scribblearena.databinding.ActivityDrawingBinding
import com.mayurg.scribblearena.ui.dialogs.ExitGameDialog
import com.mayurg.scribblearena.util.Constants
import com.mayurg.scribblearena.util.Constants.MAX_WORD_VOICE_GUESS_AMOUNT
import com.mayurg.scribblearena.util.hideKeyboard
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject


/**
 * Request code for asking permission to record audio.
 */
private const val REQUEST_CODE_RECORD_AUDIO = 1

/**
 * Main drawing game activity.
 *
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
@AndroidEntryPoint
class DrawingActivity : AppCompatActivity(), LifecycleObserver,
    EasyPermissions.PermissionCallbacks, RecognitionListener {

    /**
     * [binding] holds the reference to the views of [DrawingActivity]
     */
    private lateinit var binding: ActivityDrawingBinding

    /**
     * [viewModel] is used to perform business logic. e.g sending drawing data,chat messages
     */
    private val viewModel: DrawingViewModel by viewModels()

    /**
     * [args] is the navigation arguments passed from calling fragment such as "username","roomName"
     */
    private val args: DrawingActivityArgs by navArgs()

    /**
     * [clientId] is just randomUUID() saved in preferences to identify user re-joining
     */
    @Inject
    lateinit var clientId: String

    /**
     * [toggle] is used to tie-down/sync the drawer & icon with open/close status of the
     * drawer
     */
    private lateinit var toggle: ActionBarDrawerToggle

    /**
     * [rvPlayers] a list of players inside the drawer view
     */
    private lateinit var rvPlayers: RecyclerView

    /**
     * Adapter for displaying the chat messages & announcements
     */
    private lateinit var chatMessageAdapter: ChatMessageAdapter

    /**
     * [speechRecognizer] to get access to the speech recognizer service
     */
    private lateinit var speechRecognizer: SpeechRecognizer

    /**
     * [speechIntent] an intent to pass to [speechRecognizer] to start listening for speech
     */
    private lateinit var speechIntent: Intent

    /**
     * Adapter class for displaying players in drawer
     */
    @Inject
    lateinit var playerAdapter: PlayerAdapter

    /**
     * [updateChatJob] a job to update [chatMessageAdapter] diff util calculation in bg.
     */
    private var updateChatJob: Job? = null

    /**
     * [updatePlayersJob] a job to update [playerAdapter] diff util calculation in bg.
     */
    private var updatePlayersJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        subscribeToUiStateUpdates()
        listenToConnectionEvent()
        listenToSocketEvents()
        setupRecyclerView()
        setupDrawer()
        setupArgs()
        setupStateRestorationPolicyForAdapter()
        setupPlayersRecyclerView()
        setupSpeechRecognizer()
        setViewClickListeners()
    }

    /**
     * Initialize [SpeechRecognizer] & create [speechIntent] to launch the service
     * when user clicks on speech button.
     *
     * If speech recognition is available then set listeners
     */
    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_WORD_VOICE_GUESS_AMOUNT)
        }
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer.setRecognitionListener(this)
        }
    }

    /**
     * Sets up the restoration policy for [chatMessageAdapter] to restore scroll position
     * of chat recycler view after config change (e.g. after Activity rotation)
     */
    private fun setupStateRestorationPolicyForAdapter() {
        chatMessageAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    /**
     * Setup the arguments received from navigation [args] such as room name
     */
    private fun setupArgs() {
        binding.drawingView.roomName = args.roomName
    }

    /**
     * Setup click event listeners
     */
    private fun setViewClickListeners() {
        binding.ibPlayers.setOnClickListener {
            binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.root.openDrawer(GravityCompat.START)
        }

        binding.ibClearText.setOnClickListener {
            binding.etMessage.text?.clear()
        }

        binding.ibSend.setOnClickListener {
            viewModel.sendChatMessage(
                ChatMessage(
                    args.username,
                    args.roomName,
                    binding.etMessage.text.toString(),
                    System.currentTimeMillis()
                )
            )
            binding.etMessage.text?.clear()
            hideKeyboard(binding.root)
        }

        binding.ibMic.setOnClickListener {
            if (!viewModel.speechToTextEnabled.value && hasRecordAudioPermissions()) {
                viewModel.startListening()
            } else if (!viewModel.speechToTextEnabled.value) {
                requestRecordAudioPermission()
            } else {
                viewModel.stopListening()
            }
        }

        binding.ibUndo.setOnClickListener {
            if (binding.drawingView.isUserDrawing) {
                binding.drawingView.undo()
                viewModel.sendBaseModel(DrawAction(DrawAction.ACTION_UNDO))
            }
        }
        binding.drawingView.setPathDataChangedListener {
            viewModel.setPathData(it)
        }

        binding.colorGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.checkRadioButton(checkedId)
        }

        binding.drawingView.setOnDrawListener {
            if (binding.drawingView.isUserDrawing) {
                viewModel.sendBaseModel(it)
            }
        }
    }

    /**
     * setup recyclerview to display list of players in the room inside left drawer
     */
    private fun setupPlayersRecyclerView() {
        val header = layoutInflater.inflate(R.layout.nav_drawer_header, binding.navView)
        rvPlayers = header.findViewById(R.id.rvPlayers)

        rvPlayers.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(this@DrawingActivity)
        }
    }

    /**
     * setup drawer & [toggle] related things. such as toggle sync state & listeners for drawer
     */
    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        toggle.syncState()
        binding.root.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

            override fun onDrawerOpened(drawerView: View) = Unit

            override fun onDrawerClosed(drawerView: View) {
                binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) = Unit

        })
        binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    /**
     * onPause call the onSaveInstanceState of chat recycler view so that it can be
     * restored during recreation
     */
    override fun onPause() {
        super.onPause()
        binding.rvChat.layoutManager?.onSaveInstanceState()
    }

    /**
     * Check if app has the permission of recording the audio for speech guessing functionality
     */
    private fun hasRecordAudioPermissions() = EasyPermissions.hasPermissions(
        this,
        Manifest.permission.RECORD_AUDIO
    )

    /**
     * If app doesn't have the permission to record audio then we request for it using [EasyPermissions]
     */
    private fun requestRecordAudioPermission() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_record_audio),
            REQUEST_CODE_RECORD_AUDIO,
            Manifest.permission.RECORD_AUDIO
        )
    }

    /**
     * Called when permission are either granted or denied by the user. From this we pass the result
     * to [EasyPermissions] to handle
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * When permission to record audio is granted. Display a toast to indicate that
     * how to proceed with using speech guessing functionality
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            Toast.makeText(this, R.string.speech_to_text_info, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * When permission are denied permanently display an app settings dialog
     * to user to allow permissions from system settings.
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    /**
     * Toggle the speech icon according to the [isEnabled] status
     */
    private fun setSpeechRecognitionEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            binding.ibMic.setImageResource(R.drawable.ic_mic)
            speechRecognizer.startListening(speechIntent)
        } else {
            binding.ibMic.setImageResource(R.drawable.ic_mic_off)
            binding.etMessage.hint = ""
            speechRecognizer.stopListening()
        }
    }

    /**
     * When the [speechRecognizer] is ready for to accept speech,Set hint showing that listening has
     * started
     */
    override fun onReadyForSpeech(params: Bundle?) {
        binding.etMessage.text?.clear()
        binding.etMessage.hint = getString(R.string.listening)
    }

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    /**
     * When speech ends notify that change to [viewModel] & also change the icons
     */
    override fun onEndOfSpeech() {
        viewModel.stopListening()
    }

    override fun onError(error: Int) = Unit

    /**
     * When results from [speechRecognizer] arrive. Joins them by a space & set it
     * to message input edit-texts & also notify that change to [viewModel]
     * & also change the icons.
     */
    override fun onResults(results: Bundle?) {
        val strings = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val guessedWords = strings?.joinToString { " " }
        guessedWords?.let {
            binding.etMessage.setText(guessedWords)
        }
        speechRecognizer.stopListening()
        viewModel.stopListening()
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    /**
     * Change the visibility of color selection palette on top of drawing view.
     * e.g It should be visible when logged in user is the drawing player.
     *
     * @param isVisible a boolean flag to indicate whether color palette & undo image button should be
     * visible or not
     */
    private fun setColorGroupVisibility(isVisible: Boolean) {
        binding.colorGroup.isVisible = isVisible
        binding.ibUndo.isVisible = isVisible
    }

    /**
     * Change the visibility of message input edit-text,clean button & send message button.
     * e.g It should be visible if logged in user is the guessing player
     *
     * @param isVisible a boolean flag to indicate whether edit-text,clean button
     * & send message button should be visible or not.
     */
    private fun setMessageInputVisibility(isVisible: Boolean) {
        binding.apply {
            tilMessage.isVisible = isVisible
            ibSend.isVisible = isVisible
            ibClearText.isVisible = isVisible
        }
    }

    /**
     * called when a color is selected from the color palette.
     * it passes that selected color to the drawingView & sets the default thickness to
     * [Constants.DEFAULT_PAINT_THICKNESS]
     */
    private fun selectColor(color: Int) {
        binding.drawingView.setColor(color)
        binding.drawingView.setThickness(Constants.DEFAULT_PAINT_THICKNESS)
    }


    /**
     * subscribe to UI state updates sent from [viewModel] & update the UI accordingly
     */
    private fun subscribeToUiStateUpdates() {
        /**
         * Listen to updates of speech to text boolean & update the icon based on that or
         * show toast if [SpeechRecognizer] service is not available
         */
        lifecycleScope.launchWhenStarted {
            viewModel.speechToTextEnabled.collect { isEnabled ->
                if (isEnabled && !SpeechRecognizer.isRecognitionAvailable(this@DrawingActivity)) {
                    Toast.makeText(
                        this@DrawingActivity,
                        R.string.speech_not_available,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.ibMic.isEnabled = false
                } else {
                    setSpeechRecognitionEnabled(isEnabled)
                }
            }
        }

        /**
         * Listen to path that is being drawn & set to the drawing view.
         * Also, used to draw the whole path if user joined after game started
         */
        lifecycleScope.launchWhenStarted {
            viewModel.pathData.collect { pathData ->
                binding.drawingView.setPaths(pathData)
            }
        }

        /**
         * Listen to chat messages received in the playing room & update the [chatMessageAdapter]
         */
        lifecycleScope.launchWhenStarted {
            viewModel.chat.collect { chat ->
                if (chatMessageAdapter.chatObjects.isEmpty()) {
                    updateChatMessageList(chat)
                }
            }
        }

        /**
         * Listen to new words updates when the game state changes
         * to choose word for the next round.
         * Show 3 words to choose from.
         */
        lifecycleScope.launchWhenStarted {
            viewModel.newWords.collect {
                val newWords = it.newWords
                if (newWords.isEmpty()) {
                    return@collect
                }
                binding.apply {
                    btnFirstWord.text = newWords[0]
                    btnSecondWord.text = newWords[1]
                    btnThirdWord.text = newWords[2]

                    btnFirstWord.setOnClickListener {
                        viewModel.chooseWord(newWords[0], args.roomName)
                        viewModel.setChooseWordOverlayVisibility(false)
                    }
                    btnSecondWord.setOnClickListener {
                        viewModel.chooseWord(newWords[1], args.roomName)
                        viewModel.setChooseWordOverlayVisibility(false)
                    }
                    btnThirdWord.setOnClickListener {
                        viewModel.chooseWord(newWords[2], args.roomName)
                        viewModel.setChooseWordOverlayVisibility(false)
                    }

                }
            }
        }

        /**
         * Listen to updates of current select color & update [selectColor]
         */
        lifecycleScope.launchWhenStarted {
            viewModel.selectedColorButtonId.collect { id ->
                binding.colorGroup.check(id)
                when (id) {
                    R.id.rbBlack -> selectColor(Color.BLACK)
                    R.id.rbBlue -> selectColor(Color.BLUE)
                    R.id.rbGreen -> selectColor(Color.GREEN)
                    R.id.rbOrange -> selectColor(
                        ContextCompat.getColor(
                            this@DrawingActivity,
                            R.color.orange
                        )
                    )
                    R.id.rbRed -> selectColor(Color.RED)
                    R.id.rbYellow -> selectColor(Color.YELLOW)
                    R.id.rbEraser -> {
                        binding.drawingView.setColor(Color.WHITE)
                        binding.drawingView.setThickness(40f)
                    }
                }
            }
        }

        /**
         * Listen to game state updates & update UI based on whether the current logged in user
         * is drawing.
         *
         * 1) Display the currently select word
         * 2) Show the color palette, undo button & enable the drawing view only if logged in user is drawing
         * 3) Show input edit text & mic only  logged in user is guessing
         */
        lifecycleScope.launchWhenStarted {
            viewModel.gameState.collect { gameState ->
                binding.apply {
                    tvCurWord.text = gameState.word
                    val isUserDrawing = gameState.drawingPlayer == args.username
                    setColorGroupVisibility(isUserDrawing)
                    setMessageInputVisibility(!isUserDrawing)
                    ibUndo.isEnabled = isUserDrawing
                    drawingView.isUserDrawing = isUserDrawing
                    ibMic.isVisible = !isUserDrawing
                    drawingView.isEnabled = isUserDrawing
                }
            }
        }

        /**
         * Listen to updates of player in the playing room.
         * Called when players join or leave room.
         * Update the list of players in left side drawer
         */
        lifecycleScope.launchWhenStarted {
            viewModel.players.collect { players ->
                updatePlayersList(players)
            }
        }

        /**
         * Listen to updates of timer of current phase.
         * Update the time progressbar UI on each event in decreasing manner
         */
        lifecycleScope.launchWhenStarted {
            viewModel.phaseTime.collect { time ->
                binding.roundTimerProgressBar.progress = time.toInt()
                binding.tvRemainingTimeChooseWord.text = (time / 1000L).toString()
            }
        }

        /**
         * Listen to phase changes of the game & update UI
         *
         * 1) [Room.Phase.WAITING_FOR_PLAYERS] then change phase text to "waiting for players"
         *    & don't display timer progressbar
         * 2) [Room.Phase.WAITING_FOR_START] then change phase text to "Waiting for start" & update
         *    timer progressbar
         * 3) [Room.Phase.NEW_ROUND] then change phase text to "Player is choosing a word", disable
         *     drawing view & show the words to choose from overlay to a drawing player
         * 4) [Room.Phase.GAME_RUNNING] then remove the words selection overlay & start the timer
         * 5) [Room.Phase.SHOW_WORD] then reset all the drawing view,color,thickness, progress
         */
        lifecycleScope.launchWhenStarted {
            viewModel.phase.collect { phase ->
                when (phase.phase) {
                    Room.Phase.WAITING_FOR_PLAYERS -> {
                        binding.tvCurWord.text = getString(R.string.waiting_for_players)
                        viewModel.cancelTimer()
                        viewModel.setConnectionProgressbarVisibility(false)
                        binding.roundTimerProgressBar.progress = binding.roundTimerProgressBar.max
                    }

                    Room.Phase.WAITING_FOR_START -> {
                        binding.roundTimerProgressBar.progress = phase.time.toInt()
                        binding.tvCurWord.text = getString(R.string.waiting_for_start)
                    }

                    Room.Phase.NEW_ROUND -> {
                        phase.drawingPlayer?.let { player ->
                            binding.tvCurWord.text =
                                getString(R.string.player_is_drawing, player)
                        }
                        binding.apply {
                            drawingView.isEnabled = false
                            drawingView.setColor(Color.BLACK)
                            drawingView.setThickness(Constants.DEFAULT_PAINT_THICKNESS)
                            roundTimerProgressBar.max = phase.time.toInt()
                            val isUserDrawingPlayer = phase.drawingPlayer == args.username
                            binding.chooseWordOverlay.isVisible = isUserDrawingPlayer
                        }
                    }

                    Room.Phase.GAME_RUNNING -> {
                        binding.chooseWordOverlay.isVisible = false
                        binding.roundTimerProgressBar.max = phase.time.toInt()
                    }

                    Room.Phase.SHOW_WORD -> {
                        binding.apply {
                            /**
                            In case timer runs out before user could lift touch up or
                            complete drawing in that case we need to finish last part of the drawing
                             */
                            if (drawingView.isDrawing) {
                                drawingView.finishOffDrawing()
                            }
                            drawingView.isEnabled = false
                            drawingView.setColor(Color.BLACK)
                            drawingView.setThickness(Constants.DEFAULT_PAINT_THICKNESS)
                            roundTimerProgressBar.max = phase.time.toInt()

                        }
                    }
                    else -> Unit
                }
            }
        }

        /**
         * Listen to events of progress bar visibility & display loading animation when
         * connecting to socket or performing the call.
         */
        lifecycleScope.launchWhenStarted {
            viewModel.connectionProgressbarVisible.collect { isVisible ->
                binding.connectionProgressBar.isVisible = isVisible
            }
        }

        /**
         * Listen to updates to display the word chooser overlay.
         * When phase is [Room.Phase.NEW_ROUND] it's displayed
         * with 3 words sent from the server
         */
        lifecycleScope.launchWhenStarted {
            viewModel.chooseWordOverlayVisible.collect { isVisible ->
                binding.chooseWordOverlay.isVisible = isVisible
            }
        }
    }

    /**
     * Update the players list asynchronously with [DiffUtil]
     */
    private fun updatePlayersList(players: List<PlayerData>) {
        updatePlayersJob?.cancel()
        updatePlayersJob = lifecycleScope.launch {
            playerAdapter.updateDataset(players)
        }
    }

    /**
     * Update the chats list asynchronously with [DiffUtil]
     */
    private fun updateChatMessageList(chat: List<BaseModel>) {
        updateChatJob?.cancel()
        updateChatJob = lifecycleScope.launch {
            chatMessageAdapter.updateDataset(chat)
        }
    }

    /**
     * Add the new chat message or announcement event to the adapter list.
     * Check if we're at the end of the list then scroll down for the new message to appear.
     * This makes sure that user has not scrolled up to see the older messages.
     * In that case we don't scroll to bottom.
     */
    private suspend fun addChatObjectToRecyclerView(chatObject: BaseModel) {
        val canScrollDown = binding.rvChat.canScrollVertically(1)
        updateChatMessageList(chatMessageAdapter.chatObjects + chatObject)
        updateChatJob?.join()
        if (!canScrollDown) {
            binding.rvChat.scrollToPosition(chatMessageAdapter.chatObjects.size - 1)
        }
    }

    /**
     * Listen [DrawingViewModel.SocketEvent] updates & data sent along to update the UI
     *
     */
    private fun listenToSocketEvents() = lifecycleScope.launchWhenStarted {
        viewModel.socketEvent.collect { event ->
            when (event) {
                /**
                 * Called when drawing user is drawing something. Only for guessing players
                 *
                 * 1) [MotionEvent.ACTION_DOWN] - Touch event started by the drawing user
                 * 2) [MotionEvent.ACTION_MOVE] - Drawing user is moving/dragging the finger
                 *                                without lifting it up drawing a path.
                 * 3) [MotionEvent.ACTION_UP] - Touch ended by the drawing user
                 */
                is DrawingViewModel.SocketEvent.DrawDataEvent -> {
                    val drawData = event.data
                    if (!binding.drawingView.isUserDrawing) {
                        when (drawData.motionEvent) {
                            MotionEvent.ACTION_DOWN -> {
                                binding.drawingView.startedTouchExternally(drawData)
                            }
                            MotionEvent.ACTION_MOVE -> {
                                binding.drawingView.movedTouchExternally(drawData)
                            }
                            MotionEvent.ACTION_UP -> {
                                binding.drawingView.releasedTouchExternally(drawData)
                            }
                        }
                    }
                }

                /**
                 * Display the chosen word to every one
                 */
                is DrawingViewModel.SocketEvent.ChosenWordEvent -> {
                    binding.tvCurWord.text = event.data.chosenWord
                    binding.ibUndo.isEnabled = false
                }

                /**
                 * Received a new [ChatMessage]. Add it to the chats recyclerview.
                 */
                is DrawingViewModel.SocketEvent.ChatMessageEvent -> {
                    addChatObjectToRecyclerView(event.data)
                }

                /**
                 * Received a new [Announcement]. Add it to the chats recyclerview.
                 */
                is DrawingViewModel.SocketEvent.AnnouncementEvent -> {
                    addChatObjectToRecyclerView(event.data)
                }

                /**
                 * Undo action was performed by the drawing user.
                 * Call the undo on drawing view.
                 */
                is DrawingViewModel.SocketEvent.UndoEvent -> {
                    binding.drawingView.undo()
                }

                /**
                 * There was an in the game.
                 * If it's [GameError.ERROR_ROOM_NOT_FOUND] -> Finish the activity
                 */
                is DrawingViewModel.SocketEvent.GameErrorEvent -> {
                    when (event.data.errorType) {
                        GameError.ERROR_ROOM_NOT_FOUND -> finish()
                    }
                }

                /**
                 * A whole collections of drawing done in this current round.
                 * Useful in case of user joins
                 */
                is DrawingViewModel.SocketEvent.RoundDrawInfoEvent -> {
                    binding.drawingView.update(event.data)
                }

                /**
                 * Clear out the drawing view when game state changes
                 */
                is DrawingViewModel.SocketEvent.GameStateEvent -> {
                    binding.drawingView.clear()
                }

                else -> Unit
            }
        }
    }

    /**
     * Listen to socket connection events.
     *
     * 1) [WebSocket.Event.OnConnectionOpened] -> When socket connection is opened with the server,
     * start the [JoinRoomHandshake] & display the progress animation
     * 2) [WebSocket.Event.OnConnectionFailed] -> When socket connection fails display the message
     * snack-bar to user & hide the progress animation
     * 3) [WebSocket.Event.OnConnectionClosed] -> When connection is closed. Just hide the progress
     * animation.
     */
    private fun listenToConnectionEvent() = lifecycleScope.launchWhenStarted {
        viewModel.connectionEvent.collect { event ->
            when (event) {
                is WebSocket.Event.OnConnectionOpened<*> -> {
                    viewModel.sendBaseModel(
                        JoinRoomHandshake(
                            args.username,
                            args.roomName,
                            clientId
                        )
                    )
                    viewModel.setConnectionProgressbarVisibility(false)
                }

                is WebSocket.Event.OnConnectionFailed -> {
                    viewModel.setConnectionProgressbarVisibility(false)
                    Snackbar.make(
                        binding.root,
                        R.string.error_connection_failed,
                        Snackbar.LENGTH_LONG
                    ).show()
                    event.throwable.printStackTrace()
                }

                is WebSocket.Event.OnConnectionClosed -> {
                    viewModel.setConnectionProgressbarVisibility(false)
                }
                else -> Unit
            }
        }
    }

    /**
     * Set the [chatMessageAdapter] & chat recyclerview
     */
    private fun setupRecyclerView() = binding.rvChat.apply {
        chatMessageAdapter = ChatMessageAdapter(args.username)
        adapter = chatMessageAdapter
        layoutManager = LinearLayoutManager(this@DrawingActivity)

    }

    /**
     * When toggle option item is clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When app stops/goes in background. Disconnect the socket connection.
     * As there is no need for realtime messages & drawing events to be displayed
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppInBackground() {
        viewModel.disconnect()
    }

    /**
     * Show a exit confirmation dialog when user presses the back button,
     * If "Yes" is clicked then disconnect from the socket connection & finish the activity.
     */
    override fun onBackPressed() {
        ExitGameDialog().apply {
            setPositiveClickListener {
                viewModel.disconnect()
                finish()
            }
        }.show(supportFragmentManager, null)
    }
}