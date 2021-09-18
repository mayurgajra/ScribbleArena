package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_PLAYERS_LIST

/**
 * Data class for handling the list of players in the current room
 *
 * @param players is the list of players in the current room
 */
data class PlayersList(
    val players: List<PlayerData>
) : BaseModel(TYPE_PLAYERS_LIST)
