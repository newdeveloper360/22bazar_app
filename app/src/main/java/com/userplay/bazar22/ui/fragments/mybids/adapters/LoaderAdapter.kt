package com.userplay.bazar22.ui.fragments.mybids.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.LoaderItemViewBinding

class LoaderAdapter : LoadStateAdapter<LoaderAdapter.LoaderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder(
            LoaderItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.binding(loadState)
    }


    inner class LoaderViewHolder(private val mBinding: LoaderItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(loadState: LoadState) {
            mBinding.progressBar.isVisible = loadState is LoadState.Loading
        }


    }

}