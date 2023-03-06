package ru.shawarma.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.menu.adapters.MenuAdapter
import ru.shawarma.menu.databinding.FragmentMenuBinding
import ru.shawarma.menu.utlis.MENU_FULL_SPAN_SIZE
import ru.shawarma.menu.utlis.MENU_ITEM_SPAN_SIZE
import ru.shawarma.menu.utlis.PlaceholderStringType
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
        Log.d("menuFragment","onCreate")
        viewModel.navCommand.observe(this){navCommand ->
            when(navCommand){
                NavigationCommand.ToCartFragment -> findNavController().navigate(R.id.actionMenuToCart)
                NavigationCommand.ToMenuItemFragment -> findNavController().navigate(R.id.actionMenuToMenuItem)
            }
        }
        viewModel.getPlaceholderString.observeForever{map ->
            Log.d("menuFragment","getPlaceholderString")
            if(map.containsKey(PlaceholderStringType.ORDER_WITH_DETAILS)){
                val intParam = map[PlaceholderStringType.ORDER_WITH_DETAILS]?.get(0) as Int
                val text = requireContext().getString(R.string.order_with_details,intParam)
                viewModel.setFormattedString(
                    PlaceholderStringType.ORDER_WITH_DETAILS,
                    HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }
            if(map.containsKey(PlaceholderStringType.TOTAL_PRICE)){
                val intParam = map[PlaceholderStringType.TOTAL_PRICE]?.get(0) as Int
                val text = requireContext().getString(R.string.total_price,intParam)
                viewModel.setFormattedString(PlaceholderStringType.TOTAL_PRICE,text)
            }
        }
        val authData: AuthData? = arguments?.getParcelable("authData")
        viewModel.setToken(authData!!)
        viewModel.getMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupToolbarUpButton()
        inflateMenu()
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
                            menuAdapter?.setList(state.items)
                            menuAdapter?.notifyDataSetChanged()
                            if(state.isFullyLoaded)
                                isFullyLoaded = true
                        }
                        is MenuUIState.Error -> {
                            val items = state.items
                            menuAdapter?.setList(items)
                            menuAdapter?.notifyDataSetChanged()
                            binding!!.menuRecyclerView.scrollToPosition(items.size-1)
                        }
                        is MenuUIState.TokenInvalidError -> {
                            Log.d("menuFragment","token invalid")
                        }
                    }
                    isRequestInProgress = false
                }
            }
        }
    }

    private fun setupMenuRecyclerView(){
        menuAdapter = MenuAdapter(viewModel)
        val gridLayoutManager = GridLayoutManager(requireActivity(),2)
        gridLayoutManager.spanSizeLookup = object: SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int =
                if(menuAdapter?.getItemViewType(position) == R.layout.menu_item) MENU_ITEM_SPAN_SIZE
                else MENU_FULL_SPAN_SIZE
        }
        binding!!.menuRecyclerView.apply {
            adapter = menuAdapter
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