package com.mayurg.scribblearena.util

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

/**
 * Created On 23/07/2021
 * @author Mayur Gajra
 */

val Context.dataStore by preferencesDataStore("settings")