package com.mayurg.scribblearena.ui.setup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}