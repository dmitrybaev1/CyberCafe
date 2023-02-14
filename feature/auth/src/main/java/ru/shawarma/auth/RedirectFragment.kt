package ru.shawarma.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.auth.navigation.AuthNavigation
import ru.shawarma.auth.viewmodels.RedirectState
import ru.shawarma.auth.viewmodels.RedirectViewModel

class RedirectFragment : Fragment() {

    private val viewModel: RedirectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_redirect,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.redirectState.filterNotNull().stateIn(viewLifecycleOwner.lifecycleScope)
                    .collect{state ->
                        when(state){
                            RedirectState.NoToken -> findNavController().navigate(R.id.actionRedirectToAuth)
                            is RedirectState.RefreshError -> findNavController().navigate(R.id.actionRedirectToAuth,
                                bundleOf("error" to state.message)
                            )
                            is RedirectState.TokenValid -> (requireActivity() as AuthNavigation).navigateToMenu(state.authData)
                        }
                    }
            }
        }
    }
}