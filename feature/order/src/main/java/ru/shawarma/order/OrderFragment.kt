package ru.shawarma.order

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.order.adapters.OrderMenuItemAdapter
import ru.shawarma.order.databinding.FragmentOrderBinding
import ru.shawarma.order.viewmodels.OrderUIState
import ru.shawarma.order.viewmodels.OrderViewModel

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private val viewModel: OrderViewModel by viewModels()

    private var binding: FragmentOrderBinding? = null

    private var id: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clearMenu()
        id = arguments?.getInt("orderId")
        viewModel.getOrder(id!!)
        val binding = FragmentOrderBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.orderState.collect{state ->
                    when(state){
                        is OrderUIState.Loading -> {}
                        is OrderUIState.Success -> {
                            val binding = binding!!
                            binding.orderMenuItemsRecyclerView.apply {
                                val orderMenuItemAdapter = OrderMenuItemAdapter(state.order.menuItems)
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = orderMenuItemAdapter
                            }
                            when(state.order.status){
                                OrderStatus.IN_QUEUE -> {
                                    viewModel.setStatus(getString(R.string.in_queue))
                                    binding.orderStepIndicatorImageView.setImageResource(
                                        ru.shawarma.core.ui.R.drawable.step_indicator_1)
                                    binding.orderClosedTextView.text = getString(R.string.missed)
                                }
                                OrderStatus.COOKING -> {
                                    viewModel.setStatus(getString(R.string.cooking))
                                    binding.orderStepIndicatorImageView.setImageResource(
                                        ru.shawarma.core.ui.R.drawable.step_indicator_2)
                                    binding.orderClosedTextView.text = getString(R.string.missed)
                                }
                                OrderStatus.READY -> {
                                    viewModel.setStatus(getString(R.string.ready))
                                    binding.orderStepIndicatorImageView.setImageResource(
                                        ru.shawarma.core.ui.R.drawable.step_indicator_3)
                                    binding.orderClosedTextView.text = getString(R.string.missed)
                                }
                                OrderStatus.CLOSED -> {
                                    viewModel.setStatus(getString(R.string.closed))
                                    binding.orderStepIndicatorImageView.setImageResource(
                                        ru.shawarma.core.ui.R.drawable.step_indicator_4)
                                    binding.orderClosedTextView.text = viewModel.closedDate.value
                                }
                                OrderStatus.CANCELED -> {
                                    viewModel.setStatus(getString(R.string.canceled))
                                    binding.orderStepIndicatorImageView.setImageResource(
                                        ru.shawarma.core.ui.R.drawable.step_indicator_cancel)
                                    binding.orderClosedTextView.text = viewModel.closedDate.value
                                }
                            }
                        }
                        is OrderUIState.Error -> {
                            val id = id!!
                            binding!!.orderRetryButton.setOnClickListener {
                                viewModel.getOrder(id)
                            }
                        }
                        is OrderUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.orderFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                    }
                }
            }
        }
    }
    
    private fun clearMenu(){
        (requireActivity() as CommonComponentsController).clearToolbarMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}