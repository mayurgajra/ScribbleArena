package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CURRENT_ROUND_DRAW_INFO

/**
 * Data class for handling current round info
 *
 * @param data is either the [DrawData] or [DrawAction] in the form of raw json string
 */
data class RoundDrawInfo(
    val data: List<String>
) : BaseModel(TYPE_CURRENT_ROUND_DRAW_INFO)
