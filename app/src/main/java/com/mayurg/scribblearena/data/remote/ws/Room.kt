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
        /**
         * When  at-least two players are not active/connected
         */
        WAITING_FOR_PLAYERS,

        /**
         * When two player are active & waiting for timer to finish
         */
        WAITING_FOR_START,

        /**
         * When all players have had their drawing change it goes back to starting round
         */
        NEW_ROUND,

        /**
         * When game is currently running
         */
        GAME_RUNNING,

        /**
         * Display the current word after guessing time is over to show what an actual word was
         */
        SHOW_WORD
    }
}