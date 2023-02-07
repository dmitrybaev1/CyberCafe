package ru.shawarma.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.shawarma.auth.databinding.FragmentRegisterBinding

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
        viewModel.navCommand.observe(viewLifecycleOwner){
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_email_title)
                .setMessage(R.string.confirm_email_text)
                .setPositiveButton(R.string.ok){ dialog, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack()
                }.create().show()
        }
        viewModel.registerState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { state -> handleRegisterState(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleRegisterState(state: RegisterUIState){
        when(state){
            is RegisterUIState.Success -> {}
            is RegisterUIState.Error -> {
                if(state.message == "empty input error")
                    binding!!.registerErrorTextView.text = resources.getString(R.string.empty_input_error)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}