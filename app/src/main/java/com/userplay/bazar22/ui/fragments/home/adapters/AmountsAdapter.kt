package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.ItemAmountBinding
import com.userplay.bazar22.ui.callbacks.ItemClickListener

class AmountsAdapter(
    private val mList: ArrayList<Int>,
    private val mListener: ItemClickListener,
) : RecyclerView.Adapter<AmountsAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            ItemAmountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: GamesAddViewHolder, position: Int) {
        val item = mList[position]

        item.let {
            holder.binding(it)
        }
    }

    inner class GamesAddViewHolder(private val mBinding: ItemAmountBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(amount: Int) {
            mBinding.apply {
                tvAmount.text =amount.toString()
                cardItem.setOnClickListener {
                mListener.onItemClick(amount)
              }
            }
        }
    }
}