package ru.shawarma.core.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class CommonPagingSource<T : Any>(
    private val loadPage: suspend (page: Int, pageSize: Int) -> Result<List<T>>
): PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 0
        return when(val response = loadPage(currentPage, params.loadSize)) {
            is Result.Success -> {
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = if (response.data.size < params.loadSize) null else currentPage + 1
                )
            }
            is Result.Failure -> {
                LoadResult.Error(Exception(response.message))
            }
            is Result.NetworkFailure -> {
                LoadResult.Error(Exception(Errors.NETWORK_ERROR))
            }
        }
    }
}