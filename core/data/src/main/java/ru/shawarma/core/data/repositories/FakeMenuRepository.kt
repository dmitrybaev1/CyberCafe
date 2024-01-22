package ru.shawarma.core.data.repositories

import android.util.Log
import kotlinx.coroutines.delay
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

class FakeMenuRepository @Inject constructor(): MenuRepository {

    override suspend fun getMenu(offset: Int, count: Int): Result<List<MenuItemResponse>> {
        val menuList = arrayListOf<MenuItemResponse>()
        val time = System.currentTimeMillis()
        Log.d("TAG","invoking get menu, offset: $offset, count: $count")
        return if(offset>10) {
            delay(5000)
            /*Result.Success(
                listOf(
                    MenuItemResponse(
                        id = time,
                        name = "Shawarma $time",
                        price = 200,
                        imageUrl = null,
                        description = null,
                        visible = true
                    )
                )
            )*/
            Result.Failure("Test Error")
        }
        else{
            repeat(count){
                menuList.add(
                    MenuItemResponse(
                        id = time,
                        name = "Shawarma $time",
                        price = 200,
                        imageUrl = null,
                        description = null,
                        visible = true
                    )
                )
            }
            Result.Success(menuList)
        }
    }

    override suspend fun getMenuItem(id: Long): Result<MenuItemResponse> {
        return Result.Success(
            MenuItemResponse(
                id = id,
                name = "Shawarma $id",
                price = 200,
                imageUrl = null,
                description = null,
                visible = true
            )
        )
    }

}