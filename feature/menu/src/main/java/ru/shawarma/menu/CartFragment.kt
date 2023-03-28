package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.menu.adapters.CartAdapter
import ru.shawarma.menu.databinding.FragmentCartBinding
import ru.shawarma.menu.viewmodels.MenuViewModel
import ru.shawarma.menu.viewmodels.OrderUIState

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding? = null

    private val viewModel: MenuViewModel by hiltNavGraphViewModels(R.id.menu_nav_graph)

    private var cartAdapter: CartAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as CommonComponentsController).clearToolbarMenu()
        val binding = FragmentCartBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = binding!!
        cartAdapter = CartAdapter(viewModel)
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = cartAdapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),R.array.payment_type_array,android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.cartPaymentSpinner.adapter = adapter
            }
        viewModel.cartListLiveData.observe(viewLifecycleOwner){ list ->
            cartAdapter?.setList(list)
            cartAdapter?.notifyDataSetChanged()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.orderState.filterNotNull().stateIn(this).collect{ state ->
                    when(state){
                        is OrderUIState.Success -> {
                            findNavController().popBackStack(R.id.menuFragment,false)
                            (requireActivity() as AppNavigation).navigateToOrder(state.orderId)
                        }
                        is OrderUIState.Error -> {
                            Snackbar.make(
                                view,getString(R.string.create_order_error),Snackbar.LENGTH_LONG)
                                .show()
                        }
                        is OrderUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.menuFragment,true)
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
    }
}