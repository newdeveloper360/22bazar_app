package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.ItemNumberBinding
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.fragments.home.models.NumbersModel

class NumbersAdapter(
    private var mList: ArrayList<NumbersModel>,
    private val mListener: ItemClickListener,
) : RecyclerView.Adapter<NumbersAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            ItemNumberBinding.inflate(
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

    inner class GamesAddViewHolder(private val mBinding: ItemNumberBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(number: NumbersModel,post:Int) {
            mBinding.apply {
                tvNumber.text =number.number.toString()
                if (number.isSelected){
                    tvNumber.setTextColor(ContextCompat.getColor(tvNumber.context,R.color.white))
                    constMain.setBackgroundResource(R.drawable.bg_number)
                }else{
                    tvNumber.setTextColor(ContextCompat.getColor(tvNumber.context,R.color.black))
                    constMain.background=null
                }
                constMain.setOnClickListener {
                mListener.onItemClick(position =post)
              }
            }
        }
    }
    fun updateList(list:ArrayList<NumbersModel>){
        mList=list
        notifyDataSetChanged()
    }
}