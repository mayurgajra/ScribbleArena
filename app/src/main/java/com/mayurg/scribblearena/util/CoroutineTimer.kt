package com.mayurg.scribblearena.util

import kotlinx.coroutines.*

/**
 * Created On 25/08/2021
 * @author Mayur Gajra
 */
class CoroutineTimer {

    fun timeAndEmit(
        duration: Long,
        coroutineScope: CoroutineScope,
        emissionFrequency: Long = 100L,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        onEmit: (Long) -> Unit
    ): Job {
        return coroutineScope.launch(dispatcher) {
            var time = duration
            while (time >= 0){
                onEmit(time)
                time -= emissionFrequency
                delay(emissionFrequency)
            }

        }

    }
}