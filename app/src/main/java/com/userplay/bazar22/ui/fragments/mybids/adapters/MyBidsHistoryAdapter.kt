package com.userplay.bazar22.ui.fragments.mybids.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.BlueprintBidHistoryBinding
import com.userplay.bazar22.models.get_game_history.Data

class MyBidsHistoryAdapter(
    private val mList: ArrayList<Data>,
    private val mContext: Context
) :
    RecyclerView.Adapter<MyBidsHistoryAdapter.MyBidsHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBidsHistoryViewHolder {
        return MyBidsHistoryViewHolder(
            BlueprintBidHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyBidsHistoryViewHolder, position: Int) {
        val item = mList[position]
        item.let {
            holder.binding(it)
        }
    }

    override fun getItemCount() = mList.size

    inner class MyBidsHistoryViewHolder(private val mBinding: BlueprintBidHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mData: Data) {
            mBinding.apply {
                if (mData.session != null) {
                    if (mData.gameType?.id != 2 && mData.gameType?.id != 6 && mData.gameType?.id != 7 && mData.gameType?.id != 8) {
                        tvGameType.text = mData.market?.name + " (" + mData.session + ")"
                    } else {
                        tvGameType.text = mData.market?.name
                    }
                } else {
                    tvGameType.text = mData.market?.name
                }
                tvPoints.text = mData.amount.toString()
                tvDigits.text = mData.number.toString()
                tvBidsDate.text = mData.date
                tvTransTime.text = mData.createdAt
                tvbidId.text = mData.gameString
                tvGameName.text = mData.gameType?.name

                when (mData.status) {
                    "PENDING" -> {
                        status.text = "Best Of luck "
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.status_red))
                        ivThumb.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.pending
                            )
                        )
                    }
                    "SUCCESS" -> {
                        status.text = "Congratulations. You Won (" + mData.winAmount + ")"
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.green))
                        ivThumb.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.thumb_up_new
                            )
                        )
                    }
                    "FAILED" -> {
                        status.text = "Better luck next time "
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.status_red))
                        ivThumb.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.thumb_down
                            )
                        )
                    }
                }
            }
        }
    }
}