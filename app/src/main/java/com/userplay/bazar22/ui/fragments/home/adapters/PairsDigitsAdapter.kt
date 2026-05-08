package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.ItemPairsDigitsBinding
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.fragments.home.models.PairsModelDigits

class PairsDigitsAdapter(
    private var mList: ArrayList<PairsModelDigits>,
    var minimumBid:Int,
    private val mListener: ItemClickListener,
) : RecyclerView.Adapter<PairsDigitsAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            ItemPairsDigitsBinding.inflate(
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

    inner class GamesAddViewHolder(private val mBinding: ItemPairsDigitsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(number: PairsModelDigits,post:Int) {
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
    fun updateList(list:ArrayList<PairsModelDigits>){
        mList=list
        notifyDataSetChanged()
    }

    fun addList(list:ArrayList<PairsModelDigits>){
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getList():ArrayList<PairsModelDigits>{
       return mList
    }

}