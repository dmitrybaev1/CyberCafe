package ru.shawarma.menu

sealed interface NavigationCommand{
    object ToCart: NavigationCommand
}