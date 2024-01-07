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
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.*
import ru.shawarma.menu.adapters.CartAdapter
import ru.shawarma.menu.databinding.FragmentCartBinding
import ru.shawarma.menu.viewmodels.MenuViewModel
import ru.shawarma.menu.viewmodels.OrderUIState
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding? = null

    private val viewModel: MenuViewModel by hiltNavGraphViewModels(R.id.menu_nav_graph)

    private var cartAdapter: CartAdapter? = null

    private var isNavigationHandled = false

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
        savedInstanceState?.let {
            isNavigationHandled = it.getBoolean(IS_NAVIGATION_HANDLED_KEY)
        }
        val binding = binding!!
        cartAdapter = CartAdapter(viewModel)
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            addItemDecoration(AdaptiveSpacingItemDecoration(
                dpToPx(10f,requireContext()).roundToInt(),true))
            adapter = cartAdapter
        }
        val options = resources.getStringArray(R.array.payment_type_array)
        binding.cartPaymentDropdown.setText(options[0],false)
        viewModel.cartListLiveData.observe(viewLifecycleOwner){ list ->
            cartAdapter?.submitList(list)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.orderState.filterNotNull().stateIn(this).collect{ state ->
                    when(state){
                        is OrderUIState.Success -> {
                            val paymentType = binding.cartPaymentDropdown.text.toString()
                            if(paymentType != options[2]) {
                                //startOrderNotifications(state.orderId)
                                findNavController().popBackStack(R.id.menuFragment, false)
                                (requireActivity() as AppNavigation).navigateToOrder(state.orderId)
                            }
                            else{
                                //кинуть на оплату онлайн
                            }
                        }
                        is OrderUIState.Error -> {
                            if(state.message != Errors.NO_INTERNET_ERROR)
                                Snackbar.make(
                                    view,getString(R.string.create_order_error),Snackbar.LENGTH_LONG)
                                    .show()
                        }
                        is OrderUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.menuFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                    }
                    viewModel.resetOrderState()
                }
            }
        }
        viewModel.isDisconnectedToInternet.observe(viewLifecycleOwner, EventObserver{
            (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_NAVIGATION_HANDLED_KEY,isNavigationHandled)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    companion object{
        const val IS_NAVIGATION_HANDLED_KEY = "IsNavigationHandled"
    }
}