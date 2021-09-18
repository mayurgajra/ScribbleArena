package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_PING

/**
 * Class for sending the ping to the server to indicate that the devide
 * is connected. If this class isn't sent to server in certain time frame
 * then it considers that either player got disconnected or just left the game.
 */
class Ping : BaseModel(TYPE_PING)