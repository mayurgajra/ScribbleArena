package com.mayurg.scribblearena

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created On 23/07/2021
 * @author Mayur Gajra
 */
@HiltAndroidApp
class DrawingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

    }
}