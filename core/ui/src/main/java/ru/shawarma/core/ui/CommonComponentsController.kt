package ru.shawarma.core.ui

import androidx.navigation.NavGraph

interface CommonComponentsController {
    fun setupToolbarForInsideNavigation(subGraph: NavGraph)
}