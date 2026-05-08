package com.userplay.bazar22.ui.fragments.star_line.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.StarlineGameItemviewBinding
import com.userplay.bazar22.models.get_markets.Market
import com.userplay.bazar22.ui.callbacks.OnGameListener
import com.userplay.bazar22.utils.Constants

class StarLineMarketAdapter(
    private val mMarketList: MutableList<Market>,
    private val mListener: OnGameListener
) : RecyclerView.Adapter<StarLineMarketAdapter.StarLineMarketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarLineMarketViewHolder {
        return StarLineMarketViewHolder(
            StarlineGameItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StarLineMarketViewHolder, position: Int) {
        val item = mMarketList[position]

        item.let {
            holder.binding(it)
        }
    }

    override fun getItemCount(): Int = mMarketList.size

    inner class StarLineMarketViewHolder(private var mBinding: StarlineGameItemviewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun binding(mMarket: Market?) {
            mBinding.apply {
                if (mMarket != null) {
                    //  title.text = mMarket.name

                    if (mMarket.lastResult != null) {
                        tvResult.text = mMarket.lastResult.result
                    } else {
                        tvResult.text = "***-**"

                    }

                    if (mMarket.gameOn != null) {
                        if (!mMarket.gameOn) { // show bid close dialog box on even numbers
                            status.text = "Closed for Today"
                            alarm.setImageResource(R.drawable.alarm_off)
                            status.setTextColor(mBinding.root.context.resources.getColor(R.color.pink))

                        } else {
                            // show menu
                            status.text = "Running now"
                            alarm.setImageResource(R.drawable.alarm_on)
                            status.setTextColor(mBinding.root.context.resources.getColor(R.color.green))
                        }

                    }


                    tvOpenTime.text = mMarket.openTime
                    imgChart.setOnClickListener {
                        if (mMarket.id != null) {
                            val url =
                                "${Constants.LIVE_SERVER}start-line-market/chart/${mMarket.id}"
                            val query = Uri.encode(url, "UTF-8")
                            val browserIntent =
                                Intent(Intent.CATEGORY_BROWSABLE, Uri.parse(Uri.decode(query)))
                            browserIntent.action = Intent.ACTION_VIEW
                            imgChart.context.startActivity(browserIntent)
                        }

                    }
                    root.setOnClickListener {
                        if (mMarket.gameOn != null) {
                            if (mMarket.gameOn) {
                                if (mMarket.id != null) {
                                    mListener.onGameStartClick(
                                        bindingAdapterPosition,
                                        mMarket.id,
                                        mMarket.name.toString(),
                                        true
                                    )
                                }
                            } else {
                                if (mMarket.id != null) {
//                                    mListener.onGameStartClick(bindingAdapterPosition, mMarket.id,mMarket.name.toString())

                                    mMarket.openTime?.let { it1 ->
                                        mListener.onGameClosedClick(
                                            bindingAdapterPosition,
                                            it1, "", "", "",
                                            mMarket.name
                                        )
                                    }
                                }
                            }
                        } else {
                            Log.e("data", "else called")
                        }
                    }
                }
            }
        }
    }
}