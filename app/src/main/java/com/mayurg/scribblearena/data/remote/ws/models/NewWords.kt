package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_NEW_WORDS

/**
 * Data class for handling the list of new words to select from for a new round
 *
 * @param newWords is a list of words sent by server when game phase is in new round
 */
data class NewWords(
    val newWords: List<String>
): BaseModel(TYPE_NEW_WORDS)
