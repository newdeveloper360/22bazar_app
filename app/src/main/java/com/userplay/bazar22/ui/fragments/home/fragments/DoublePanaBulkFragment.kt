package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.userplay.bazar22.databinding.FragmentDoublePanaBulkBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DoublePanaBulkFragment : Fragment(R.layout.fragment_double_pana_bulk), View.OnClickListener,
    OnGameTypeListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentDoublePanaBulkBinding? = null
    private val mBinding get() = _binding!!
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mDoublePanaList: MutableList<Int> = ArrayList()
    private var mTotalAmount = 0 // define a variable to store the total amount
    private lateinit var mSendBody: SendBody
    private val mArgs: DoublePanaBulkFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoublePanaBulkBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    var gameTypeId: Int = 4
    private fun initView() {

        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId=11
            mBinding.gameTypeLyt.visibility = View.GONE
            mBinding.tvType.visibility = View.GONE
            isTypeShow = false
        }

        mBinding.apply {
            gameType.setOnClickListener(this@DoublePanaBulkFragment)
            tvGameType.setOnClickListener(this@DoublePanaBulkFragment)
            btnZero.setOnClickListener(this@DoublePanaBulkFragment)
            btnOne.setOnClickListener(this@DoublePanaBulkFragment)
            btnTwo.setOnClickListener(this@DoublePanaBulkFragment)
            btnThree.setOnClickListener(this@DoublePanaBulkFragment)
            btnFour.setOnClickListener(this@DoublePanaBulkFragment)
            btnFive.setOnClickListener(this@DoublePanaBulkFragment)
            btnSix.setOnClickListener(this@DoublePanaBulkFragment)
            btnSeven.setOnClickListener(this@DoublePanaBulkFragment)
            btnEight.setOnClickListener(this@DoublePanaBulkFragment)
            btnNine.setOnClickListener(this@DoublePanaBulkFragment)
            finalSubmit.setOnClickListener(this@DoublePanaBulkFragment)
            back.setOnClickListener(this@DoublePanaBulkFragment)
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
                if (isPointEnter() && edAmount.text.toString()
                        .toLong() < mPref.getMinBid(Constants.MIN_BID) && v?.id != R.id.tvGameType
                ) {
                    edAmount.error = "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                    return
                }

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
                            SinglePanaBulkFragmentDirections.actionGlobalGameTypeDialogFragment(
                                Constants.SINGLE_GAME_TYPE
                            )
                        findNavController().navigate(action)
                    }

                    R.id.btnZero -> {
                        if (isPointEnter()) {
                            getPanaList(0)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnOne -> {
                        if (isPointEnter()) {
                            getPanaList(1)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnTwo -> {
                        if (isPointEnter()) {
                            getPanaList(2)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnThree -> {
                        if (isPointEnter()) {
                            getPanaList(3)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnFour -> {
                        if (isPointEnter()) {
                            getPanaList(4)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnFive -> {
                        if (isPointEnter()) {
                            getPanaList(5)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnSix -> {
                        if (isPointEnter()) {
                            getPanaList(6)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnSeven -> {
                        if (isPointEnter()) {
                            getPanaList(7)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnEight -> {
                        if (isPointEnter()) {
                            getPanaList(8)
                        } else {
                            showErrorDialog()
                        }
                    }

                    R.id.btnNine -> {
                        if (isPointEnter()) {
                            getPanaList(9)
                        } else {
                            showErrorDialog()
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
                                gameTypeId,
                                type = mArgs.from,
                                marketId = mArgs.marketID,
                                games = mGamesList
                            )
                            val action =
                                DoublePanaFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, mArgs.from, mArgs.gameName, true
                                ).setTotalBids(mGamesList.size).setTotalPoints(mTotalAmount)
                            findNavController().navigate(action)

                        }
                    }
                }
            }
        }
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mPref.setSessionType(it)
                mBinding.tvGameType.text = it
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPanaList(panaNumber: Int) {
        Log.e("double pana", "" + panaNumber.doublePanaBulk())
        mDoublePanaList = panaNumber.doublePanaBulk()

        for (number in mDoublePanaList) {
            // check if the item is already in the gameList
            val existingItem = mGamesList.find { it.number.toString() == number.toString() }

            if (existingItem != null) {
                // if the item is already in the gameList, update its amount
                existingItem.amount =
                    existingItem.amount?.plus(mBinding.edAmount.text.toString().toInt())

            } else {
                mGamesList.add(
                    Game(
                        mBinding.edAmount.text.toString().toInt(),
                        number.toString(),
                        mPref.getSessionType(Constants.SESSION_TYPE),
                        gameTypeId,
                    )
                )
            }
        }

        updateTotalAmount()
        mGamesAddAdapter.notifyDataSetChanged()
    }

    private fun updateTotalAmount() {
        mTotalAmount = mGamesList.sumOf { it.amount!! } // calculate the total amount

        mBinding.apply {
            tvBids.text = mGamesList.size.toString()
            tvPoints.text = mTotalAmount.toString()
        }
    }

    private fun isPointEnter(): Boolean {
        return mBinding.edAmount.text.isNotEmpty()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun removeGameType(
        number: String, gameType: String?, position: Int, totalPoints: Int
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

    private fun showErrorDialog() {
        mBinding.edAmount.error = "Please enter point"
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
        if (!mArgs.openStatus) {
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(CLOSE_GAME_TYPE)
            mPref.setSessionType(CLOSE_GAME_TYPE)
        }else{
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.OPEN_GAME_TYPE)
            mPref.setSessionType(Constants.OPEN_GAME_TYPE)
        }
        val mGameType = mPref.getSessionType(Constants.SESSION_TYPE)
        mBinding.tvGameType.text = mGameType
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}