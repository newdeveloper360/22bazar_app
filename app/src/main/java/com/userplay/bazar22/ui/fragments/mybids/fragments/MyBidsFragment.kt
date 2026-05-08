package com.userplay.bazar22.ui.fragments.mybids.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentMyBidsBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.*
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyBidsFragment : Fragment(R.layout.fragment_my_bids), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentMyBidsBinding? = null
    private val mBinding get() = _binding!!
    private val mSharedViewModels: SharedViewModels by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBidsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvBalance.text = it
            }
        }
    }

    private fun initView() {


        mBinding.apply {
            back.setOnClickListener(this@MyBidsFragment)
            bidHistory.setOnClickListener(this@MyBidsFragment)
            gameResults.setOnClickListener(this@MyBidsFragment)
            kingStarlineBidHistory.setOnClickListener(this@MyBidsFragment)
            kingStarlineResultHistory.setOnClickListener(this@MyBidsFragment)
            desawarBidHistory.setOnClickListener(this@MyBidsFragment)
            desawarResultHistory.setOnClickListener(this@MyBidsFragment)

            if (Constants.SHOW_STARLINE) {
                kingStarlineBidHistory.visibility = View.VISIBLE
                kingStarlineResultHistory.visibility = View.VISIBLE
            } else {
                kingStarlineBidHistory.visibility = View.GONE
                kingStarlineResultHistory.visibility = View.GONE
            }
            if (mPref.getEnableDesawarOnly() != 0) {
                bidHistory.visibility=View.GONE
                gameResults.visibility=View.GONE
                kingStarlineBidHistory.visibility=View.GONE
                kingStarlineResultHistory.visibility=View.GONE
            }
        }

    }

    override fun onClick(v: View?) {
        activity?.let {
            when (v?.id) {
                R.id.bid_history -> {
                    findNavController().navigate(R.id.myBidsHistoryFragment)
                }
                R.id.game_results -> {
                    findNavController().navigate(R.id.myGameResultsFragment)
                }
                R.id.king_starline_bid_history -> {
                    findNavController().navigate(R.id.starLineBidHistoryFragment)
                }
                R.id.king_starline_result_history -> {
                    findNavController().navigate(R.id.starLineResultHistoryFragment)
                }
                R.id.desawar_bid_history -> {
                    findNavController().navigate(R.id.desawerBidHistoryFragment)
                }
                R.id.desawar_result_history -> {
                    findNavController().navigate(R.id.desawerBidResultHistoryFragment)
                }
                R.id.back -> {
                    findNavController().popBackStack()
                }
                else -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(Constants.BALANCE).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}