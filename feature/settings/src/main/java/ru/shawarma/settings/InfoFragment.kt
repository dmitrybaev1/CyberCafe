package ru.shawarma.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.settings.adapters.InfoAdapter
import ru.shawarma.settings.databinding.FragmentInfoBinding
import ru.shawarma.settings.viewmodels.InfoUIState
import ru.shawarma.settings.viewmodels.InfoViewModel

@AndroidEntryPoint
class InfoFragment : Fragment() {

    private val viewModel: InfoViewModel by hiltNavGraphViewModels(R.id.settings_nav_graph)

    private var binding: FragmentInfoBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("infoFragment","onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("infoFragment","onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("infoFragment","onCreateView")
        val binding = FragmentInfoBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.infoState.filterNotNull().stateIn(this).collect{ state ->
                    when(state){
                        is InfoUIState.Success -> {
                            Log.d("infoFragment",state.items.size.toString())
                            binding?.infoRecyclerView?.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                val infoAdapter = InfoAdapter(state.items)
                                adapter = infoAdapter
                                //infoAdapter.notifyDataSetChanged()
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
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.d("infoFragment","onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("infoFragment","onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("infoFragment","onDetach")
    }

    override fun onPause() {
        super.onPause()
        Log.d("infoFragment","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("infoFragment","onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d("infoFragment","onResume")
    }
}