package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_GAME_ERROR

data class GameError(
    val errorType: Int
): BaseModel(TYPE_GAME_ERROR){
    companion object {
        const val ERROR_ROOM_NOT_FOUND = 0
    }
}
