package com.mayurg.scribblearena.data.remote.ws

import com.mayurg.scribblearena.data.remote.ws.models.BaseModel
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

/**
 * Created On 11/08/2021
 * @author Mayur Gajra
 */
interface DrawingApi {

    @Receive
    fun observeEvents(): Flow<WebSocket.Event>

    @Send
    fun sendBaseModel(baseModel: BaseModel)

    @Receive
    fun observeBaseModels(): Flow<BaseModel>

}