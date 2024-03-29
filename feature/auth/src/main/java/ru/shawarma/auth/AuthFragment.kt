package ru.shawarma.auth

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.auth.databinding.FragmentAuthBinding
import ru.shawarma.auth.viewmodels.AuthUIState
import ru.shawarma.auth.viewmodels.AuthViewModel
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.core.ui.EventObserver


@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var binding: FragmentAuthBinding? = null

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupToolbarUpButton()
        val binding = FragmentAuthBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        arguments?.let{bundle ->
            viewModel.setRefreshTokenErrorAndClearData(bundle.getString("error") ?: "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupGoogleAuth()
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.authState.filterNotNull().stateIn(this)
                    .collect{state ->
                        handleAuthState(state,view)
                    }
            }
        }
        viewModel.navCommand.observe(viewLifecycleOwner, EventObserver{command ->
            findNavController().navigate(R.id.actionAuthToRegister)
        })
    }

    private fun handleAuthState(state: AuthUIState, view: View){
        resetErrorOnFields()
        when(state){
            is AuthUIState.Success -> {
                binding!!.authErrorTextView.text = ""
                (requireActivity() as CommonComponentsController).sendFirebaseToken(
                    sendAction = { token ->
                        viewModel.saveFirebaseToken(FirebaseTokenRequest(token))
                    }
                )
            }
            is AuthUIState.Error -> {
                val authTextInputLayoutEmail = binding!!.authTextInputLayoutEmail
                val authTextInputLayoutPassword =  binding!!.authTextInputLayoutPassword
                val authErrorTextView = binding!!.authErrorTextView
                when(val message = state.message){
                    Errors.NO_INTERNET_ERROR -> {
                        (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
                        viewModel.resetState()
                    }
                    Errors.EMPTY_INPUT_ERROR -> {
                        authErrorTextView.text = resources.getString(R.string.empty_input_error)
                        authTextInputLayoutEmail.error = " "
                        authTextInputLayoutPassword.error = " "
                    }
                    Errors.EMAIL_ERROR -> {
                        authErrorTextView.text = resources.getString(R.string.email_error)
                        authTextInputLayoutEmail.error = " "
                    }
                    Errors.PASSWORD_ERROR -> {
                        authErrorTextView.text = resources.getString(R.string.password_error)
                        authTextInputLayoutPassword.error = " "
                    }
                    Errors.NETWORK_ERROR -> authErrorTextView.text = resources.getString(R.string.unknown_error)
                    Errors.REFRESH_TOKEN_ERROR -> authErrorTextView.text = resources.getString(R.string.refresh_token_error)

                    else -> authErrorTextView.text = message

                }
                (AnimatorInflater.loadAnimator(requireContext(), R.animator.error_text_anim) as AnimatorSet).apply {
                    setTarget(authErrorTextView)
                    interpolator = LinearInterpolator()
                    start()
                }
            }
            is AuthUIState.NeedGoogleSignIn -> {
                googleSignIn()
                viewModel.resetState()
            }

            is AuthUIState.FirebaseTokenSent -> {
                findNavController().popBackStack(R.id.authFragment,true)
                (requireActivity() as AppNavigation).navigateToMenu()
            }
        }
    }

    private fun setupGoogleAuth(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun googleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val token = task.result.idToken
                token?.let {
                    viewModel.verifyGoogle(it)
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun resetErrorOnFields(){
        val authTextInputLayoutEmail = binding!!.authTextInputLayoutEmail
        val authTextInputLayoutPassword =  binding!!.authTextInputLayoutPassword
        authTextInputLayoutEmail.error = null
        authTextInputLayoutPassword.error = null
    }

    private fun setupToolbarUpButton(){
        val toolbarGraph = findNavController().createGraph(startDestination = R.id.authFragment){
            fragment<AuthFragment>(R.id.authFragment){}
        }
        val activity = requireActivity() as CommonComponentsController
        activity.clearToolbarMenu()
        activity.setupToolbarForInsideNavigation(subGraph = toolbarGraph)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    companion object{
        const val RC_GOOGLE_SIGN_IN = 1488
    }
}