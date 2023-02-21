package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.menu.viewmodels.MenuViewModel

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var menuNavController: NavController? = null

    private val viewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu,container,false)
        val authData: AuthData? = arguments?.getParcelable("authData")
        viewModel.setToken(authData!!)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navHostFragment = parentFragmentManager.findFragmentById(R.id.menuFragmentContainerView) as NavHostFragment
        menuNavController = navHostFragment.navController
        setToolbar(menuNavController!!)
    }

    private fun setToolbar(navController: NavController){
        (requireActivity() as CommonComponentsController).setupToolbarForInsideNavigation(navController,navController.graph)
    }
}