package ru.shawarma.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.auth.databinding.FragmentRegisterBinding
import ru.shawarma.auth.viewmodels.RegisterUIState
import ru.shawarma.auth.viewmodels.RegisterViewModel
import ru.shawarma.core.data.utils.Errors

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.registerState.filterNotNull().stateIn(this)
                    .collect{state ->
                        handleRegisterState(state)
                    }
            }
        }
    }

    private fun handleRegisterState(state: RegisterUIState){
        when(state){
            is RegisterUIState.Success -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.confirm_email_title)
                    .setMessage(R.string.confirm_email_text)
                    .setPositiveButton(R.string.ok){ dialog, _ ->
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }.create().show()
            }
            is RegisterUIState.Error -> {
                when(val message = state.message){
                    Errors.EMPTY_INPUT_ERROR -> binding!!.registerErrorTextView.text = resources.getString(R.string.empty_input_error)
                    Errors.NETWORK_ERROR -> binding!!.registerErrorTextView.text = resources.getString(R.string.unknown_error)
                    else -> binding!!.registerErrorTextView.text = message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}