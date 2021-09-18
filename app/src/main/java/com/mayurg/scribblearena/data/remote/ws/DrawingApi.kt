package com.mayurg.scribblearena.data.remote.ws

import com.mayurg.scribblearena.data.remote.ws.models.BaseModel
import com.mayurg.scribblearena.data.remote.ws.models.ChatMessage
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

/**
 * It contains the socket calls used for drawing game
 *
 * Connection for Websockets is established via [Scarlet]
 *
 * Created On 11/08/2021
 * @author Mayur Gajra
 */
interface DrawingApi {

    /**
     * It receives the events related to [WebSocket] Like OnConnectionOpened,closed etc
     *
     * @return Flow of [WebSocket.Event] to be observed
     */
    @Receive
    fun observeEvents(): Flow<WebSocket.Event>

    /**
     * It send the [baseModel] data to server. For example [ChatMessage]
     */
    @Send
    fun sendBaseModel(baseModel: BaseModel)

    /**
     * It receives the app related events sent from the server.
     *
     * @return Flow of [BaseModel] to be observed
     */
    @Receive
    fun observeBaseModels(): Flow<BaseModel>

}