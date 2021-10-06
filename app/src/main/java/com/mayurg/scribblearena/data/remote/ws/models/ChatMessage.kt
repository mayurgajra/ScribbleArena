package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CHAT_MESSAGE

/**
 * Data class for a chat message. Which is used during the game to send & receive messages.
 *
 * @param from is username of message sent by user it's unique.
 * @param roomName is room in which message is sent/received
 * @param message is the actual content/message which will be displayed
 * @param timestamp is time in millis when this message was sent.
 */
data class ChatMessage(
    val from: String,
    val roomName: String,
    val message: String,
    val timestamp: Long
): BaseModel(TYPE_CHAT_MESSAGE)