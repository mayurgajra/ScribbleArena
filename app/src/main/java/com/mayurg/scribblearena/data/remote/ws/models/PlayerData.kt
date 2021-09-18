package com.mayurg.scribblearena.data.remote.ws.models

/**
 * Data class for handling the individual player data.
 *
 * @param username it's a unique name of the player
 * @param isDrawing is a flag that indicates whether this player is currently drawing or not
 * @param score is the amount of points scored by a player in that room
 * @param rank is the number that indicates player's current position in the room based on [score]
 */
data class PlayerData(
    val username: String,
    val isDrawing: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
)
