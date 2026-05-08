package com.userplay.bazar22.ui.fragments.home.adapters

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.BlueprintHomeScreenItemsBinding
import com.userplay.bazar22.models.get_markets.Market
import com.userplay.bazar22.ui.callbacks.OnGameListener
import com.userplay.bazar22.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random


class MarketAdapter(
    private val mMarketList: MutableList<Market>,
    private val mListener: OnGameListener,
    var isShowResultOnly:Int,
    var from:String
) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    private val updateInterval = 5000L
    private val jobMap = mutableMapOf<Int, Job>()
    private fun scheduleUpdate(position: Int,binding: BlueprintHomeScreenItemsBinding) {
        jobMap[position]?.cancel()
        val job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                try {
                    val randomMinusPlus = 1
                    if (binding.tvLiveUsers.text.toString().isEmpty()){
                        binding.tvLiveUsers.text="${Random.nextInt(1940, 2450)}"
                    }
                    if (binding.tvTotalBids.text.toString().isEmpty()){
                        binding.tvTotalBids.text="${Random.nextInt(19950, 24647)}"
                    }
                    if (randomMinusPlus==1){
                        if (binding.tvLiveUsers.text.toString().toInt()<9000){
                            val randomValue1 = Random.nextInt(0, 23)
                            val randomValue = Random.nextInt(binding.tvLiveUsers.text.toString().toInt(), binding.tvLiveUsers.text.toString().toInt()+randomValue1)
                            binding.tvLiveUsers.text="${randomValue}"

                        }
                        if (binding.tvTotalBids.text.toString().toInt()<90000) {
                            val randomValue2 = Random.nextInt(0, 100)
                            val randomBidsValue = Random.nextInt(
                                binding.tvTotalBids.text.toString().toInt(),
                                binding.tvTotalBids.text.toString().toInt() + randomValue2
                            )
                            binding.tvTotalBids.text = "${randomBidsValue}"
                        }
                    }else{
                        if (binding.tvLiveUsers.text.toString().toInt()>1889){
                            val randomValue1 = Random.nextInt(0, 23)
                            val randomValue = Random.nextInt(binding.tvLiveUsers.text.toString().toInt()-randomValue1, binding.tvLiveUsers.text.toString().toInt())
                            binding.tvLiveUsers.text="${randomValue}"
                        }
                        if (binding.tvTotalBids.text.toString().toInt()>19900) {
                            val randomValue2 = Random.nextInt(0, 100)
                            val randomBidsValue = Random.nextInt(
                                binding.tvTotalBids.text.toString().toInt() - randomValue2,
                                binding.tvTotalBids.text.toString().toInt()
                            )
                            binding.tvTotalBids.text = "${randomBidsValue}"
                        }
                    }

                } catch (e: Exception) {
                    binding.tvLiveUsers.text="1975"
                    binding.tvTotalBids.text="20045"
                }
                delay(updateInterval)
            }
        }
        jobMap[position] = job

    }

    override fun onViewRecycled(holder: MarketViewHolder) {
        super.onViewRecycled(holder)
        jobMap[holder.bindingAdapterPosition]?.cancel()
        jobMap.remove(holder.bindingAdapterPosition)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        jobMap.values.forEach { it.cancel() }
        jobMap.clear()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        return MarketViewHolder(
            BlueprintHomeScreenItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val item = mMarketList[position]

        item.let {
            holder.binding(position,it)
        }
    }

    override fun getItemCount(): Int = mMarketList.size

    inner class MarketViewHolder(private var mBinding: BlueprintHomeScreenItemsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(position: Int,mMarket: Market?) {
            mBinding.apply {
                if (mMarket != null) {
                    if (Constants.showLiveUsers && mMarket.gameOn != null && mMarket.gameOn) {
                        scheduleUpdate(position, mBinding)
                        llLive.visibility = View.VISIBLE
                    } else {
                        llLive.visibility = View.GONE
                    }
                    title.text = mMarket.name

                    if (mMarket.lastResult != null) {
                        number.text = mMarket.lastResult.result
                    } else {
                        number.text = "***-**-***"
                    }

                    tvCloseBids.text = mMarket.closeTime
                    tvOpenBids.text = mMarket.openTime


                    if (mMarket.gameOn != null) {
                        if (!mMarket.gameOn) { // show bid close dialog box on even numbers
                            tvbidStatus.text = "Closed for Today"
                            tvbidStatus.setTextColor(mBinding.root.context.resources.getColor(R.color.status_red))
                        } else {
                            // show menu
                            tvbidStatus.text = "Running now"
                            tvbidStatus.setTextColor(mBinding.root.context.resources.getColor(R.color.green))
                        }
                    }

                    rootLyt.setOnClickListener {
                        if (isShowResultOnly == 1) return@setOnClickListener
                        if (mMarket.gameOn != null) {
                            if (mMarket.gameOn) {
                                if (mMarket.id != null) {
                                    var openStatus = true
                                    if (mMarket.openGameStatus != null)
                                        openStatus = mMarket.openGameStatus
                                    mListener.onGameStartClick(
                                        bindingAdapterPosition,
                                        mMarket.id,
                                        mMarket.name.toString(),
                                        openStatus
                                    )
                                }
                            } else {
                                if (mMarket.id != null) {
                                    mListener.onGameClosedClick(
                                        bindingAdapterPosition,
                                        mMarket.openTime,
                                        mMarket.closeTime,
                                        mMarket.openResultTime,
                                        mMarket.closeResultTime,
                                        mMarket.name
                                    )

//                                    mMarket.openTime?.let { it1 ->
//                                        mMarket.closeTime?.let { it2 ->
//                                            mMarket.openResultTime?.let { it3 ->
//                                                mMarket.closeResultTime?.let { it4 ->
//                                                    mListener.onGameClosedClick(
//                                                        bindingAdapterPosition,
//                                                        it1, it2, it3, it4
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    }
                                }
                            }
                        } else {
                            Log.e("data", "else called")
                        }
                    }
                    imgChart.setOnClickListener{
                        if (isShowResultOnly == 1) return@setOnClickListener
                        if (mMarket.id != null) {
                            if (from == "desawar") {
                                val url = "${Constants.LIVE_SERVER}desawar/chart/${mMarket.id}"
                                val query = Uri.encode(url, "UTF-8")
                                val browserIntent =
                                    Intent(CATEGORY_BROWSABLE, Uri.parse(Uri.decode(query)))
                                browserIntent.action = ACTION_VIEW
                                imgChart.context.startActivity(browserIntent)
                            } else {
                                val url = "${Constants.LIVE_SERVER}market/pana-chart/${mMarket.id}"
                                val query = Uri.encode(url, "UTF-8")
                                val browserIntent =
                                    Intent(CATEGORY_BROWSABLE, Uri.parse(Uri.decode(query)))
                                browserIntent.action = ACTION_VIEW
                                imgChart.context.startActivity(browserIntent)

                            }
                        }
                    }
                    if (isShowResultOnly == 1) {
                        constPlay.visibility= View.GONE
                        tvPlayGame.visibility= View.GONE
                    }
                }
            }
        }
    }
}