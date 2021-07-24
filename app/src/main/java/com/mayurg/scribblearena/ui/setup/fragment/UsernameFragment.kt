package com.mayurg.scribblearena.ui.setup.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.databinding.FragmentUsernameBinding

/**
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
class UsernameFragment : Fragment(R.layout.fragment_username) {

    private var _binding: FragmentUsernameBinding? = null
    private val binding: FragmentUsernameBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUsernameBinding.bind(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}