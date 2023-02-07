package ru.shawarma.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.shawarma.auth.databinding.FragmentAuthBinding
import ru.shawarma.core.data.Errors

class AuthFragment : Fragment() {

    private var binding: FragmentAuthBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    /*Here we should implement observers like triggers in fragment lifecycle (not view!). Only LiveData
     works with value caching and doesn't invoke duplicate value observe when we move back here from other fragments*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navCommand.observe(this){
            findNavController().navigate(R.id.actionAuthToRegister)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.authState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { state -> handleAuthState(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleAuthState(state: AuthUIState){
        when(state){
            is AuthUIState.Success -> {
                (requireActivity() as AuthNavigation).navigateToMenu(state.authData)
            }
            is AuthUIState.Error -> {
                when(val message = state.message){
                    Errors.emptyInputError -> binding!!.authErrorTextView.text = resources.getString(R.string.empty_input_error)
                    Errors.networkError -> binding!!.authErrorTextView.text = resources.getString(R.string.unknown_error)
                    else -> binding!!.authErrorTextView.text = message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}