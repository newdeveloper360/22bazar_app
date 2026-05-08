package com.userplay.bazar22.ui.fragments.home.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.ItemGameBinding
import com.userplay.bazar22.models.SelectGameModel
import com.userplay.bazar22.ui.callbacks.ItemGameClickListener

class SelectGameAdapter(
    private val mList: ArrayList<SelectGameModel>,
    private val mListener: ItemGameClickListener,
) : RecyclerView.Adapter<SelectGameAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            ItemGameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: GamesAddViewHolder, position: Int) {
        val item = mList[position]

        item.let {
            holder.binding(it,position)
        }
    }

    inner class GamesAddViewHolder(private val mBinding: ItemGameBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(item: SelectGameModel,position: Int) {
            mBinding.apply {
               llItem.setOnClickListener{
                   mListener.onItemClick(item)
               }
                if (position!=0 && position/2==0){
                   lineRight.visibility= View.GONE
                }
                imgGame.setImageResource(item.icon)
                tvTitle.text=item.name
                llItem.setBackgroundColor(Color.parseColor(item.bgColor))
            }
        }
    }
}