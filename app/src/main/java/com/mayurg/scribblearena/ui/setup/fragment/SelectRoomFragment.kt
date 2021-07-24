package com.mayurg.scribblearena.ui.setup.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mayurg.scribblearena.databinding.FragmentSelectRoomBinding
import com.mayurg.scribblearena.databinding.FragmentUsernameBinding

/**
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
class SelectRoomFragment : Fragment() {

    private var _binding: FragmentSelectRoomBinding? = null
    private val binding: FragmentSelectRoomBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectRoomBinding.bind(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}