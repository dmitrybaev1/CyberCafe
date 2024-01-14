package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.core.ui.loadImage
import ru.shawarma.menu.databinding.FragmentMenuItemBinding
import ru.shawarma.menu.viewmodels.MenuViewModel

@AndroidEntryPoint
class MenuItemFragment : Fragment() {

    private val viewModel: MenuViewModel by hiltNavGraphViewModels(R.id.menu_nav_graph)

    private var binding: FragmentMenuItemBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflateMenu()
        val binding = FragmentMenuItemBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = binding!!
        val menuItem = viewModel.chosenMenuItem.value!!
        binding.menuItemFragmentCartQuantityControlView.count = viewModel.getMenuItemCount(menuItem)
        menuItem.imageUrl?.let { binding.menuItemFragmentImageView.loadImage(it,requireContext()) }
        binding.menuItemFragmentAddToCartButton.setOnClickListener {
            viewModel.addToCart(menuItem)
            binding.menuItemFragmentCartQuantityControlView.count = viewModel.getMenuItemCount(menuItem)
        }
        binding.menuItemFragmentCartQuantityControlView.setOnMinusClickListener {
            viewModel.removeFromCart(viewModel.chosenMenuItem.value!!)
            binding.menuItemFragmentCartQuantityControlView.count = viewModel.getMenuItemCount(menuItem)
        }
        binding.menuItemFragmentCartQuantityControlView.setOnPlusClickListener {
            viewModel.addToCart(viewModel.chosenMenuItem.value!!)
            binding.menuItemFragmentCartQuantityControlView.count = viewModel.getMenuItemCount(menuItem)
        }
    }

    private fun inflateMenu(){
        (requireActivity() as CommonComponentsController).inflateToolbarMenu(R.menu.menu_item_menu) {
            if(it.itemId == R.id.action_cart)
                findNavController().navigate(R.id.actionMenuItemToCart)
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}