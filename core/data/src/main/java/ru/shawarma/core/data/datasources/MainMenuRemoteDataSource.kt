package ru.shawarma.core.data.datasources

import kotlinx.coroutines.CoroutineDispatcher
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.services.MenuService
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.safeServiceCall
import javax.inject.Inject

class MainMenuRemoteDataSource @Inject constructor(
    private val menuService: MenuService,
    private val dispatcher: CoroutineDispatcher
): MenuRemoteDataSource {
    override suspend fun getMenu(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<MenuItemResponse>> =
        safeServiceCall(dispatcher){
            menuService.getMenu(token, offset, count)
        }

    override suspend fun getMenuItem(token: String, id: Long): Result<MenuItemResponse> =
        safeServiceCall(dispatcher){
            menuService.getMenuItem(token, id)
        }

}