package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.util.Constants.TYPE_PHASE_CHANGE

/**
 * Data class for handling the phase changes in game
 *
 * @param phase is the current phase of game in a room. Different phases can be found at [Room.Phase]
 * @param time it time in millis. It indicates how much time is allowed for the current phase
 * @param drawingPlayer is the unique username of the user currently drawing
 */
data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    var drawingPlayer: String? = null
) : BaseModel(TYPE_PHASE_CHANGE)
