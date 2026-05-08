package com.userplay.bazar22.ui.fragments.mybids.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.BlueprintGameResultsBinding
import com.userplay.bazar22.models.get_result.GameResult

class MyBidsResultAdapter(private val mList: ArrayList<GameResult>) :
    RecyclerView.Adapter<MyBidsResultAdapter.MyBidsResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBidsResultViewHolder {
        return MyBidsResultViewHolder(
            BlueprintGameResultsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: MyBidsResultViewHolder, position: Int) {
        val item = mList[position]

        item.let {
            holder.binding(it)
        }
    }

    inner class MyBidsResultViewHolder(private val mBinding: BlueprintGameResultsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(mGameResult: GameResult) {
            mBinding.apply {
                title.text = mGameResult.market?.name
                number.text = mGameResult.result
            }
        }
    }
}