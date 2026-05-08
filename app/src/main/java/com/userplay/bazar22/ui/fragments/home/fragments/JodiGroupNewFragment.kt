package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentJodiGroupNewBinding
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JodiGroupNewFragment : Fragment(R.layout.fragment_jodi_group_new), OnGameTypeListener {
    private var _binding: FragmentJodiGroupNewBinding? = null
    var mAdapter: NumbersAdapter?=null
    var mAdapterPair: PairsDigitsAdapter?=null
    @Inject
    lateinit var mPref: MatkaPref
    private val mBinding get() = _binding!!
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: JodiGroupNewFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    var gameTypeId: Int = 2
    var listPairNumbers:ArrayList<PairsModelDigits> = getJodiGroupNumbers(0)
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
        _binding = FragmentJodiGroupNewBinding.inflate(inflater, container, false)
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
                            mGamesList.add(
                                Game(
                                    item.pairPoint.trim().toInt(),
                                    if(item.pairNumber.toString().length==2){item.pairNumber}else{"0"+item.pairNumber},
                                    Constants.NULL_GAME_TYPE,
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
        mAdapterPair?.updateList(getJodiGroupNumbers(number))
    }
    var lastSelected=0
    fun refreshAdapter(){
        listNumbers.find {it.isSelected}?.isSelected=false
        listNumbers[0].isSelected=true
        mAdapter= NumbersAdapter(listNumbers,object : ItemClickListener {
            override fun onItemClick(position: Int) {
                lastSelected=position
                listNumbers.find {it.isSelected}?.isSelected=false
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

   /* private fun getJodiGroupNumbers(jodi: Int): ArrayList<PairsModelDigits> {
        val numbersList:MutableList<PairsModelDigits> = arrayListOf()
        for(i in 0..9) {
           val jodiStr= "$jodi$i"
            val firstDigit = jodiStr[0].digitToInt()
            val secondDigit = jodiStr[1].digitToInt()
            val firstSet = listOf(firstDigit * 10 + secondDigit, firstDigit * 10 + (secondDigit + 5) % 10)
            val secondSet = listOf(secondDigit * 10 + firstDigit, secondDigit * 10 + (firstDigit + 5) % 10)
            val thirdSet = listOf((firstDigit + 5) % 10 * 10 + secondDigit, (firstDigit + 5) % 10 * 10 + (secondDigit + 5) % 10)
            val fourthSet = listOf((secondDigit + 5) % 10 * 10 + firstDigit, (secondDigit + 5) % 10 * 10 + (firstDigit + 5) % 10)
            val list=(firstSet + secondSet + thirdSet + fourthSet).distinct()
            numbersList.addAll(list.map { PairsModelDigits(pairNumber = it.toString().takeIf { it.length==2 }?:"0$it")  })
        }
        return numbersList.distinct().toCollection(ArrayList())
       }*/

    private fun getJodiGroupNumbers(jodi: Int): ArrayList<PairsModelDigits> {
        val jodiStr = jodi.toString().padStart(2, '0')
        val firstDigit = jodiStr[0].digitToInt()
        val secondDigit = jodiStr[1].digitToInt()

        val firstSet = listOf(firstDigit * 10 + secondDigit, firstDigit * 10 + (secondDigit + 5) % 10)
        val secondSet = listOf(secondDigit * 10 + firstDigit, secondDigit * 10 + (firstDigit + 5) % 10)
        val thirdSet = listOf((firstDigit + 5) % 10 * 10 + secondDigit, (firstDigit + 5) % 10 * 10 + (secondDigit + 5) % 10)
        val fourthSet = listOf((secondDigit + 5) % 10 * 10 + firstDigit, (secondDigit + 5) % 10 * 10 + (firstDigit + 5) % 10)

        return (firstSet + secondSet + thirdSet + fourthSet).distinct().map { PairsModelDigits(it.toString().takeIf { it.length==2 }?:"0$it") }.toCollection(ArrayList())
    }
}
