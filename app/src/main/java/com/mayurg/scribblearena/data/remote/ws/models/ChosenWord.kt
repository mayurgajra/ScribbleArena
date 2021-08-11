package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_CHOSEN_WORD


data class ChosenWord(
    val chosenWord: String,
    val roomName: String,
): BaseModel(TYPE_CHOSEN_WORD)
