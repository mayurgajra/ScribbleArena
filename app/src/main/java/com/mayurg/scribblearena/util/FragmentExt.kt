package com.mayurg.scribblearena.util

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Created On 30/07/2021
 * @author Mayur Gajra
 */

fun Fragment.snackbar(text: String) {
    Snackbar.make(
        requireView(),
        text,
        Snackbar.LENGTH_LONG
    ).show()
}

fun Fragment.snackbar(@StringRes res: Int) {
    Snackbar.make(
        requireView(),
        getString(res),
        Snackbar.LENGTH_LONG
    ).show()
}