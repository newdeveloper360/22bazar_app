package com.userplay.bazar22.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.SubmitGameItemviewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET

class GameSubmitDialogAdapter(
    private val mGamesAddedList: List<Game>?,
    private val mFrom: String,
    private val isGameTypeVisible: Boolean
) : RecyclerView.Adapter<GameSubmitDialogAdapter.GameSubmitViewDialogHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameSubmitViewDialogHolder {
        return GameSubmitViewDialogHolder(
            SubmitGameItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGamesAddedList!!.size

    override fun onBindViewHolder(holder: GameSubmitViewDialogHolder, position: Int) {
        val item = mGamesAddedList?.get(position)

        item.let {
            holder.binding(it)
        }
    }

    inner class GameSubmitViewDialogHolder(private val mBinding: SubmitGameItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(mGame: Game?) {
            mBinding.apply {

                if (mFrom == DESAWAR_MARKET || mFrom == STARLINE_MARKET) {
                    type.visibility = View.GONE
                } else {

//                    if(isGameTypeVisible) {
//                        type.visibility = View.VISIBLE
//                        type.text = mGame?.session.toString()
//                    } else {
//                        type.visibility = View.GONE
//                    }

//                    type.visibility = View.VISIBLE

                    if (mGame?.session == "null") {
                        type.visibility = View.GONE
                    } else {
                        type.visibility = View.VISIBLE
                        type.text = mGame?.session.toString()
                    }
                }



                val formattedNumber = mGame?.number?.let { number ->
                    when {
                        mGame.pattiType.contains("HSB") == true ->
                            if (number.length > 3) number.substring(0, 3) + "x" + number.substring(3) else number
                        mGame.pattiType.contains("HSA") == true ->
                            if (number.length > 1) number.substring(0, 1) + "x" + number.substring(1) else number
                        else ->
                            if (number.length > 3) number.substring(0, 3) + "x" + number.substring(3) else number
                    }
                }
                digitValue.text = formattedNumber + mGame?.pattiType
                pointsValue.text = mGame?.amount.toString()

            }
        }
    }
}
