package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_GAME_STATE

/**
 * Data class to hold the current drawing player & current selected for current round
 *
 * @param drawingPlayer is the unique username of the user currently drawing
 * @param word is the currently selected word in the running round. For guessing players
 * it will be just underscores(_) & for drawing player it will be an actual word.
 */
data class GameState(
    val drawingPlayer: String,
    val word: String
) : BaseModel(TYPE_GAME_STATE)