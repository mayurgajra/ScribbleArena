package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_PLAYERS_LIST

data class PlayersList(
    val players: List<PlayerData>
) : BaseModel(TYPE_PLAYERS_LIST)
