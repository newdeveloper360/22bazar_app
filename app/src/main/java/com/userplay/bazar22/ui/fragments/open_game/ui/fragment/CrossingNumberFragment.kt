package com.userplay.bazar22.ui.fragments.open_game.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentCrossingNumberBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.fragments.open_game.ui.activity.OpenGameActivity
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.pairsTwoGivenValuesNumbers
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CrossingNumberFragment : Fragment(R.layout.fragment_crossing_number), View.OnClickListener,
    OnGameTypeListener, CompoundButton.OnCheckedChangeListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentCrossingNumberBinding? = null
    private val mBinding get() = _binding!!
    private var mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, false) }
    private lateinit var mSendBody: SendBody
    private var mPairList: MutableList<Int> = ArrayList()
    private var mTotalAmount = 0 // define a variable to store the total amount

    private var isJodiCut = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrossingNumberBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }

        mBinding.apply {
            add.setOnClickListener(this@CrossingNumberFragment)
            jodiCutCheckBox.setOnCheckedChangeListener(this@CrossingNumberFragment)
            finalSubmit.setOnClickListener(this@CrossingNumberFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.add -> {
                        if (edFirstNumber.text.isNullOrEmpty()) {
                            edFirstNumber.error = "first number can't be empty"
                            return@let
                        } else if (edSecondNumber.text.isNullOrEmpty()) {
                            edSecondNumber.error = "second number can't be empty"
                            return@let
                        }else if (edAmount.text.isNullOrEmpty()) {
                            edAmount.error ="amount can't be empty"
                            return@let
                        }  else if (edAmount.text.toString()
                                .toLong() < mPref.getMinBid(Constants.MIN_BID)
                        ) {
                            edAmount.error =
                                "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                            return@let
                        } else {
                            mPairList = pairsTwoGivenValuesNumbers(
                                edFirstNumber.text.toString(),
                                edSecondNumber.text.toString()
                            )

                            for (number in mPairList) {
                                // Check if the item is less than 10
                                val formattedNumber = if (number < 10) {
                                    "0$number"
                                } else {
                                    number.toString()
                                }

                                // Check if the item is already in the gameList
                                val existingItem = mGamesList.find { it.number.toString() == formattedNumber }

                                if (existingItem != null) {
                                    existingItem.amount = existingItem.amount?.plus(edAmount.text.toString().toInt())
                                } else {
                                    mGamesList.add(
                                        Game(
                                            edAmount.text.toString().toInt(),
                                            formattedNumber,
                                            "null",
                                            gameTypeId = 13,
                                        )
                                    )
                                }
                            }

                            for (item in mGamesList) {
                                // check if the item's number is not in the randomList
                                if (!mPairList.contains(item.number?.toInt())) {
                                    // if the item's number is not in the randomList, increment its amount
                                    item.amount =
                                        item.amount?.plus(edAmount.text.toString().toInt())
                                    Log.e("data for loop", "" + item.amount)
                                }
                            }

                            updateTotalAmount()

                            if (isJodiCut) {
                                // Get the list of unique digits
                                val uniqueDigits = mGamesList.map { it.number?.toSet() }.distinct()

                                // Filter out the games with same digits
                                val filteredList = mGamesList.filter { game ->
                                    val digits = game.number?.toSet()
                                    digits != null && !uniqueDigits.any {
                                        it != digits && it!!.containsAll(
                                            digits
                                        )
                                    }
                                }

                                // Update the userplay list and notify the adapter
                                mGamesList.clear()
                                mGamesList.addAll(filteredList)
                                updateTotalAmount()
                            }

                            mGamesAddAdapter.notifyDataSetChanged()
                        }
                    }

                    R.id.final_submit -> {
                        if (mGamesList.size == 0) {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message","Please add userplay")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")
                        } else {
                            mSendBody = SendBody(
                                gameTypeId = 13,
                                type = DESAWAR_MARKET,
                                marketId = (activity as? OpenGameActivity)?.mMarketID,
                                games = mGamesList
                            )
                            val action =
                                CrossingNumberFragmentDirections.actionCrossingNumberFragmentToSubmitGameDialogFragment2(
                                    mSendBody,
                                    DESAWAR_MARKET,
                                    (activity as? OpenGameActivity)?.mGameName.toString(),
                                    false
                                ).setTotalBids(mGamesList.size).setTotalPoints(mTotalAmount)
                            findNavController().navigate(action)


                        }
                    }
                }
            }
        }
    }

    private fun updateTotalAmount() {
        mTotalAmount = mGamesList.sumBy { it.amount!! } // calculate the total amount

        mBinding.apply {
            tvBids.text = mGamesList.size.toString()
            tvPoints.text = mTotalAmount.toString()
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
        mTotalAmount -= totalPoints
        updateTotalAmount()
        mGamesAddAdapter.notifyDataSetChanged()
    }

    override fun updateSubmitResult(totalPoints: Int) {

    }

    override fun onCrossingInserted(amount: String, number: String, position: Int) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        isJodiCut = isChecked

        if (isChecked) {
            // Get the list of unique digits
            val uniqueDigits = mGamesList.map { it.number?.toSet() }.distinct()

            // Filter out the games with same digits
            val filteredList = mGamesList.filter { game ->
                val digits = game.number?.toSet()
                digits != null && !uniqueDigits.any { it != digits && it!!.containsAll(digits) }
            }

            // Update the userplay list and notify the adapter
            mGamesList.clear()
            mGamesList.addAll(filteredList)
            mGamesAddAdapter.notifyDataSetChanged()
            updateTotalAmount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}