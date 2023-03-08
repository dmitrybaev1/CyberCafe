package ru.shawarma.auth

sealed interface NavigationCommand{
    object ToRegisterFragment: NavigationCommand
}