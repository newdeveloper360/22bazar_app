package com.userplay.bazar22.ui.fragments.funds.viewmodel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.userplay.bazar22.models.get_deposit_history.Data
import com.userplay.bazar22.network.ApiInterface

class GetDepositeHistoryPagingSource(private val mApiInterface: ApiInterface) :
      PagingSource<Int, Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        return try {
            val position = params.key ?: 1
            val response = mApiInterface.getDepositHistory(position)
            LoadResult.Page(
                data = response.body()?.response?.depositHistory!!.data,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.body()?.response?.depositHistory!!.lastPage) null else position + 1
            )

        } catch (e: Exception) {
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