package com.mayurg.scribblearena.data.remote.ws

/**
 * Data class for handling data related to the individual game room
 *
 * @param name is the unique name of the room
 * @param maxPlayers is maximum number of players allowed by creator of the room
 * @param playerCount is number of players currently in the room
 *
 * Created On 26/07/2021
 * @author Mayur Gajra
 */
data class Room(
    val name: String,
    val maxPlayers: Int,
    val playerCount: Int = 1
) {
    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }
}