package ru.shawarma.settings

import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.AdaptiveSpacingItemDecoration
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.core.ui.dpToPx
import ru.shawarma.settings.adapters.OrdersAdapter
import ru.shawarma.settings.databinding.FragmentOrdersBinding
import ru.shawarma.settings.viewmodels.OrdersUIState
import ru.shawarma.settings.viewmodels.OrdersViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by hiltNavGraphViewModels(R.id.settings_nav_graph)

    private var binding: FragmentOrdersBinding? = null

    private var ordersAdapter: OrdersAdapter? = null

    private var isRequestInProgress = false

    private var isFullyLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navCommand.observe(this){ command ->
            if(command is NavigationCommand.ToOrderModule)
                (requireActivity() as AppNavigation).navigateToOrder(command.orderId)
        }
    }

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
        setupOrdersRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.ordersState.collect{ state ->
                    when(state){
                        is OrdersUIState.Success -> {
                            ordersAdapter?.submitList(state.items)
                            isFullyLoaded = state.isFullyLoaded
                        }
                        is OrdersUIState.Error -> {
                            val items = state.items
                            ordersAdapter?.submitList(state.items)
                            binding!!.ordersRecyclerView.scrollToPosition(items.size-1)
                        }
                        is OrdersUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.profileFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                    }
                    binding!!.ordersSwipeRefreshLayout.isRefreshing = false
                    isRequestInProgress = false
                }
            }
        }
        viewModel.isDisconnectedToInternet.observe(viewLifecycleOwner){ isDisconnected ->
            if(isDisconnected){
                (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
                binding!!.ordersSwipeRefreshLayout.isRefreshing = false
                viewModel.resetNoInternetState()
            }
        }
        binding!!.ordersSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshOrders()
        }
    }

    private fun setupOrdersRecyclerView(){
        ordersAdapter = OrdersAdapter(viewModel)
        binding!!.ordersRecyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(AdaptiveSpacingItemDecoration(
                dpToPx(5f,requireContext()).roundToInt(),true))
            addOnScrollListener(object: OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val position = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if(!isFullyLoaded)
                        if(ordersAdapter?.getItemViewType(position) == R.layout.order_loading && !isRequestInProgress){
                            isRequestInProgress = true
                            viewModel.getOrders()
                        }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        ordersAdapter = null
    }
}