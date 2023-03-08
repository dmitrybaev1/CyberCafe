package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.menu.adapters.CartAdapter
import ru.shawarma.menu.databinding.FragmentCartBinding
import ru.shawarma.menu.viewmodels.MenuViewModel

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}