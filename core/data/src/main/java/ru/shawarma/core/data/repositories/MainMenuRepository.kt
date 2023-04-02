package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.datasources.MenuRemoteDataSource
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import javax.inject.Inject

class MainMenuRepository @Inject constructor(
    private val menuRemoteDataSource: MenuRemoteDataSource,
    private val authRepository: AuthRepository
): MenuRepository {

    override suspend fun getMenu(offset: Int, count: Int): Result<List<MenuItemResponse>> =
        when(val result = authRepository.getActualAuthData()){
            is Result.Success<AuthData> ->
                menuRemoteDataSource.getMenu("Bearer ${result.data.accessToken}", offset, count)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }

    override suspend fun getMenuItem(id: Long): Result<MenuItemResponse> =
        when(val result = authRepository.getActualAuthData()) {
            is Result.Success<AuthData> ->
                menuRemoteDataSource.getMenuItem("Bearer ${result.data.accessToken}", id)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
}