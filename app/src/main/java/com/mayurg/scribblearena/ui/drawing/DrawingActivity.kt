package com.mayurg.scribblearena.ui.drawing

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mayurg.scribblearena.databinding.ActivityDrawingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
@AndroidEntryPoint
class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    private val viewModel: DrawingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.colorGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.checkRadioButton(checkedId)
        }
        subscribeToUiStateUpdates()
    }

    private fun subscribeToUiStateUpdates() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedColorButtonId.collect { id ->
                binding.colorGroup.check(id)
            }
        }
    }
}