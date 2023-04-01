package ru.shawarma.settings

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.settings.adapters.OrdersAdapter
import ru.shawarma.settings.databinding.FragmentOrdersBinding
import ru.shawarma.settings.viewmodels.OrdersUIState
import ru.shawarma.settings.viewmodels.OrdersViewModel

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
        setupOrdersRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.ordersState.collect{ state ->
                    when(state){
                        is OrdersUIState.Success -> {
                            ordersAdapter?.setList(state.items)
                            ordersAdapter?.notifyDataSetChanged()
                            if(state.isFullyLoaded)
                                isFullyLoaded = true
                        }
                        is OrdersUIState.Error -> {
                            val items = state.items
                            ordersAdapter?.setList(state.items)
                            ordersAdapter?.notifyDataSetChanged()
                            binding!!.ordersRecyclerView.scrollToPosition(items.size-1)
                        }
                        is OrdersUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.profileFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                    }
                    isRequestInProgress = false
                }
            }
        }
    }

    private fun setupOrdersRecyclerView(){
        ordersAdapter = OrdersAdapter(viewModel)
        binding!!.ordersRecyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext())
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