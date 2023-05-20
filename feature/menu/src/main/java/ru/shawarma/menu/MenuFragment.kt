package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.ui.*
import ru.shawarma.menu.adapters.MenuAdapter
import ru.shawarma.menu.databinding.FragmentMenuBinding
import ru.shawarma.menu.utlis.MENU_FULL_SPAN_SIZE
import ru.shawarma.menu.utlis.MENU_ITEM_SPAN_SIZE
import ru.shawarma.menu.utlis.PlaceholderStringType
import ru.shawarma.menu.viewmodels.MenuUIState
import ru.shawarma.menu.viewmodels.MenuViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by hiltNavGraphViewModels(R.id.menu_nav_graph)

    private var binding: FragmentMenuBinding? = null

    private var menuAdapter: MenuAdapter? = null

    private var isRequestInProgress = false

    private var isFullyLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPlaceholderString.observeForever{map ->
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
        savedInstanceState?.let {
            isRequestInProgress = it.getBoolean(IS_REQUEST_IN_PROGRESS_KEY)
        }
        setupMenuRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.menuState.collect{state ->
                    when(state){
                        is MenuUIState.Success -> {
                            menuAdapter?.submitList(state.items)
                            isFullyLoaded = state.isFullyLoaded
                        }
                        is MenuUIState.Error -> {
                            val items = state.items
                            menuAdapter?.submitList(items)
                            binding!!.menuRecyclerView.scrollToPosition(items.size-1)
                        }
                        is MenuUIState.TokenInvalidError -> {
                            findNavController().popBackStack(R.id.menuFragment,true)
                            (requireActivity() as AppNavigation).navigateToAuth(Errors.REFRESH_TOKEN_ERROR)
                        }
                    }
                    isRequestInProgress = false
                }
            }
        }
        viewModel.isDisconnectedToInternet.observe(viewLifecycleOwner, EventObserver{
            (requireActivity() as CommonComponentsController).showNoInternetSnackbar(view)
        })
        viewModel.navCommand.observe(viewLifecycleOwner, EventObserver{navCommand ->
            when(navCommand){
                NavigationCommand.ToCartFragment -> findNavController().navigate(R.id.actionMenuToCart)
                NavigationCommand.ToMenuItemFragment -> findNavController().navigate(R.id.actionMenuToMenuItem)
            }
        })
    }

    private fun setupMenuRecyclerView(){
        menuAdapter = MenuAdapter(viewModel)
        val gridLayoutManager = GridLayoutManager(requireContext(),2)
        gridLayoutManager.spanSizeLookup = object: SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int =
                if(menuAdapter?.getItemViewType(position) == R.layout.menu_item) MENU_ITEM_SPAN_SIZE
                else MENU_FULL_SPAN_SIZE
        }
        binding!!.menuRecyclerView.apply {
            adapter = menuAdapter
            layoutManager = gridLayoutManager
            addItemDecoration(AdaptiveSpacingItemDecoration(
                dpToPx(5f,requireContext()).roundToInt(),true))
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
             when(it.itemId){
                 R.id.action_cart -> findNavController().navigate(R.id.actionMenuToCart)
                 R.id.action_settings -> (requireActivity() as AppNavigation).navigateToSettings()
             }
             true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_REQUEST_IN_PROGRESS_KEY,isRequestInProgress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        menuAdapter = null
    }
    companion object{
        const val IS_REQUEST_IN_PROGRESS_KEY = "IsRequestInProgress"
    }
}