package com.userplay.bazar22.ui.fragments.funds.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.BlueprintFundDepositHistoryBinding
import com.userplay.bazar22.models.get_deposit_history.Data

class DepositFundAdapter(
    private val mContext: Context,
    private val mList: ArrayList<Data>
) : RecyclerView.Adapter<DepositFundAdapter.DepositFundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositFundViewHolder {
        return DepositFundViewHolder(
            BlueprintFundDepositHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DepositFundViewHolder, position: Int) {
        val item = mList[position]
        item.let {
            holder.binding(it)
        }
    }

    override fun getItemCount() = mList.size

    inner class DepositFundViewHolder(private val mBinding: BlueprintFundDepositHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mData: Data) {
            mBinding.apply {
                transactionDate.text = mData.createdAt
                tvAmount.text = mContext.getString(R.string.ruppes_symbol) + mData.amount.toString()
                tvRequestMode.text = mData.depositMode?.capitalize()
                if (mData.transactionId != null) {
                    tvTransId.text = mData.transactionId
                }
                transactionType.text = mData.requestType?.capitalize()


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