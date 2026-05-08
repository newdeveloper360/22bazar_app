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
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSingleDigitsBulkBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.SingleDigitsBulkAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.getSingleDigitsBulkList
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleDigitsBulkFragment : Fragment(R.layout.fragment_single_digits_bulk),
    View.OnClickListener, ItemClickListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSingleDigitsBulkBinding? = null
    private val mBinding get() = _binding!!
    private val mArgs: SingleDigitsBulkFragmentArgs by navArgs()
    private var mGameList: ArrayList<Game> = ArrayList()
    private lateinit var mSendBody: SendBody
    private var mTotalAmount: Int = 0
    private var mGameType: String? = null
    private val mSharedViewModels: SharedViewModels by activityViewModels()

    private val mAdapter: SingleDigitsBulkAdapter by lazy {
        SingleDigitsBulkAdapter(mGameList, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleDigitsBulkBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    var gameTypeId: Int = 1
    private fun initView() {

        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId = 9
            mBinding.gameTypeLyt.visibility = View.GONE
        }

        for (item in getSingleDigitsBulkList()) {
            mGameList.add(
                Game(
                    0, item.toString(), mPref.getSessionType(Constants.SESSION_TYPE), gameTypeId
                )
            )
        }

        mBinding.apply {
            finalSubmit.setOnClickListener(this@SingleDigitsBulkFragment)
        }


        activity?.let {
            val layoutManager = FlexboxLayoutManager(it)
            layoutManager.apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
                mBinding.recyclerView.layoutManager = layoutManager
                mBinding.recyclerView.adapter = mAdapter
            }
        }

        mBinding.apply {
            back.setOnClickListener(this@SingleDigitsBulkFragment)
            gameType.setOnClickListener(this@SingleDigitsBulkFragment)
            tvGameType.setOnClickListener(this@SingleDigitsBulkFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {
        activity?.let {
            mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mGameType = it
                mPref.setSessionType(it)
                mBinding.tvGameType.text = it

                for (item in mGameList) {
                    item.session = mPref.getSessionType(Constants.SESSION_TYPE)
                }

                mAdapter.notifyDataSetChanged()
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.final_submit -> {
                        // Check if the amount of any userplay has changed
                        val newSize = mGameList.filter { it.amount != 0 }.size

                        if (newSize != 0) {
                            mSendBody = SendBody(gameTypeId,
                                type = mArgs.from,
                                marketId = mArgs.marketID,
                                games = mGameList.filter { it.amount != 0 })

                            val action =
                                SingleDigitsBulkFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, mArgs.from, mArgs.gameName, true
                                ).setTotalBids(newSize).setTotalPoints(mTotalAmount)
                            findNavController().navigate(action)

                        } else {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "please select number")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")
                        }
                    }

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
                            SingleDigitsBulkFragmentDirections.actionGlobalGameTypeDialogFragment(
                                Constants.SINGLE_BULK_GAME_TYPE
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
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
        mGameType = mPref.getSessionType(Constants.SESSION_TYPE)
        mBinding.tvGameType.text = mGameType
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(position: Int) {
        // Get the selected userplay

        if (mBinding.edAmount.text.isNullOrEmpty()) {
            mBinding.edAmount.error = "please select amount"
        } else if (mBinding.edAmount.text.toString()
                .toLong() < mPref.getMinBid(Constants.MIN_BID)
        ) {
            mBinding.edAmount.error = "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
            return
        } else {
            val game: Game = mGameList[position]
            // Get the amount from the EditText
            val amountStr: String = mBinding.edAmount.text.trim().toString()
            val amount = amountStr.toInt()
            // Add the amount to the userplay's amount
            game.amount = game.amount?.plus(amount)
            // Notify the adapter that the data has changed
            mAdapter.notifyDataSetChanged()
            getBidsSize()
        }
    }


    private fun getBidsSize() {
        // Assume that `gameList` is the list of games
        val initialSize = mGameList.size

        // Check if the amount of any userplay has changed
        val newSize = mGameList.filter { it.amount != 0 }.size

        //changed
//        if (newSize != initialSize) {
        // The size of the list has changed
        mBinding.tvBids.text = newSize.toString()
        mTotalAmount = mGameList.sumBy { it.amount!! }
        mBinding.tvPoints.text = mTotalAmount.toString()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}