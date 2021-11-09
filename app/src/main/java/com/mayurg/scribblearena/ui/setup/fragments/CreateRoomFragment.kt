package com.mayurg.scribblearena.ui.setup.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.databinding.FragmentCreateRoomBinding
import com.mayurg.scribblearena.ui.setup.CreateRoomViewModel
import com.mayurg.scribblearena.util.Constants
import com.mayurg.scribblearena.util.hideKeyboard
import com.mayurg.scribblearena.util.navigateSafely
import com.mayurg.scribblearena.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Room creation fragment.
 *
 * Created On 24/07/2021
 * @author Mayur Gajra
 */
@AndroidEntryPoint
class CreateRoomFragment : Fragment(R.layout.fragment_create_room) {

    /**
     * [binding] holds the reference to the views of [CreateRoomFragment]
     */
    private var _binding: FragmentCreateRoomBinding? = null
    private val binding: FragmentCreateRoomBinding
        get() = _binding!!

    /**
     * [viewModel] is used to perform business logic. e.g creating room,validating room name
     * & passing thr result to UI.
     */
    private val viewModel: CreateRoomViewModel by viewModels()

    /**
     * [args] is the bundle of arguments passed safely using [navArgs]. It contains "username" chosen
     * by player.
     */
    private val args: CreateRoomFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateRoomBinding.bind(view)
        setupRoomSizeSpinner()
        listenToEvents()
        setCreateRoomClickEvent()
    }

    /**
     * Setup the click listener on create button.
     *
     * Call create room method of [viewModel] & make the progress bar visible on click.
     */
    private fun setCreateRoomClickEvent() {
        binding.btnCreateRoom.setOnClickListener {
            binding.createRoomProgressBar.isVisible = true
            viewModel.createRoom(
                Room(
                    binding.etRoomName.text.toString(),
                    binding.tvMaxPersons.text.toString().toInt()
                )
            )
            requireActivity().hideKeyboard(binding.root)
        }
    }

    /**
     * Listens to setup events emitted by [viewModel] after verifying the input data.
     * E.g InputEmptyError,InputTooShortError.
     * Shows the appropriate indicators in case of error or else performs the action.
     */
    private fun listenToEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.setupEvent.collect { event ->

                when (event) {
                    is CreateRoomViewModel.SetupEvent.CreateRoomEvent -> {
                        viewModel.joinRoom(args.username, event.room.name)
                    }

                    is CreateRoomViewModel.SetupEvent.InputEmptyError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(R.string.error_field_empty)
                    }
                    is CreateRoomViewModel.SetupEvent.InputTooShortError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(
                            getString(
                                R.string.error_room_name_too_short,
                                Constants.MIN_ROOM_NAME_LENGTH
                            )
                        )
                    }
                    is CreateRoomViewModel.SetupEvent.InputTooLongError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(
                            getString(
                                R.string.error_room_name_too_long,
                                Constants.MAX_ROOM_NAME_LENGTH
                            )
                        )
                    }

                    is CreateRoomViewModel.SetupEvent.CreateRoomErrorEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(event.error)
                    }

                    is CreateRoomViewModel.SetupEvent.JoinRoomEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        findNavController().navigateSafely(
                            R.id.action_createRoomFragment_to_drawingActivity,
                            args = Bundle().apply {
                                putString("username", args.username)
                                putString("roomName", event.roomName)
                            }
                        )
                    }

                    is CreateRoomViewModel.SetupEvent.JoinRoomErrorEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(event.error)
                    }

                    else -> Unit
                }

            }
        }
    }

    /**
     * Populate data in the maximum player count spinner.
     */
    private fun setupRoomSizeSpinner() {
        val roomSizes = resources.getStringArray(R.array.room_size_array)
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_room_size, roomSizes)
        binding.tvMaxPersons.setAdapter(adapter)
    }

    /**
     * Destroy the binding onDestroy.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}