package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
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
import com.userplay.bazar22.databinding.FragmentJodiDigitBulkBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.NULL_GAME_TYPE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JodiDigitBulkFragment : Fragment(R.layout.fragment_jodi_digit_bulk), View.OnClickListener,
    OnGameTypeListener, TextWatcher {

    @Inject
    lateinit var mPref: MatkaPref

    private var _binding: FragmentJodiDigitBulkBinding? = null
    private val mBinding get() = _binding!!
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, false) }
    private val mArgs: JodiDigitBulkFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJodiDigitBulkBinding.inflate(inflater, container, false)
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
            finalSubmit.setOnClickListener(this@JodiDigitBulkFragment)
            back.setOnClickListener(this@JodiDigitBulkFragment)
            edJodi.addTextChangedListener(this@JodiDigitBulkFragment)
            edJodi.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(2),
                object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        if ((dest?.length ?: 0) >= 2) {
                            return null // Accept input as is
                        }
                        if (source != null && source.isNotEmpty()) {
                            val input = dest.toString() + source.toString()
                            return if (input.length > 2) {
                                input.substring(0, 2) // Truncate to first two digits

                            } else {
                                Log.e("jodi", "else called")
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

        addJodiOnList()
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.final_submit -> {

                        if (mGamesList.size > 0) {
                            mSendBody = SendBody(
                                gameTypeId = 2,
                                type = mArgs.from,
                                marketId = mArgs.marketID,
                                games = mGamesList
                            )

                            val action =
                                JodiDigitsFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, mArgs.from, mArgs.gameName, false
                                ).setTotalBids(mGamesList.size).setTotalPoints(mTotalPoints)
                            findNavController().navigate(action)
                        } else {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "Please add userplay")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")
                        }

//                        if (edAmount.text.isEmpty()) {
//                            edAmount.error = "Enter points"
//                            return@let
//                        }
//                        else if (edJodi.text.isNullOrEmpty()) {
//                            edJodi.error = "Jodi can't be empty"
//                            return@let
//                        }
//                        else if (edJodi.text.length != 2) {
//                            edJodi.error = "Please enter jodi"
//                            return@let
//                        }
//                        else {
//                            if (mGamesList.size == 0) {
//                                val bundle = Bundle()
//                                bundle.putString("message", "Please add userplay")
//                                findNavController().navigate(
//                                    R.id.errorDialogFragment,
//                                    bundle
//                                )
//                            } else {
//
//                                mSendBody = SendBody(
//                                    gameTypeId = 2,
//                                    type = mArgs.from,
//                                    marketId = mArgs.marketID,
//                                    games = mGamesList
//                                )
//
//                                val action =
//                                    JodiDigitsFragmentDirections.actionGlobalSubmitGameDialogFragment(
//                                        mSendBody, mArgs.from, mArgs.gameName
//                                    ).setTotalBids(mGamesList.size).setTotalPoints(mTotalPoints)
//                                findNavController().navigate(action)
//                            }
//                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addJodiOnList() {
        mBinding.apply {
            if (edAmount.text.isNotEmpty() && edJodi.text.isNotEmpty()) {
                if (edAmount.text.toString()
                        .toLong() < mPref.getMinBid(Constants.MIN_BID)
                ) {
                    edAmount.error =
                        "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                    return
                }

                mGamesList.add(
                    Game(
                        edAmount.text.trim().toString().toInt(),
                        edJodi.text.trim().toString(),
                        NULL_GAME_TYPE,
                        gameTypeId = 2,
                    )
                )
                mGamesAddAdapter.notifyDataSetChanged()
                edJodi.text.clear()
                val amount = edAmount.text.toString().toInt()
                mTotalPoints += amount
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


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s?.length == 2) {
            addJodiOnList()
        }
    }

    override fun afterTextChanged(s: Editable?) {

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