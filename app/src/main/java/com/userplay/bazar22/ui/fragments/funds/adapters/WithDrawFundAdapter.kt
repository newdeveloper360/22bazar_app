package com.userplay.bazar22.ui.fragments.funds.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.BlueprintFundDepositHistoryBinding
import com.userplay.bazar22.models.getWithDrawHistory.Data

class WithDrawFundAdapter(
    private val mContext: Context,
    private val mList: ArrayList<Data>
) : RecyclerView.Adapter<WithDrawFundAdapter.WithDrawFundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithDrawFundViewHolder {
        return WithDrawFundViewHolder(
            BlueprintFundDepositHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: WithDrawFundViewHolder, position: Int) {
        val item = mList[position]
        item.let {
            holder.binding(item)
        }
    }

    inner class WithDrawFundViewHolder(private val mBinding: BlueprintFundDepositHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mData: Data) {
            mBinding.apply {
                transactionDate.text = mData.createdAt
                tvAmount.text = mContext.getString(R.string.ruppes_symbol) + mData.amount.toString()
                tvRequestMode.text = mData.withdrawMode?.capitalize()
                transactionType.text = mData.requestType?.capitalize()

                if (mData.transactionId != null) {
                    tvTransId.text = mData.transactionId
                }
                else{
                    tvTransId.text = "XXYYZZ"
                }

                when (mData.status) {
                    "pending" -> {
                        statusIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.pending
                            )
                        )
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.yellow))
                    }
                    "failed" -> {
                        statusIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.canceled
                            )
                        )
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.pink))
                    }

                    "success" -> {
                        statusIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.done
                            )
                        )
                        status.setTextColor(ContextCompat.getColor(mContext, R.color.green))
                    }
                }

                status.text = mData.status?.capitalize()
            }
        }
    }

}