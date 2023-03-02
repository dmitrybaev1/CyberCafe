package ru.shawarma.core.ui

import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.navigation.NavController
import androidx.navigation.NavGraph

interface CommonComponentsController {
    fun setupToolbarForInsideNavigation(navController: NavController? = null,subGraph: NavGraph)

    fun inflateToolbarMenu(@MenuRes menuRes: Int,onMenuItemClickListener: OnMenuItemClickListener)
}