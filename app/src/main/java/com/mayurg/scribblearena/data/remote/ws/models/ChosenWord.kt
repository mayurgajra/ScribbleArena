package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CHOSEN_WORD

/**
 * Data class used to display chosen word for the given room in particular phase.
 *
 * @param chosenWord is the word chosen by current drawing player. Depending on the phase it will
 * be masked with (_) for players other than current drawing player
 *
 * @param roomName is room in which the word was chosen
 */
data class ChosenWord(
    val chosenWord: String,
    val roomName: String,
): BaseModel(TYPE_CHOSEN_WORD)
