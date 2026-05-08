package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentFullSangamBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.NULL_GAME_TYPE
import com.userplay.bazar22.utils.allPana
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FullSangamFragment : Fragment(R.layout.fragment_full_sangam), View.OnClickListener,
    OnGameTypeListener {

    @Inject
    lateinit var mPref: MatkaPref

    private var _binding: FragmentFullSangamBinding? = null
    private val mBinding get() = _binding!!
    private var mTotalPoints: Int = 0
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mOriginalList: ArrayList<Game> = ArrayList()
    private lateinit var mSendBody: SendBody
    private val mArgs: FullSangamFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by viewModels()


    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, false) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullSangamBinding.inflate(inflater, container, false)
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
                mBinding.tvAmount.text = it
            }
        }
    }

    private fun initView() {


        mBinding.apply {
            add.setOnClickListener(this@FullSangamFragment)
            finalSubmit.setOnClickListener(this@FullSangamFragment)
            back.setOnClickListener(this@FullSangamFragment)
            edOpenPana.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(3),
                object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        if ((dest?.length ?: 0) >= 3) {
                            return null // Accept input as is
                        }
                        if (source != null && source.isNotEmpty()) {
                            val input = dest.toString() + source.toString()
                            return if (input.length > 3) {
                                input.substring(0, 3) // Truncate to first two digits
                            } else {
                                null // Accept input as is
                            }
                        }
                        return null // Accept input as is
                    }
                }
            )
            edClosePana.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(3),
                object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        if ((dest?.length ?: 0) >= 3) {
                            return null // Accept input as is
                        }
                        if (source != null && source.isNotEmpty()) {
                            val input = dest.toString() + source.toString()
                            return if (input.length > 3) {
                                input.substring(0, 3) // Truncate to first two digits
                            } else {
                                null // Accept input as is
                            }
                        }
                        return null // Accept input as is
                    }
                }
            )
        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.add -> {
                        if (edOpenPana.text.isNullOrEmpty()) {
                            edOpenPana.error = "Enter Valid Pana"
                            return@let
                        } else if (edOpenPana.text.trim().length != 3) {
                            edOpenPana.error = "Enter Valid Pana"
                            return@let
                        } else if (!edOpenPana.text.trim().toString().toInt().allPana()) {
                            edOpenPana.error = "Enter Valid Pana"
                            return@let
                        } else if (edClosePana.text.isNullOrEmpty()) {
                            edClosePana.error = "Enter Valid Pana"
                            return@let
                        } else if (edClosePana.text.trim().length != 3) {
                            edClosePana.error = "Enter Valid Pana"
                            return@let
                        } else if (!edClosePana.text.trim().toString().toInt().allPana()) {
                            edClosePana.error = "Enter Valid Pana"
                            return@let
                        } else if (edAmount.text.isNullOrEmpty()) {
                            edAmount.error = "Please enter point"
                            return@let
                        } else if (edAmount.text.toString()
                                .toLong() < mPref.getMinBid(Constants.MIN_BID)
                        ) {
                            edAmount.error =
                                "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                            return@let
                        } else {
                            mOriginalList.add(
                                Game(
                                    edAmount.text.toString().trim().toInt(),
                                    edOpenPana.text.toString().trim() + edClosePana.text.toString()
                                        .trim(),
                                    NULL_GAME_TYPE,
                                    gameTypeId = 8
                                )
                            )
                            mGamesList.add(
                                Game(
                                    edAmount.text.toString().trim().toInt(),
                                    edOpenPana.text.toString()
                                        .trim() + "-" + edClosePana.text.toString().trim(),
                                    NULL_GAME_TYPE,
                                    gameTypeId = 8
                                )
                            )
                            val amount = edAmount.text.toString().toInt()
                            mTotalPoints += amount
                            mGamesAddAdapter.notifyDataSetChanged()
                            edOpenPana.text.clear()
                            edClosePana.text.clear()
                            edAmount.text.clear()
                        }

                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.final_submit -> {
                        if (mGamesList.size == 0) {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "Please add userplay")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")
                        } else {
                            mSendBody = SendBody(
                                gameTypeId = 8,
                                type = GENERAL_MARKET,
                                marketId = mArgs.marketID,
                                games = mOriginalList
                            )

                            val gson = GsonBuilder().setPrettyPrinting().create().toJson(mSendBody)

                            Log.e("data print", "" + gson)

                            val action =
                                HalfSangamAFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, GENERAL_MARKET, mArgs.gameName, false
                                ).setTotalBids(mOriginalList.size).setTotalPoints(mTotalPoints)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun removeGameType(
        number: String,
        gameType: String?,
        position: Int,
        totalPoints: Int
    ) {
        mGamesList.removeAt(position)
        mOriginalList.removeAt(position)
        mTotalPoints -= totalPoints
        setFinalSubmitData(mGamesList.size, mTotalPoints)
        mGamesAddAdapter.notifyDataSetChanged()
    }

    private fun setFinalSubmitData(totalBids: Int, totalPoints: Int) {
        mBinding.apply {
            tvBids.text = totalBids.toString()
            tvPoints.text = totalPoints.toString()
        }
    }

    override fun updateSubmitResult(totalPoints: Int) {
        setFinalSubmitData(mGamesList.size, mTotalPoints)
    }

    override fun onCrossingInserted(amount: String, number: String, position: Int) {

    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}