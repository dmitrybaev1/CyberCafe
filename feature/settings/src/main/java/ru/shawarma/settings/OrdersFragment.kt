package ru.shawarma.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private val viewModel: OrdersViewModel by viewModels()

    private var binding: FragmentOrdersBinding? = null

    private var ordersAdapter: OrdersAdapter? = null

    private var isRequestInProgress = false

    private var isFullyLoaded = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("ordersFragment","onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ordersFragment","onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ordersFragment","onCreateView")
        val binding = FragmentOrdersBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOrdersRecyclerView()
        viewModel.navCommand.observe(viewLifecycleOwner){ command ->
            if(command is NavigationCommand.ToOrderModule)
                (requireActivity() as AppNavigation).navigateToOrder(command.orderId)
        }
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
        Log.d("ordersFragment","onDestroyView")
        binding = null
        ordersAdapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ordersFragment","onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("ordersFragment","onDetach")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ordersFragment","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ordersFragment","onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ordersFragment","onResume")
    }
}