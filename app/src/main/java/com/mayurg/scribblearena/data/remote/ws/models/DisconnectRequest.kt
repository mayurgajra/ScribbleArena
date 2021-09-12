package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_DISCONNECT_REQUEST

/**
 * Class for sending a disconnection from room request through socket.
 */
class DisconnectRequest : BaseModel(TYPE_DISCONNECT_REQUEST)