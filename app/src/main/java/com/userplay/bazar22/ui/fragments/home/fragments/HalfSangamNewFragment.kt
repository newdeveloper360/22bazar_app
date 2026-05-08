package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentHalfSangamNewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.allPana
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HalfSangamNewFragment : Fragment(R.layout.fragment_half_sangam_new), View.OnClickListener,
    OnGameTypeListener {

    @Inject
    lateinit var mPref: MatkaPref

    private var _binding: FragmentHalfSangamNewBinding? = null
    private val mBinding get() = _binding!!
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: HalfSangamANewFragmentArgs by navArgs()
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mOriginalList: ArrayList<Game> = ArrayList()
    private val mSharedViewModels: SharedViewModels by viewModels()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, true) }
    var mGameType = "close"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHalfSangamNewBinding.inflate(inflater, container, false)
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
            btnAdd.setOnClickListener(this@HalfSangamNewFragment)
            finalSubmit.setOnClickListener(this@HalfSangamNewFragment)
            back.setOnClickListener(this@HalfSangamNewFragment)
            edInputAnk.filters = arrayOf<InputFilter>(
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
            edInputPana.filters = arrayOf<InputFilter>(
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


            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (mBinding.rbOpen.isChecked) {
                    mGameType = "open"
                    edInputAnk.hint = "Enter Close Digit"
                    edInputPana.hint = "Enter Open Pana"
                    tvHalfSangam.text = "Close Ank Open Pana"
                    mBinding.rbClose.isChecked = false
                    edInputAnk.error = null
                    edInputPana.error = null
                    edInputAnk.text.clear()
                    edInputPana.text.clear()
                } else {
                    mGameType = "close"
                    mBinding.rbClose.isChecked = true
                    edInputAnk.hint = "Enter Open Digit"
                    edInputPana.hint = "Enter Close Pana"
                    tvHalfSangam.text = "Open Ank Close Pana"
                    edInputAnk.error = null
                    edInputPana.error = null
                    edInputAnk.text.clear()
                    edInputPana.text.clear()
                }
            }
        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.btnAdd -> {
                        if (mGameType == "open") {
                            if (edInputPana.text.isNullOrEmpty()) {
                                edInputPana.error = " Enter valid pana"
                                return@let
                            } else if (edInputPana.text.trim().length != 3) {
                                edInputPana.error = " Enter valid pana"
                                return@let
                            } else if (!edInputPana.text.trim().toString().toInt().allPana()) {
                                edInputPana.error = " Enter valid pana"
                                return@let
                            } else if (edInputAnk.text.isNullOrEmpty()) {
                                edInputAnk.error = "Please enter close digit"
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
                                        edInputPana.text.toString()
                                            .trim() + edInputAnk.text.toString()
                                            .trim(),
                                        mGameType,
                                        gameTypeId = 7,
                                        pattiType=" (HSB)",
                                    )
                                )
                                mGamesList.add(
                                    Game(
                                        edAmount.text.toString().trim().toInt(),
                                        edInputPana.text.toString()
                                            .trim() + "-" + edInputAnk.text.toString().trim(),
                                        mGameType,
                                        gameTypeId = 7,
                                        pattiType=" (HSB)",
                                    )
                                )
                            }
                            val amount = edAmount.text.toString().toInt()
                            mTotalPoints += amount
                            mGamesAddAdapter.notifyDataSetChanged()
                            edInputAnk.text.clear()
                            edInputPana.text.clear()
                            edAmount.text.clear()
                        } else {
                            if (edInputAnk.text.isNullOrEmpty()) {
                                edInputAnk.error = "Please enter open digit"
                                return@let
                            } else if (edInputPana.text.isNullOrEmpty()) {
                                edInputPana.error = " Enter valid pana"
                                return@let
                            } else if (edInputPana.text.trim().length != 3) {
                                edInputPana.error = " Enter valid pana"
                                return@let
                            } else if (!edInputPana.text.trim().toString().toInt().allPana()) {
                                edInputPana.error = " Enter valid pana"
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
                                        edInputAnk.text.toString()
                                            .trim() + edInputPana.text.toString()
                                            .trim(),
                                        mGameType,
                                        gameTypeId = 6,
                                        pattiType=" (HSA)",
                                    )
                                )
                                mGamesList.add(
                                    Game(
                                        edAmount.text.toString().trim().toInt(),
                                        edInputAnk.text.toString()
                                            .trim() + "-" + edInputPana.text.toString().trim(),
                                        mGameType,
                                        gameTypeId = 6,
                                        pattiType=" (HSA)",
                                    )
                                )
                            }

                            val amount = edAmount.text.toString().toInt()
                            mTotalPoints += amount
                            mGamesAddAdapter.notifyDataSetChanged()
                            edInputAnk.text.clear()
                            edInputPana.text.clear()
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
                                gameTypeId = 6,
                                type = Constants.GENERAL_MARKET,
                                marketId = mArgs.marketID,
                                games = mOriginalList,
                            )

                            val gson = GsonBuilder().setPrettyPrinting().create().toJson(mSendBody)

                            Log.e("data print", "" + gson)

                            val action =
                                HalfSangamNewFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, Constants.GENERAL_MARKET, mArgs.gameName, true
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
        val mGameType = mPref.getSessionType(Constants.SESSION_TYPE)
        // mBinding.tvGameType.text = mGameType
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}