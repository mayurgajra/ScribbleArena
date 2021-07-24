package com.mayurg.scribblearena.ui.drawing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mayurg.scribblearena.databinding.ActivityDrawingBinding

/**
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}