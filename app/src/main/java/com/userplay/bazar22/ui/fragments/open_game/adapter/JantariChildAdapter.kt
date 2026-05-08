package com.userplay.bazar22.ui.fragments.open_game.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.BlueprintJantriItemBinding
import com.userplay.bazar22.models.jantri_request.JantriDetailExpo
import com.userplay.bazar22.ui.fragments.open_game.callback.JantariListener
import java.util.ArrayList

class JantariChildAdapter(
    private var mJantriDetailExpo: ArrayList<JantriDetailExpo>?,
    private val mListener: JantariListener) : RecyclerView.Adapter<JantariChildAdapter.JantariChildViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JantariChildViewHolder {
        return JantariChildViewHolder(
            BlueprintJantriItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JantariChildViewHolder, position: Int) {
        val listSize = mJantriDetailExpo?.size ?: 0

        if (position < listSize) {
            val item = mJantriDetailExpo?.get(position)
            item.let {
                holder.binding(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mJantriDetailExpo != null) {
            mJantriDetailExpo!!.size
        } else {
            return 0
        }
    }


    inner class JantariChildViewHolder(private val mBinding: BlueprintJantriItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(mJantriDetailExpo: JantriDetailExpo?) {

            mJantriDetailExpo?.let { data ->
                mBinding.apply {
                    Log.e("data from jantari",""+mJantriDetailExpo.jantriNumber)
                    tvNumber.text = mJantriDetailExpo.jantriNumber
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
                            Log.e("itemclicked","clicked")
                            val amount = edAmount.text.toString().trim()
                            if (amount.isNotEmpty()) {
                                mListener.onTextAddListener(
                                    amount,
                                    mJantriDetailExpo.jantriNumber,
                                    bindingAdapterPosition,
                                    1,
                                    2
                                )
                            } else {
                                mListener.onTextRemoveListener(
                                    amount,
                                    mJantriDetailExpo.jantriNumber,
                                    bindingAdapterPosition,
                                    1
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
}