package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSPDPTPBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.doublePanaBulk
import com.userplay.bazar22.utils.singlePanaBulk
import com.userplay.bazar22.utils.triplePanaBulk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SPDPTPFragment : Fragment(R.layout.fragment_s_p_d_p_t_p), View.OnClickListener,
    OnGameTypeListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSPDPTPBinding? = null
    private val mBinding get() = _binding!!
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mDoublePanaList: MutableList<Int> = ArrayList()
    private var mTotalAmount = 0 // define a variable to store the total amount
    private lateinit var mSendBody: SendBody
    private val mArgs: SPDPTPFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSPDPTPBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    var gameTypeId: Int = 3
    private fun initView() {

        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId = 10
            mBinding.gameTypeLyt.visibility = View.GONE
            mBinding.tvType.visibility = View.GONE
            isTypeShow = false
        }
        mBinding.apply {
            gameType.setOnClickListener(this@SPDPTPFragment)
            tvGameType.setOnClickListener(this@SPDPTPFragment)
            add.setOnClickListener(this@SPDPTPFragment)
            finalSubmit.setOnClickListener(this@SPDPTPFragment)
            back.setOnClickListener(this@SPDPTPFragment)

            checkBoxSP.setOnCheckedChangeListener { buttonView, isChecked ->
                /*    mGamesList.clear()
                    mGamesAddAdapter.notifyDataSetChanged()*/
            }
            checkBoxDP.setOnCheckedChangeListener { buttonView, isChecked ->
                /*  mGamesList.clear()
                  mGamesAddAdapter.notifyDataSetChanged()*/
            }
            checkBoxTP.setOnCheckedChangeListener { buttonView, isChecked ->
                /*   mGamesList.clear()
                   mGamesAddAdapter.notifyDataSetChanged()*/
            }
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

                    R.id.add -> {
                        if (isPointEnter() && mBinding.edDigits.text.toString().isNotEmpty()) {
                            if (mBinding.checkBoxSP.isChecked || mBinding.checkBoxDP.isChecked || mBinding.checkBoxTP.isChecked) {
                                getPanaList(mBinding.edDigits.text.toString().toInt())
                            } else {
                                Toast.makeText(activity, "Select Pana Types", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.final_submit -> {

                        if (mGamesList.size == 0) {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "Please add kuber")
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
        val session = mPref.getSessionType(Constants.SESSION_TYPE)
        if (mBinding.checkBoxSP.isChecked) {
            var gameTypeIdTemp = 3
            if (mArgs.from == Constants.STARLINE_MARKET)
                gameTypeIdTemp = 10


            val mSinglePanaList = panaNumber.singlePanaBulk()
            for (number in mSinglePanaList) {
                // check if the item is already in the gameList
                val existingItem =
                    mGamesList.find { it.number.toString() == number.toString() && session!!.lowercase() == it.session }

                if (existingItem != null) {
                    // if the item is already in the gameList, update its amount
                    existingItem.amount =
                        existingItem.amount?.plus(mBinding.edAmount.text.toString().toInt())

                } else {
                    mGamesList.add(
                        Game(
                            mBinding.edAmount.text.toString().toInt(),
                            number.toString(),
                            session,
                            gameTypeIdTemp,
                        )
                    )
                }
            }
        }
        if (mBinding.checkBoxDP.isChecked) {
            var gameTypeIdTemp = 4
            if (mArgs.from == Constants.STARLINE_MARKET)
                gameTypeIdTemp = 11
            mDoublePanaList = panaNumber.doublePanaBulk()
            for (number in mDoublePanaList) {
                // check if the item is already in the gameList
                val existingItem =
                    mGamesList.find { it.number.toString() == number.toString() && session!!.lowercase() == it.session }
                if (existingItem != null) {
                    // if the item is already in the gameList, update its amount
                    existingItem.amount =
                        existingItem.amount?.plus(mBinding.edAmount.text.toString().toInt())

                } else {
                    mGamesList.add(
                        Game(
                            mBinding.edAmount.text.toString().toInt(),
                            number.toString(),
                            session,
                            gameTypeIdTemp,
                        )
                    )
                }
            }
        }
        if (mBinding.checkBoxTP.isChecked) {
            var gameTypeIdTemp = 5
            if (mArgs.from == Constants.STARLINE_MARKET)
                gameTypeIdTemp = 12
            val mTriplePanaList = panaNumber.triplePanaBulk()
            for (number in mTriplePanaList) {

                // check if the item is already in the gameList
                val existingItem =
                    mGamesList.find { it.number.toString() == number.toString() && session!!.lowercase() == it.session }
                if (existingItem != null) {
                    // if the item is already in the gameList, update its amount
                    existingItem.amount =
                        existingItem.amount?.plus(mBinding.edAmount.text.toString().toInt())
                } else {
                    mGamesList.add(
                        Game(
                            mBinding.edAmount.text.toString().toInt(),
                            if(number==0){"000"}else{number.toString()},
                            session,
                            gameTypeIdTemp,
                        )
                    )
                }
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
            val mSharedViewModels: SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.CLOSE_GAME_TYPE)
            mPref.setSessionType(Constants.CLOSE_GAME_TYPE)
        } else {
            val mSharedViewModels: SharedViewModels by activityViewModels()
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