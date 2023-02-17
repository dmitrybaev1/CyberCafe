package ru.shawarma.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.ui.CommonComponentsController

class MenuFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarUpButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu,container,false)
        val authData: AuthData? = arguments?.getParcelable("authData")
        return view
    }

    private fun setToolbarUpButton(){
        val toolbarGraph = findNavController().createGraph(startDestination = R.id.menuFragment){
            fragment<MenuFragment>(R.id.menuFragment){}
        }
        (requireActivity() as CommonComponentsController).setupToolbarForInsideNavigation(toolbarGraph)
    }
}