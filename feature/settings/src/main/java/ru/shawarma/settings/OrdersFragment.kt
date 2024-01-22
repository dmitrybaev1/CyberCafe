package ru.shawarma.settings

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.*
import ru.shawarma.settings.adapters.OrdersAdapter
import ru.shawarma.settings.adapters.OrdersLoadStateAdapter
import ru.shawarma.settings.databinding.FragmentOrdersBinding
import ru.shawarma.settings.viewmodels.OrdersViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by hiltNavGraphViewModels(R.id.settings_nav_graph)

    private var binding: FragmentOrdersBinding? = null

    private var ordersAdapter: OrdersAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOrdersBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.ordersSwipeRefreshLayout.apply {
            setColorSchemeColors(MaterialColors.getColor(requireContext(),
                com.google.android.material.R.attr.colorPrimary, Color.BLACK))
            setProgressBackgroundColorSchemeColor(MaterialColors.getColor(requireContext(),
               android.R.attr.colorBackground, Color.WHITE))
        }
        binding!!.ordersSwipeRefreshLayout.setOnRefreshListener {
            ordersAdapter?.refresh()
        }
        setupOrdersRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch{
            launch {
                viewModel.ordersFlow.collectLatest { pagingData ->
                    ordersAdapter?.submitData(pagingData)
                }
            }
            launch {
                ordersAdapter?.loadStateFlow?.collectLatest {loadStates ->
                    val itemCount = ordersAdapter?.itemCount ?: 0
                    viewModel.isOrdersListEmpty.value =
                        loadStates.append is LoadState.NotLoading &&
                                loadStates.append.endOfPaginationReached && itemCount < 1
                    val states = listOf(loadStates.refresh,loadStates.append,loadStates.prepend)
                    val loadingStates = states.filterIsInstance<LoadState.Loading>()
                    val errorStates = states.filterIsInstance<LoadState.Error>()
                    if(loadingStates.isEmpty()){
                        binding!!.ordersSwipeRefreshLayout.isRefreshing = false
                    }
                    if(errorStates.isNotEmpty()) {
                        binding!!.ordersSwipeRefreshLayout.isRefreshing = false
                        val state = errorStates[0]
                        if (state.error.message == Errors.UNAUTHORIZED_ERROR
                            || state.error.message == Errors.REFRESH_TOKEN_ERROR
                        ) {
                            findNavController().popBackStack(R.id.profileFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                        else if(state.error.message == Errors.NO_INTERNET_ERROR){
                            (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
                        }
                    }
                }
            }
        }

        viewModel.isDisconnectedToInternet.observe(viewLifecycleOwner, EventObserver{
            (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
            binding!!.ordersSwipeRefreshLayout.isRefreshing = false
        })

        viewModel.navCommand.observe(viewLifecycleOwner,EventObserver{ command ->
            if(command is NavigationCommand.ToOrderModule)
                (requireActivity() as AppNavigation).navigateToOrder(command.orderId)
        })
    }

    private fun setupOrdersRecyclerView(){
        ordersAdapter = OrdersAdapter(viewModel)
        val _adapter = ordersAdapter!!.withLoadStateFooter(OrdersLoadStateAdapter(ordersAdapter!!::retry))
        binding!!.ordersRecyclerView.apply {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(AdaptiveSpacingItemDecoration(
                dpToPx(5f,requireContext()).roundToInt(),true))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        ordersAdapter = null
    }
}