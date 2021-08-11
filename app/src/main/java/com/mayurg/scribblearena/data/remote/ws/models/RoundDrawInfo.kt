package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CURRENT_ROUND_DRAW_INFO

data class RoundDrawInfo(
    val data: List<String>
) : BaseModel(TYPE_CURRENT_ROUND_DRAW_INFO)
