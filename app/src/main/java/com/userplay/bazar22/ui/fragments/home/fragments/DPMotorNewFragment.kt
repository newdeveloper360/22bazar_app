package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentDpMotorNewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.fragments.home.adapters.NumbersAdapter
import com.userplay.bazar22.ui.fragments.home.adapters.PairsAdapter
import com.userplay.bazar22.ui.fragments.home.models.NumbersModel
import com.userplay.bazar22.ui.fragments.home.models.PairsModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DPMotorNewFragment : Fragment(R.layout.fragment_dp_motor_new), OnGameTypeListener  {
    private var _binding: FragmentDpMotorNewBinding? = null
    var mAdapter:NumbersAdapter?=null
    var mAdapterPair:PairsAdapter?=null
    @Inject
    lateinit var mPref: MatkaPref
    private val mBinding get() = _binding!!
    private var mGameType: String? = null
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: DPMotorNewFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    var gameTypeId: Int = 4
    var listPairNumbers:ArrayList<PairsModel> = getDpMotorNumber("0")
    var listNumbers:ArrayList<NumbersModel> = arrayListOf(
        NumbersModel(number = 0,isSelected = true),
        NumbersModel(number = 1),
        NumbersModel(number = 2),
        NumbersModel(number = 3),
        NumbersModel(number = 4),
        NumbersModel(number = 5),
        NumbersModel(number = 6),
        NumbersModel(number = 7),
        NumbersModel(number = 8),
        NumbersModel(number = 9),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDpMotorNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observer()
    }

    private fun initViews() {
        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId=11
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
                   updatePairs(listNumbers[lastSelected].number)
                   mGamesAddAdapter.notifyDataSetChanged()
                   //refreshAdapter()
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
                mSendBody = SendBody(
                    gameTypeId,
                    type = mArgs.from,
                    marketId = mArgs.marketID,
                    games = mGamesList
                )
                val action =
                    DPMotorNewFragmentDirections.actionGlobalSubmitGameDialogFragment(
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

    fun updatePairs(number:Int){
        mAdapterPair?.updateList(getDpMotorNumber(number.toString()))
    }
    var lastSelected=0
    fun refreshAdapter(){
        listNumbers.find {it.isSelected}?.isSelected=false
        listNumbers[0].isSelected=true
        mAdapter= NumbersAdapter(listNumbers,object :ItemClickListener{
            override fun onItemClick(position: Int) {
                lastSelected=position
                listNumbers.find {it.isSelected}?.isSelected=false
                listNumbers[position].isSelected=true
                mAdapter?.updateList(listNumbers)
                updatePairs(listNumbers[position].number)
            }
        })
        mBinding.rvNumbers.adapter=mAdapter
        mAdapterPair= PairsAdapter(listPairNumbers,mPref.getMinBid(Constants.MIN_BID),object :ItemClickListener{
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
            mGameType="close"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }






    private fun getDpMotorNumber(selectedDigit: String): ArrayList<PairsModel> {
        val allDigits = ('0'..'9').toList()
        val output = mutableSetOf<String>()

        for (d1 in allDigits) {
            for (d2 in allDigits) {
                if (d1 != d2) {
                    if (selectedDigit != d1.toString() && selectedDigit != d2.toString()) continue
                    val combination1 = "$d1$d1$d2"
                    val combination2 = "$d2$d2$d1"

                    output.add(formatNumber(combination1))
                    output.add(formatNumber(combination2))
                }
            }
        }
        return output.sorted().map { PairsModel(it.toInt()) }.toCollection(ArrayList())
    }

    /**
     * Ensures correct formatting:
     * - Moves '0' to the end if it appears first
     */
    private fun formatNumber(number: String): String {
        return if (number.startsWith("0")) {
            number.replaceFirst("0", "") + "0"
        } else {
            number
        }
    }

}
