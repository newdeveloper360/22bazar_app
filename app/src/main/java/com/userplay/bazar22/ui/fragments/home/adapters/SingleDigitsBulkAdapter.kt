package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.SingleDigitsBulkItemBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.ui.callbacks.ItemClickListener

class SingleDigitsBulkAdapter(
    private var mGameList: ArrayList<Game>,
    private val mListener: ItemClickListener
) : RecyclerView.Adapter<SingleDigitsBulkAdapter.SingleDigitsBulkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleDigitsBulkViewHolder {
        return SingleDigitsBulkViewHolder(
            SingleDigitsBulkItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGameList.size

    override fun onBindViewHolder(holder: SingleDigitsBulkViewHolder, position: Int) {
        val item = mGameList[position]

        item.let {
            holder.binding(it)
        }
    }

    inner class SingleDigitsBulkViewHolder(private val mBinding: SingleDigitsBulkItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(mGame: Game) {

            mBinding.apply {

                tvNumber.text = mGame.number
                tvAmount.text = mGame.amount.toString()

                root1.setOnClickListener {
                    mListener.onItemClick(bindingAdapterPosition)
                }

            }
        }
    }
}