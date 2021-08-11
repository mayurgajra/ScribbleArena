package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CHAT_MESSAGE


data class ChatMessage(
    val from: String,
    val roomName: String,
    val message: String,
    val timestamp: Long
): BaseModel(TYPE_CHAT_MESSAGE)