package com.userplay.bazar22.ui.fragments.open_game.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.BlueprintJantriItemBinding
import com.userplay.bazar22.models.jantari_model.Number
import com.userplay.bazar22.ui.fragments.open_game.callback.JantariListener

class JantariChild2Adapter(
    private val mNumberList: ArrayList<Number>,
    private val mListener: JantariListener
) :
    RecyclerView.Adapter<JantariChild2Adapter.JantariChild2ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JantariChild2ViewHolder {
        return JantariChild2ViewHolder(
            BlueprintJantriItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mNumberList.size

    override fun onBindViewHolder(holder: JantariChild2ViewHolder, position: Int) {
        val item = mNumberList[position]
        item.let {
            holder.binding(it)
        }
    }


    inner class JantariChild2ViewHolder(private val mBinding: BlueprintJantriItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mNumber: Number) {
            mBinding.apply {
                tvNumber.text = mNumber.number

                edAmount.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        Log.e("itemclicked", "clicked")
                        val amount = edAmount.text.toString().trim()
                        if (amount.isNotEmpty()) {
                            mListener.onTextAddListener(
                                amount,
                                mNumber.number,
                                bindingAdapterPosition,
                                mNumber.id.toInt(),
                                mNumber.gameTypeId

                            )
                        } else {
                            mListener.onTextRemoveListener(
                                amount,
                                mNumber.number,
                                bindingAdapterPosition,
                                mNumber.id.toInt()
                            )
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }
        }
    }
}