package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.GamesItemviewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener

class GamesAddAdapter(
    private val mGamesAddedList: ArrayList<Game>,
    private val mListener: OnGameTypeListener,
    private val isTypeShow: Boolean,
     var isDigitType: Boolean=false,
) : RecyclerView.Adapter<GamesAddAdapter.GamesAddViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): GamesAddViewHolder {
        return GamesAddViewHolder(
            GamesItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mGamesAddedList.size

    override fun onBindViewHolder(holder: GamesAddViewHolder, position: Int) {
        val item = mGamesAddedList[position]

        item.let {
            holder.binding(it)
        }
    }

    inner class GamesAddViewHolder(private val mBinding: GamesItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(mGame: Game?) {
            mBinding.apply {

                if (isTypeShow) {
                    type.visibility = View.VISIBLE
                } else {
                    type.visibility = View.GONE
                }
                if (mGame?.number.equals("0")){
                    if(isDigitType){
                        digitValue.text = "0"
                    }else{
                        digitValue.text = "000"
                    }

                }else{
                    digitValue.text = mGame?.number.toString()+mGame?.pattiType
                }

                pointsValue.text = mGame?.amount.toString()
                type.text = mGame?.session.toString()
                delete.setOnClickListener {
                    if (mGame?.amount != null && mGame.number != null) {
                        mListener.removeGameType(
                            mGame.number,
                            mGame.session,
                            adapterPosition,
                            mGame.amount!!
                        )
                    }
                }


                if (mGame?.amount != null) {
                    mListener.updateSubmitResult(mGame.amount!!)
                }

                mListener.onCrossingInserted(mGame?.amount.toString(),mGame?.number.toString(),adapterPosition)

            }
        }
    }
}