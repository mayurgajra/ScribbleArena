package com.mayurg.scribblearena.data.remote.ws.models

/**
 * Data class for sending the create new room request.
 *
 * @param name is a unique name of the room to be created
 * @param maxPlayers is the number of players user wants to allow for the room.
 */
data class CreateRoomRequest(
    val name: String,
    val maxPlayers: Int
)
