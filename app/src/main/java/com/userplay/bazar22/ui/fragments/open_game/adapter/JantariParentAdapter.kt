package com.userplay.bazar22.ui.fragments.open_game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.JantariParentItemViewBinding
import com.userplay.bazar22.models.jantari_model.JantariResponseItem
import com.userplay.bazar22.ui.fragments.open_game.callback.JantariListener

class JantariParentAdapter(
    private val mJantariResponseItemList: ArrayList<JantariResponseItem>,
    private val mListener: JantariListener
) :
    RecyclerView.Adapter<JantariParentAdapter.JantariParentsViewHolder>() {


    private val JANTRI = 1
    private val ANDAR_HARUF = 2
    private val BAHAR_HARUF = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JantariParentsViewHolder {
        return when (viewType) {
            JANTRI, ANDAR_HARUF, BAHAR_HARUF -> {
                JantariParentsViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                JantariParentsViewHolder(
                    JantariParentItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = mJantariResponseItemList.size

    override fun onBindViewHolder(holder: JantariParentsViewHolder, position: Int) {
        when (holder.itemViewType) {
            JANTRI, ANDAR_HARUF, BAHAR_HARUF -> {
                mJantariResponseItemList[position].let {
                    (holder).binding(it.title)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mJantariResponseItemList[position].title) {
            "Jantri" -> {
                JANTRI
            }
            "Andar Haruf" -> {
                ANDAR_HARUF
            }
            "Bahar Haruf" -> {
                BAHAR_HARUF
            }
            else -> {
                JANTRI
            }
        }
    }


    inner class JantariParentsViewHolder(private val mBinding: JantariParentItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(title: String?) {
            mBinding.apply {

                tvTitle.text = title

                val mChildMembersAdapter =
                    JantariChild2Adapter(mJantariResponseItemList[bindingAdapterPosition].numbers,mListener)
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
}