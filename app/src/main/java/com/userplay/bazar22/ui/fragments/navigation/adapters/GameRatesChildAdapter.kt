package com.userplay.bazar22.ui.fragments.navigation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.GamesRateChildItemviewBinding
import com.userplay.bazar22.models.get_rate_new.GameRates

class GameRatesChildAdapter(private val mGameTypeList: ArrayList<GameRates>) :
    RecyclerView.Adapter<GameRatesChildAdapter.GameRatesChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameRatesChildViewHolder {
        return GameRatesChildViewHolder(
            GamesRateChildItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGameTypeList.size

    override fun onBindViewHolder(holder: GameRatesChildViewHolder, position: Int) {
        val item = mGameTypeList[position]
        item.let {
            holder.binding(it)
        }
    }


    inner class GameRatesChildViewHolder(private val mBinding: GamesRateChildItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mGameRates  : GameRates) {
            mBinding.apply {
                tvGameType.text = mGameRates.name
                tvGameRate.text = "1 RS KA "+mGameRates.multiplyBy?.replace(".00","")+" Rs"
            }
        }
    }
}