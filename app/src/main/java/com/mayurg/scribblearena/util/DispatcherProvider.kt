package com.mayurg.scribblearena.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created On 23/07/2021
 * @author Mayur Gajra
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}