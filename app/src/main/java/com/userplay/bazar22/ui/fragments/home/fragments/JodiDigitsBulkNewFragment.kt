package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentJodiDigitsBulkNewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.fragments.home.adapters.NumbersAdapter
import com.userplay.bazar22.ui.fragments.home.adapters.PairsDigitsAdapter
import com.userplay.bazar22.ui.fragments.home.models.NumbersModel
import com.userplay.bazar22.ui.fragments.home.models.PairsModelDigits
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.jodiDigitsPairs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class JodiDigitsBulkNewFragment : Fragment(R.layout.fragment_jodi_digits_bulk_new), OnGameTypeListener {
    private var _binding: FragmentJodiDigitsBulkNewBinding? = null
    var mAdapter: NumbersAdapter?=null
    var mAdapterPair: PairsDigitsAdapter?=null
    @Inject
    lateinit var mPref: MatkaPref
    private val mBinding get() = _binding!!
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: JodiDigitsBulkNewFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    var gameTypeId: Int = 2
    var listPairNumbers:ArrayList<PairsModelDigits> = arrayListOf(
        PairsModelDigits("00",""),
        PairsModelDigits("01",""),
        PairsModelDigits("02",""),
        PairsModelDigits("03",""),
        PairsModelDigits("04",""),
        PairsModelDigits("05",""),
        PairsModelDigits("06",""),
        PairsModelDigits("07",""),
        PairsModelDigits("08",""),
        PairsModelDigits("09","")
    )
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
        _binding = FragmentJodiDigitsBulkNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observer()
    }

    private fun initViews() {
        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId=2
            /*    mBinding.gameTypeLyt.visibility = View.GONE
                mBinding.tvType.visibility = View.GONE*/
            isTypeShow = false
        }

        refreshAdapter()
        mBinding.rvPairs.apply {
            mBinding.rvPairs.layoutManager= GridLayoutManager(activity,2)
        }
        mBinding.rvGamesList.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }




        mBinding.btnAdd.setOnClickListener {
            val find= mAdapterPair?.getList()?.filter{ it.pairPoint!="" && it.pairPoint.toInt()>=mPref.getMinBid(
                Constants.MIN_BID)}
            find?.let {
                if(find.isNotEmpty()){
                    find.forEach {item->
                        if(item.pairNumber.toString().length==1){
                            mGamesList.add(
                                Game(
                                    item.pairPoint.trim().toInt(),
                                    "0"+item.pairNumber.toString(),
                                    Constants.NULL_GAME_TYPE,
                                    gameTypeId = 2,
                                )
                            )
                        }else{
                            mGamesList.add(
                                Game(
                                    item.pairPoint.trim().toInt(),
                                    item.pairNumber.toString(),
                                    Constants.NULL_GAME_TYPENULL_GAME_TYPE,
                                    gameTypeId = 2,
                                )
                            )
                        }

                        mTotalPoints +=item.pairPoint.trim().toInt()
                    }
                    /*updatePairs(listNumbers[lastSelected].number)*/
                    mGamesAddAdapter.notifyDataSetChanged()
                    refreshAdapter()
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

            }
        }
        mBinding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observer() {
        activity?.let {
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
        mAdapterPair?.addList(number.jodiDigitsPairs())
    }
    var lastSelected=0
    fun refreshAdapter(){
        listNumbers.find {it.isSelected}?.isSelected=false
        listNumbers[0].isSelected=true
        mAdapter= NumbersAdapter(listNumbers,object : ItemClickListener {
            override fun onItemClick(position: Int) {
                lastSelected=position
                listNumbers[position].isSelected=true
                mAdapter?.updateList(listNumbers)
                updatePairs(listNumbers[position].number)
            }
        })
        mBinding.rvNumbers.adapter=mAdapter
        mAdapterPair= PairsDigitsAdapter(listPairNumbers,mPref.getMinBid(Constants.MIN_BID),object :
            ItemClickListener {
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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}