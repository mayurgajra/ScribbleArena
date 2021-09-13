package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.data.remote.ws.models.DrawAction.Companion.ACTION_UNDO
import com.mayurg.scribblearena.util.Constants.TYPE_DRAW_ACTION

/**
 * Data class for sending an action on drawing board. such as [ACTION_UNDO]
 *
 * @param action an action to perform on drawing board
 *
 * For now only [ACTION_UNDO] is supported
 */
data class DrawAction(
    val action: String
) : BaseModel(TYPE_DRAW_ACTION) {

    companion object {
        const val ACTION_UNDO = "ACTION_UNDO"
    }
}
