package ru.shawarma.core.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraph

interface CommonComponentsController {
    fun setupToolbarForInsideNavigation(navController: NavController? = null,subGraph: NavGraph)
}