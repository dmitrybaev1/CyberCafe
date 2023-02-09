package ru.shawarma.auth

sealed interface NavigationCommand{
    object ToRegister: NavigationCommand
}