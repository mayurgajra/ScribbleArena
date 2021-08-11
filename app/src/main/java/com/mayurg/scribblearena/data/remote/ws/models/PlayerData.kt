package com.mayurg.scribblearena.data.remote.ws.models

data class PlayerData(
    val username: String,
    val isDrawing: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
)
