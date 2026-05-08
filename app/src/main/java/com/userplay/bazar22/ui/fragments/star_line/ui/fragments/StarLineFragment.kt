package com.userplay.bazar22.ui.fragments.star_line.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentStarLineBinding
import com.userplay.bazar22.models.get_markets.Market
import com.userplay.bazar22.models.get_rate_new.GameRates
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.star_line.adapter.StarLineMarketAdapter
import com.userplay.bazar22.ui.fragments.star_line.adapter.StarLineTopAdapter
import com.userplay.bazar22.ui.fragments.star_line.viewmodels.StarLineViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.OPEN_GAME_TYPE
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StarLineFragment : Fragment(R.layout.fragment_star_line), OnGameListener,
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentStarLineBinding
    private val mStarLineViewModel: StarLineViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private val mGameTypeList: ArrayList<GameRates> = ArrayList()
    private val mMarketList: MutableList<Market> = ArrayList()
    private val mGameTypeAdapter: StarLineTopAdapter by lazy { StarLineTopAdapter(mGameTypeList) }
    private val mStarLineMarketAdapter: StarLineMarketAdapter by lazy {
        StarLineMarketAdapter(
            mMarketList,
            this
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentStarLineBinding.inflate(inflater, container, false)
            initView()
            observer()
        }

        return mBinding.root
    }


    private fun initView() {

        mBinding.back.setOnClickListener(this)

        activity?.let {
            mBinding.topRV.apply {
                layoutManager = GridLayoutManager(it, 2)
                adapter = mGameTypeAdapter
            }

            mBinding.rvGames.apply {
                layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                adapter = mStarLineMarketAdapter
            }
        }

        if (CheckNetwork.isNetworkConnected) {
            mStarLineViewModel.getGameRates()
            mStarLineViewModel.getStarLineMarket()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
        }

        mBinding.history.setOnClickListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {

        activity?.let {

            mStarLineViewModel.mGameRatesResponse.observe(it) { response ->
                when (response) {

                    is ApiState.Success -> {
                        if (response.data?.error != null) {
                            if (!response.data.error) {
                                mGameTypeList.clear()
                                if (response.data.response?.data != null) {
                                    for (item in response.data.response.data) {

                                        for (item2 in item.list) {
                                            if (item2.type.equals("start_line")) {
                                                mGameTypeList.add(item2)
                                            }
                                        }
                                    }
                                    mGameTypeAdapter.notifyDataSetChanged()
                                }

                            } else {
                                it.showToast(response.data.message.toString())
                            }
                        }
                    }

                    is ApiState.Error -> {
                        Log.e("error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                    }
                }
            }

            mStarLineViewModel.mMarketResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", data.message.toString())
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")

                                } else {
                                    if (data.response?.markets != null) {
                                        mMarketList.clear()
                                        mMarketList.addAll(data.response.markets)
                                        mStarLineMarketAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("market_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("market_loading", "loading---->>>>")
                    }
                }
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }


    override fun onGameClosedClick(
        position: Int?,
        openTime: String?,
        closeTime: String?,
        openResultTime: String?,
        closeResultTime: String?,
        bidName: String?
    ) {

        val dialog = StarLineGamesClosedFragment()
        val bundle = Bundle()
        bundle.putString("openTime", openTime)
        bundle.putString("closeTime", closeTime)
        bundle.putString("openResultTime", openResultTime)
        bundle.putString("closeResultTime", closeResultTime)
        bundle.putString("bidName", bidName)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "OpenGame")
    }

    override fun onGameStartClick(
        position: Int,
        marketID: Int,
        mGameName: String,
        openStatus: Boolean
    ) {
        if (openStatus){
            mPref.setSessionType(CLOSE_GAME_TYPE)
        }else{
            mPref.setSessionType(OPEN_GAME_TYPE)
        }
        val action = StarLineFragmentDirections.actionStarLineFragmentToSelectGameFragment(
            marketID,
            mGameName,
            STARLINE_MARKET,
            openStatus
        )
        findNavController().navigate(action)
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.history -> {
                        findNavController().navigate(R.id.action_starLineFragment_to_kingStarHistoryFragment)
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }


}