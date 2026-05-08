package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.ItemPairsBinding
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.fragments.home.models.PairsModel

class PairsAdapter(
    private var mList: ArrayList<PairsModel>,
    var minimumBid:Int,
    private val mListener: ItemClickListener,
) : RecyclerView.Adapter<PairsAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            ItemPairsBinding.inflate(
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

    inner class GamesAddViewHolder(private val mBinding: ItemPairsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(number: PairsModel,post:Int) {
            mBinding.apply {
                edValue.setOnFocusChangeListener { v, hasFocus ->
                    if(!hasFocus){
                        try {
                            if(edValue.text!!.toString().isNotEmpty() && edValue.text!!.toString().toInt()>=minimumBid){
                                edValue.error=null
                            }else if(edValue.text!!.toString().isEmpty()){
                                edValue.error=null
                            }else{
                                edValue.error= "Minimum Bid Amount is $minimumBid"
                            }
                        } catch (e: Exception) {
                            edValue.setText("")
                        }
                    }
                }
               mBinding.model=number
            }
        }
    }
    fun updateList(list:ArrayList<PairsModel>){
        mList=list
        notifyDataSetChanged()
    }

    fun addList(list:ArrayList<PairsModel>){
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getList():ArrayList<PairsModel>{
       return mList
    }

}