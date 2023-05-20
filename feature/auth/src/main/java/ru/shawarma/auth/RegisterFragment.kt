package ru.shawarma.auth

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
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
import ru.shawarma.core.ui.CommonComponentsController

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
                        handleRegisterState(state,view)
                    }
            }
        }
    }

    private fun handleRegisterState(state: RegisterUIState,view: View){
        resetErrorOnFields()
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
                val registerTextInputLayoutEmail = binding!!.registerTextInputLayoutEmail
                val registerTextInputLayoutPassword =  binding!!.registerTextInputLayoutPassword
                val registerErrorTextView = binding!!.registerErrorTextView
                when(val message = state.message){
                    Errors.NO_INTERNET_ERROR -> {
                        (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
                        viewModel.resetState()
                    }
                    Errors.EMPTY_INPUT_ERROR -> {
                        registerErrorTextView.text = resources.getString(R.string.empty_input_error)
                        registerTextInputLayoutEmail.error = " "
                        registerTextInputLayoutPassword.error = " "
                    }
                    Errors.EMAIL_ERROR -> {
                        registerErrorTextView.text = resources.getString(R.string.email_error)
                        registerTextInputLayoutEmail.error = " "
                    }
                    Errors.PASSWORD_ERROR -> {
                        registerErrorTextView.text = resources.getString(R.string.password_error)
                        registerTextInputLayoutPassword.error = " "
                    }
                    Errors.NETWORK_ERROR -> registerErrorTextView.text = resources.getString(R.string.unknown_error)
                    else -> registerErrorTextView.text = message
                }
                (AnimatorInflater.loadAnimator(requireContext(), R.animator.error_text_anim) as AnimatorSet).apply {
                    setTarget(registerErrorTextView)
                    interpolator = LinearInterpolator()
                    start()
                }
            }
        }
    }
    private fun resetErrorOnFields(){
        val registerTextInputLayoutEmail = binding!!.registerTextInputLayoutEmail
        val registerTextInputLayoutPassword =  binding!!.registerTextInputLayoutPassword
        registerTextInputLayoutEmail.error = null
        registerTextInputLayoutPassword.error = null
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}