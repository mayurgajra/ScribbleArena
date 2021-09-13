package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.data.remote.ws.models.GameError.Companion.ERROR_ROOM_NOT_FOUND
import com.mayurg.scribblearena.util.Constants.TYPE_GAME_ERROR

/**
 * Data class for receiving an error related to the game.
 *
 * @param errorType is an int indicating the type of error.
 *
 * For now only [ERROR_ROOM_NOT_FOUND] is the error type.
 */
data class GameError(
    val errorType: Int
): BaseModel(TYPE_GAME_ERROR){
    companion object {
        const val ERROR_ROOM_NOT_FOUND = 0
    }
}
