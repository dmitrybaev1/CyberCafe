package ru.shawarma.menu

sealed interface NavigationCommand{
    object ToCartFragment: NavigationCommand
    object ToMenuItemFragment: NavigationCommand
}