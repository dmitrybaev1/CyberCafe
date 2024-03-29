package ru.shawarma.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.*
import ru.shawarma.settings.adapters.InfoAdapter
import ru.shawarma.settings.databinding.FragmentInfoBinding
import ru.shawarma.settings.viewmodels.InfoUIState
import ru.shawarma.settings.viewmodels.InfoViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class InfoFragment : Fragment() {

    private val viewModel: InfoViewModel by hiltNavGraphViewModels(R.id.settings_nav_graph)

    private var binding: FragmentInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInfoBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.infoRecyclerView?.addItemDecoration(AdaptiveSpacingItemDecoration(
                dpToPx(10f,requireContext()).roundToInt(),true))
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.infoState.filterNotNull().stateIn(this).collect{ state ->
                    when(state){
                        is InfoUIState.Success -> {
                            binding?.infoRecyclerView?.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                val infoAdapter = InfoAdapter(state.items)
                                adapter = infoAdapter
                            }
                        }
                        is InfoUIState.Error -> {
                            Snackbar.make(view,R.string.info_error,Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry){
                                    viewModel.getInfo()
                                }.show()

                        }
                        is InfoUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.profileFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                        is InfoUIState.LoggedOut -> {
                            findNavController().popBackStack(R.id.profileFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth()
                        }
                    }
                }
            }
        }

        binding?.infoExitButton?.setOnClickListener {
            (requireActivity() as CommonComponentsController).deleteFirebaseToken()
            viewModel.resetAuth()
        }

        viewModel.isDisconnectedToInternet.observe(viewLifecycleOwner, EventObserver{
            (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}