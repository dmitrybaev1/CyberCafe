package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.menu.databinding.FragmentMenuBinding
import ru.shawarma.menu.viewmodels.MenuUIState
import ru.shawarma.menu.viewmodels.MenuViewModel

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by activityViewModels()

    private var binding: FragmentMenuBinding? = null

    private var menuAdapter: MenuAdapter? = null

    private var isRequestInProgress = false

    private var isFullyLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authData: AuthData? = arguments?.getParcelable("authData")
        viewModel.setToken(authData!!)
        setupToolbarUpButton()
        inflateMenu()
        viewModel.navCommand.observe(this){
            findNavController().navigate(R.id.actionMenuToCart)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMenuBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenuRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.menuState.collect{state ->
                    when(state){
                        is MenuUIState.Success -> {
                            menuAdapter?.notifyDataSetChanged() ?: run {
                                menuAdapter = MenuAdapter(state.items,viewModel)
                                binding!!.menuRecyclerView.adapter = menuAdapter
                            }
                            if(state.isFullyLoaded)
                                isFullyLoaded = true
                        }
                        is MenuUIState.Error -> {
                            menuAdapter?.notifyDataSetChanged()
                        }
                        is MenuUIState.TokenInvalidError -> {
                            //выкинуть на авторизацию
                        }
                    }
                    isRequestInProgress = false
                }
            }
        }
    }

    private fun setupMenuRecyclerView(){
        val gridLayoutManager = GridLayoutManager(requireActivity(),2)
        gridLayoutManager.spanSizeLookup = object: SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int =
                if(menuAdapter?.getItemViewType(position) == R.layout.menu_item) MENU_ITEM_SPAN_SIZE
                else MENU_FULL_SPAN_SIZE
        }
        binding!!.menuRecyclerView.apply {
            layoutManager = gridLayoutManager
            addOnScrollListener(object: OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val position = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    if(!isFullyLoaded)
                        if(menuAdapter?.getItemViewType(position) == R.layout.menu_loading && !isRequestInProgress) {
                            isRequestInProgress = true
                            viewModel.getMenu()
                        }
                }
            })
        }

    }

    private fun setupToolbarUpButton(){
        val toolbarGraph = findNavController().createGraph(startDestination = R.id.menuFragment){
            fragment<MenuFragment>(R.id.menuFragment){}
        }
        (requireActivity() as CommonComponentsController).setupToolbarForInsideNavigation(subGraph = toolbarGraph)
    }

    private fun inflateMenu(){
        (requireActivity() as CommonComponentsController).inflateToolbarMenu(R.menu.menu_menu) {
             if(it.itemId == R.id.action_cart)
                 findNavController().navigate(R.id.actionMenuToCart)
             true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}