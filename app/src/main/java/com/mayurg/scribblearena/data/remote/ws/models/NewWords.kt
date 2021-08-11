package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants.TYPE_NEW_WORDS

data class NewWords(
    val newWords: List<String>
): BaseModel(TYPE_NEW_WORDS)
