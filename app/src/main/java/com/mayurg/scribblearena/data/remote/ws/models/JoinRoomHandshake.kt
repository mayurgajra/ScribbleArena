package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_JOIN_ROOM_HANDSHAKE

/**
 * Data class for sending request to perform a handshake
 * When a web socket connection is established with websockets
 *
 * @param username is the unique username logged into the app
 * @param roomName is the room name user has joined
 * @param clientId is UUID.randomUUID() used to identify user re-joining or a completely new user
 */
data class JoinRoomHandshake(
    val username: String,
    val roomName: String,
    val clientId: String
) : BaseModel(TYPE_JOIN_ROOM_HANDSHAKE)
