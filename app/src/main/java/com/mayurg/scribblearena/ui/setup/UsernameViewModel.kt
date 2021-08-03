package com.mayurg.scribblearena.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.repository.SetupRepository
import com.mayurg.scribblearena.util.Constants.MAX_ROOM_NAME_LENGTH
import com.mayurg.scribblearena.util.Constants.MAX_USERNAME_LENGTH
import com.mayurg.scribblearena.util.Constants.MIN_ROOM_NAME_LENGTH
import com.mayurg.scribblearena.util.Constants.MIN_USERNAME_LENGTH
import com.mayurg.scribblearena.util.DispatcherProvider
import com.mayurg.scribblearena.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created On 29/07/2021
 * @author Mayur Gajra
 */
@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val repository: SetupRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class SetupEvent {
        object InputEmptyError : SetupEvent()

        object InputTooShortError : SetupEvent()

        object InputTooLongError : SetupEvent()

        data class NavigateToSelectRoomEvent(val username: String) : SetupEvent()
    }

    private val _setupEvent = MutableSharedFlow<SetupEvent>()
    val setupEvent: SharedFlow<SetupEvent> = _setupEvent

    fun validateUsernameAndNavigateToSelectRoom(username: String) {
        viewModelScope.launch(dispatchers.main) {
            val trimmedUsername = username.trim()
            when {
                trimmedUsername.isEmpty() -> {
                    _setupEvent.emit(SetupEvent.InputEmptyError)
                }
                trimmedUsername.length < MIN_USERNAME_LENGTH -> {
                    _setupEvent.emit(SetupEvent.InputTooShortError)
                }
                trimmedUsername.length > MAX_USERNAME_LENGTH -> {
                    _setupEvent.emit(SetupEvent.InputTooLongError)
                }
                else -> _setupEvent.emit(SetupEvent.NavigateToSelectRoomEvent(trimmedUsername))
            }
        }
    }


}