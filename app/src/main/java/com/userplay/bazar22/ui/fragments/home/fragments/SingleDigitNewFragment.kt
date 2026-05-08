package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSingleDigitNewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.fragments.home.adapters.PairsDigitsAdapter
import com.userplay.bazar22.ui.fragments.home.models.PairsModelDigits
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.singleDigitsPairs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleDigitNewFragment : Fragment(R.layout.fragment_single_digit_new), OnGameTypeListener  {
    private var _binding: FragmentSingleDigitNewBinding? = null
    var mAdapterPair:PairsDigitsAdapter?=null
    @Inject
    lateinit var mPref: MatkaPref
    private val mBinding get() = _binding!!
    private var mGameType: String? = null
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow,true) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: SingleDigitNewFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    var gameTypeId: Int = 1
    var listPairNumbers:ArrayList<PairsModelDigits> = arrayListOf(
        PairsModelDigits("0",""),
        PairsModelDigits("1",""),
        PairsModelDigits("2",""),
        PairsModelDigits("3",""),
        PairsModelDigits("4",""),
        PairsModelDigits("5",""),
        PairsModelDigits("6",""),
        PairsModelDigits("7",""),
        PairsModelDigits("8",""),
        PairsModelDigits("9","")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSingleDigitNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observer()
    }

    private fun initViews() {
        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId=9
            /*    mBinding.gameTypeLyt.visibility = View.GONE
                mBinding.tvType.visibility = View.GONE*/
            isTypeShow = false
        }

        refreshAdapter()
        mBinding.rvPairs.apply {
            mBinding.rvPairs.layoutManager=GridLayoutManager(activity,2)
        }
        mBinding.rvGamesList.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }
        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (mBinding.rbOpen.isChecked) {
                if (!mArgs.openStatus) {
                    mGameType = "close"
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.open_game_already_set),
                        Toast.LENGTH_SHORT
                    ).show()
                    mBinding.rbClose.isChecked = true
                    mBinding.rbOpen.isChecked = false
                } else {
                    mGameType = "open"
                    mBinding.rbClose.isChecked = false
                }
            }else{
                mGameType = "close"
                mBinding.rbClose.isChecked = true
            }
        }



        mBinding.btnAdd.setOnClickListener {
            val find= mAdapterPair?.getList()?.filter{ it.pairPoint!="" && it.pairPoint.toInt()>=mPref.getMinBid(Constants.MIN_BID)}
            find?.let {
                if(find.isNotEmpty()){
                    find.forEach {item->
                        mGamesList.add(
                            Game(
                                item.pairPoint.trim().toInt(),
                                item.pairNumber.toString(),
                                mGameType,
                                gameTypeId,
                            )
                        )
                        mTotalPoints +=item.pairPoint.trim().toInt()
                    }
                    updatePairs()
                    mGamesAddAdapter.notifyDataSetChanged()
                }
            }
        }
        mBinding.btnSubmit.setOnClickListener {
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
        mBinding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mGameType = it
                mPref.setSessionType(it)
                if(it.lowercase().contains("close")){
                    mBinding.rbOpen.isChecked=false
                    mBinding.rbClose.isChecked=true
                }else{
                    mBinding.rbOpen.isChecked=true
                    mBinding.rbClose.isChecked=false
                }
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
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

    override fun updateSubmitResult(totalPoints: Int) {
        setFinalSubmitData(mGamesList.size, mTotalPoints)
    }

    override fun onCrossingInserted(amount: String, number: String, position: Int) {

    }

    fun updatePairs(){
        mAdapterPair?.updateList(singleDigitsPairs())
    }
    var lastSelected=0
    fun refreshAdapter(){
        mAdapterPair= PairsDigitsAdapter(listPairNumbers,mPref.getMinBid(Constants.MIN_BID),object :ItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })
        mBinding.rvPairs.apply {
            mBinding.rvPairs.adapter=mAdapterPair
        }
    }

    private fun setFinalSubmitData(totalBids: Int, totalPoints: Int) {
        mBinding.tvBids.text = totalBids.toString()
        mBinding.tvPoints.text = totalPoints.toString()
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
        if (!mArgs.openStatus) {
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.CLOSE_GAME_TYPE)
            mPref.setSessionType(Constants.CLOSE_GAME_TYPE)
        }else{
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.OPEN_GAME_TYPE)
            mPref.setSessionType(Constants.OPEN_GAME_TYPE)
        }
        mGameType = mPref.getSessionType(Constants.SESSION_TYPE)
        mGameType?.let {
            if(mGameType!!.lowercase().contains("close")){
                mBinding.rbOpen.isChecked=false
                mBinding.rbClose.isChecked=true
            }else{
                mBinding.rbOpen.isChecked=true
                mBinding.rbClose.isChecked=false
            }
        }?: kotlin.run {
            mGameType="open"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}