package ru.shawarma.auth.navigation

sealed interface NavigationCommand{
    object ToRegister: NavigationCommand
}