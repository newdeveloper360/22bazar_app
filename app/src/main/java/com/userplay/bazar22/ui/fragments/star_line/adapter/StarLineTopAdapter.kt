package com.userplay.bazar22.ui.fragments.star_line.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.StarlineTopItemviewBinding
import com.userplay.bazar22.models.get_rate_new.GameRates

class StarLineTopAdapter(private val mGameTypeList: ArrayList<GameRates>) :
    RecyclerView.Adapter<StarLineTopAdapter.StarLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarLineViewHolder {
        return StarLineViewHolder(
            StarlineTopItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGameTypeList.size

    override fun onBindViewHolder(holder: StarLineViewHolder, position: Int) {
        val item = mGameTypeList[position]

        item.let {
            holder.binding(it)
        }
    }

    inner class StarLineViewHolder(private val mBinding: StarlineTopItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mGameRates: GameRates) {
            mBinding.apply {
                tvName.text = mGameRates.name
                tvDigits.text ="1 - " +mGameRates.multiplyBy?.replace(".00","")
            }
        }
    }
}