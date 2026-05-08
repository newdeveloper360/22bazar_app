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
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSingleDigitsBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.SESSION_TYPE
import com.userplay.bazar22.utils.Constants.SINGLE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleDigitsFragment : Fragment(R.layout.fragment_single_digits), View.OnClickListener,
    OnGameTypeListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSingleDigitsBinding? = null
    private val mBinding get() = _binding!!
    private var mGameType: String? = null
    private val mGamesList: ArrayList<Game> = ArrayList()
    private var isTypeShow = true
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow, true) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: SingleDigitsFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleDigitsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    var gameTypeId: Int = 1
    private fun initView() {

        if (mArgs.from == STARLINE_MARKET) {
            gameTypeId = 9
            mBinding.gameTypeLyt.visibility = View.GONE
            mBinding.tvType.visibility = View.GONE
            isTypeShow = false
        }

        mBinding.apply {
            gameType.setOnClickListener(this@SingleDigitsFragment)
            tvGameType.setOnClickListener(this@SingleDigitsFragment)
            add.setOnClickListener(this@SingleDigitsFragment)
            finalSubmit.setOnClickListener(this@SingleDigitsFragment)
            back.setOnClickListener(this@SingleDigitsFragment)
            edNumber.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(1),
                object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        if ((dest?.length ?: 0) >= 1) {
                            return null // Accept input as is
                        }
                        if (source != null && source.isNotEmpty()) {
                            val input = dest.toString() + source.toString()
                            return if (input.length > 1) {
                                input.substring(0, 1) // Truncate to first two digits
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

    private fun observer() {
        activity?.let {
            mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mGameType = it
                mPref.setSessionType(it)
                mBinding.tvGameType.text = it
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.gameType, R.id.tvGameType -> {
                        if (!mArgs.openStatus) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.open_game_already_set),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@let
                        }
                        val action =
                            SingleDigitsFragmentDirections.actionGlobalGameTypeDialogFragment(
                                SINGLE_GAME_TYPE
                            )
                        findNavController().navigate(action)
                    }

                    R.id.add -> {
                        if (edNumber.text.isNullOrEmpty()) {
                            edNumber.error = "Please Enter Single Digit"
                            return@let
                        } else if (edAmount.text.isNullOrEmpty()) {
                            edAmount.error = "Please Enter Points"
                            return@let
                        } else if (edAmount.text.toString()
                                .toLong() < mPref.getMinBid(Constants.MIN_BID)
                        ) {
                            edAmount.error =
                                "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                            return@let
                        } else {
                            mGamesList.add(
                                Game(
                                    edAmount.text.toString().trim().toInt(),
                                    edNumber.text.toString().trim(),
                                    mGameType,
                                    gameTypeId,
                                )
                            )
                            val amount = edAmount.text.toString().toInt()
                            mTotalPoints += amount
                            mGamesAddAdapter.notifyDataSetChanged()
                            edNumber.text.clear()
                            edAmount.text.clear()
                        }
                    }

                    R.id.final_submit -> {

                        if (mGamesList.size == 0) {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "Please add userplay")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")

                        } else {
                            Log.e("singleDigits", "" + mArgs.from)
                            mSendBody = SendBody(
                                gameTypeId,
                                type = mArgs.from,
                                marketId = mArgs.marketID,
                                games = mGamesList
                            )

                            val action =
                                SingleDigitsFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, mArgs.from, mArgs.gameName, true
                                ).setTotalBids(mGamesList.size).setTotalPoints(mTotalPoints)
                            findNavController().navigate(action)
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }


    private fun setFinalSubmitData(totalBids: Int, totalPoints: Int) {
        mBinding.apply {
            tvBids.text = totalBids.toString()
            tvPoints.text = totalPoints.toString()
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
        mTotalPoints -= totalPoints
        setFinalSubmitData(mGamesList.size, mTotalPoints)
        mGamesAddAdapter.notifyDataSetChanged()
    }

    override fun updateSubmitResult(totalPoints: Int) {
        setFinalSubmitData(mGamesList.size, mTotalPoints)
    }

    override fun onCrossingInserted(amount: String, number: String, position: Int) {

    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(BALANCE).toString()
        if (!mArgs.openStatus) {
            val mSharedViewModels: SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(CLOSE_GAME_TYPE)
            mPref.setSessionType(CLOSE_GAME_TYPE)
        } else {
            val mSharedViewModels: SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.OPEN_GAME_TYPE)
            mPref.setSessionType(Constants.OPEN_GAME_TYPE)
        }
        mGameType = mPref.getSessionType(SESSION_TYPE)
        mBinding.tvGameType.text = mGameType
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}