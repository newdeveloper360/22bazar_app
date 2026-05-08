package com.userplay.bazar22.ui.fragments.navigation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.GameRatesParentItemviewBinding
import com.userplay.bazar22.models.get_rate_new.Data

class GameRateParentAdapter(private val mGameTypeList: ArrayList<Data>) :
    RecyclerView.Adapter<GameRateParentAdapter.GameRatesParentsViewHolder>() {


    private val GENERAL = 1
    private val DESAWAR = 2
    private val SATAR_LINE = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameRatesParentsViewHolder {
        return when (viewType) {
            GENERAL, DESAWAR, SATAR_LINE -> {
                GameRatesParentsViewHolder(
                    GameRatesParentItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                GameRatesParentsViewHolder(
                    GameRatesParentItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = mGameTypeList.size

    override fun onBindViewHolder(holder: GameRatesParentsViewHolder, position: Int) {
        when (holder.itemViewType) {
            GENERAL, DESAWAR, SATAR_LINE -> {
                mGameTypeList[position].let {
                    (holder as GameRatesParentsViewHolder).binding(it.title)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mGameTypeList[position].title) {
            "desawar" -> {
                DESAWAR
            }
            "start_line" -> {
                SATAR_LINE
            }
            "general" -> {
                GENERAL
            }
            else -> {
                GENERAL
            }
        }
    }


    inner class GameRatesParentsViewHolder(private val mBinding: GameRatesParentItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(title: String?) {
            mBinding.apply {

                tvTitle.text = title

                val mChildMembersAdapter = GameRatesChildAdapter(mGameTypeList[bindingAdapterPosition].list)
                itemRV.apply {
                    layoutManager = LinearLayoutManager(
                        mBinding.root.context, LinearLayoutManager.VERTICAL, false
                    )
                    adapter = mChildMembersAdapter
                }
            }
        }
    }
}