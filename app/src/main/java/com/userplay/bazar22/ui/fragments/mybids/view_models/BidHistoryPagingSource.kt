package com.userplay.bazar22.ui.fragments.mybids.view_models

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.userplay.bazar22.models.get_game_history.Data
import com.userplay.bazar22.network.ApiInterface

class BidHistoryPagingSource(private val mApiInterface: ApiInterface, private val type: String) :
    PagingSource<Int, Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        return try {
            val position = params.key ?: 1
            val response = mApiInterface.getGameHistory(type = type, position)
            LoadResult.Page(
                data = response.body()?.response?.gameHistory!!.data,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.body()?.response?.gameHistory!!.lastPage) null else position + 1

            )
        } catch (e: java.lang.Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus((1))
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }


}