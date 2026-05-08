package com.userplay.bazar22.ui.fragments.navigation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.DrawerItemViewBinding
import com.userplay.bazar22.models.navigation.NawDrawerPanelItem
import com.userplay.bazar22.ui.fragments.navigation.callback.OnMenuItemClickListener


class NewCustomDrawerAdapter(
    private var drawerList: List<NawDrawerPanelItem>,
    private val mListener : OnMenuItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DrawerItemsViewHolder(
            DrawerItemViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = drawerList[position]

        item.let {
            (holder as DrawerItemsViewHolder).binding(it)
        }
    }

    override fun getItemCount(): Int = drawerList.size

    inner class DrawerItemsViewHolder(var mBinding: DrawerItemViewBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun binding(mNawDrawerPanelItem: NawDrawerPanelItem) {

            mBinding.apply {
                tvTitle.text = mNawDrawerPanelItem.mTitle
                mNawDrawerPanelItem.mImage?.let { titleImg.setImageResource(it) }

                linearMain.setOnClickListener {
                    mListener.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }
}