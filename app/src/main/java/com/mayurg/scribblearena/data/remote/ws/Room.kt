package com.mayurg.scribblearena.data.remote.ws

/**
 * Created On 26/07/2021
 * @author Mayur Gajra
 */
data class Room(
    val name: String,
    val maxPlayers: Int,
    val playerCount: Int = 1,

) {
    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }
}