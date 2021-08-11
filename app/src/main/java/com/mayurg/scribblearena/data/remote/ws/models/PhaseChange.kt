package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.util.Constants.TYPE_PHASE_CHANGE

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    var drawingPlayer: String? = null
): BaseModel(TYPE_PHASE_CHANGE)
