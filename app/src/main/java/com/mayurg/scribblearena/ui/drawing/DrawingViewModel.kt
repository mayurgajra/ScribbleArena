package com.mayurg.scribblearena.ui.drawing

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.data.remote.ws.DrawingApi
import com.mayurg.scribblearena.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created On 07/08/2021
 * @author Mayur Gajra
 */
@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val gson: Gson,
    private val drawingApi: DrawingApi
) : ViewModel() {

    private val _selectedColorButtonId = MutableStateFlow(R.id.rbBlack)
    val selectedColorButtonId: StateFlow<Int> = _selectedColorButtonId

    fun checkRadioButton(id: Int) {
        _selectedColorButtonId.value = id
    }


}