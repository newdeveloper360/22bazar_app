package com.userplay.bazar22.ui.fragments.open_game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.JantariParentItemViewBinding
import com.userplay.bazar22.models.jantri_request.JantriExpoSummury
import com.userplay.bazar22.ui.fragments.open_game.callback.JantariListener

class JantriAdapter(
    private val mJantriSummuryList: ArrayList<JantriExpoSummury>,
    private val mListener: JantariListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val TYPE_JANTRI = 1
    private val TYPE_A_HARUF = 2
    private val TYPE_B_HARUF = 3
    private val TYPE_NOTHING = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_JANTRI -> {
                JantriViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_A_HARUF -> {
                HarufAViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_B_HARUF -> {
                HarufBViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                JantriViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = mJantriSummuryList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            TYPE_JANTRI -> {
                mJantriSummuryList[position].let {
                    (holder as JantriViewHolder).binding(it)
                }
            }

            TYPE_A_HARUF -> {
                mJantriSummuryList[position].let {
                    (holder as HarufAViewHolder).binding(it)
                }
            }

            TYPE_B_HARUF -> {
                mJantriSummuryList[position].let {
                    (holder as HarufBViewHolder).binding(it)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (mJantriSummuryList[position].jantriSectionDetails.sectionTitle) {
            "Jantri" -> {
                TYPE_JANTRI
            }
            "Andar Haruf" -> {
                TYPE_A_HARUF
            }
            "Bahar Haruf" -> {
                TYPE_B_HARUF
            }
            else -> {
                TYPE_NOTHING
            }
        }
    }


    inner class JantriViewHolder(private var mBinding: JantariParentItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(mJantriExpoSummury: JantriExpoSummury) {
            mBinding.apply {
                tvTitle.text = mJantriExpoSummury.jantriSectionDetails.sectionTitle


                val mChildMembersAdapter =
                    JantariChildAdapter(mJantriSummuryList[adapterPosition].jantriDetailExpo,mListener)
                recyclerViewJori.apply {
                    layoutManager = GridLayoutManager(
                        mBinding.root.context,
                        10
                    )
                    adapter = mChildMembersAdapter
                }
            }
        }
    }

    inner class HarufAViewHolder(private var mBinding: JantariParentItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(mJantriExpoSummury: JantriExpoSummury) {
            mBinding.apply {
                tvTitle.text = mJantriExpoSummury.jantriSectionDetails.sectionTitle


                val mJantariChildAdapter =
                    JantariChildAdapter(mJantriSummuryList[adapterPosition].jantriDetailExpo,mListener)
                recyclerViewJori.apply {
                    layoutManager = GridLayoutManager(
                        mBinding.root.context,
                        10
                    )
                    adapter = mJantariChildAdapter
                }
            }

        }


    }

    inner class HarufBViewHolder(private var mBinding: JantariParentItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(mJantriExpoSummury: JantriExpoSummury) {
            mBinding.apply {
                tvTitle.text = mJantriExpoSummury.jantriSectionDetails.sectionTitle


                val mChildMembersAdapter = JantariChildAdapter(mJantriSummuryList[adapterPosition].jantriDetailExpo,mListener)
                recyclerViewJori.apply {
                    layoutManager = LinearLayoutManager(
                        mBinding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = mChildMembersAdapter
                }
            }
        }
    }


}