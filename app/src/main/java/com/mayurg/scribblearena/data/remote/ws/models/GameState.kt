package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_GAME_STATE

data class GameState(
    val drawingPlayer: String,
    val word: String
) : BaseModel(TYPE_GAME_STATE)