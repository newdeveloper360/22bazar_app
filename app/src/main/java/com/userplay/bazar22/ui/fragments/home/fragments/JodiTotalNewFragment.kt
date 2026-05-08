package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.userplay.bazar22.databinding.FragmentJodiTotalNewBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.GameTotal
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.models.SendBodyTotal
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
class JodiTotalNewFragment : Fragment(R.layout.fragment_jodi_total_new), OnGameTypeListener {
    private var _binding: FragmentJodiTotalNewBinding? = null
    var mAdapterPair: PairsDigitsAdapter? = null

    @Inject
    lateinit var mPref: MatkaPref
    private val mBinding get() = _binding!!
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesListActual: ArrayList<GameTotal> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow, true) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: SingleDigitNewFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    var gameTypeId: Int = 2
    var listPairNumbers: ArrayList<PairsModelDigits> = arrayListOf(
        PairsModelDigits("00", ""),
        PairsModelDigits("01", ""),
        PairsModelDigits("02", ""),
        PairsModelDigits("03", ""),
        PairsModelDigits("04", ""),
        PairsModelDigits("05", ""),
        PairsModelDigits("06", ""),
        PairsModelDigits("07", ""),
        PairsModelDigits("08", ""),
        PairsModelDigits("09", "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJodiTotalNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observer()
    }

    private fun initViews() {
        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId = 2
            /*    mBinding.gameTypeLyt.visibility = View.GONE
                mBinding.tvType.visibility = View.GONE*/
            isTypeShow = false
        }

        refreshAdapter()
        mBinding.rvPairs.apply {
            mBinding.rvPairs.layoutManager = GridLayoutManager(activity, 2)
        }
        mBinding.rvGamesList.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }



        mBinding.btnAdd.setOnClickListener {
            val find = mAdapterPair?.getList()
                ?.filter { it.pairPoint != "" && it.pairPoint.toInt() >= mPref.getMinBid(Constants.MIN_BID) }
            find?.let {
                if (find.isNotEmpty()) {
                    find.forEach { item ->
                        mGamesList.add(
                            Game(
                                item.pairPoint.trim().toInt(),
                                item.pairNumber.toString(),
                                Constants.CLOSE_GAME_TYPE,
                                gameTypeId,
                            )
                        )
                        val listJodiTotal=generateJodiNumbers(item.pairNumber.toInt())
                        listJodiTotal.forEach {
                            mGamesListActual.add(
                                GameTotal(
                                    item.pairPoint.trim().toDouble()/listJodiTotal.size,
                                    it.takeIf { it.length==2 }?:"0$it",
                                    Constants.CLOSE_GAME_TYPE,
                                    gameTypeId,
                                )
                            )
                        }
                        mTotalPoints += item.pairPoint.trim().toInt()
                    }
                    updatePairs()
                    mGamesAddAdapter.notifyDataSetChanged()
                }
            }
        }
        mBinding.btnSubmit.setOnClickListener {
            if (mGamesListActual.size == 0) {
                val bundle = Bundle()
                val dialog = ErrorDialogFragment()
                bundle.putString("message", "Please add userplay")
                dialog.arguments = bundle
                dialog.show(childFragmentManager, "error")

            } else {
                Log.e("Jodi Total", "" + mArgs.from)
                mSendBody = SendBody(
                    gameTypeId,
                    type = mArgs.from,
                    marketId = mArgs.marketID,
                    games = mGamesList
                )

                val mSendBodyActual = SendBodyTotal(
                    gameTypeId,
                    type = mArgs.from,
                    marketId = mArgs.marketID,
                    games = mGamesListActual
                )

                val action =
                    JodiTotalNewFragmentDirections.actionGlobalSubmitGameTotalJodiDialogFragment(
                        mSendBody, mSendBodyActual, mArgs.from, mArgs.gameName, false
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
            /*mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mPref.setSessionType(it)
                if (it.lowercase().contains("close")) {
                    mBinding.rbOpen.isChecked = false
                    mBinding.rbClose.isChecked = true
                } else {
                    mBinding.rbOpen.isChecked = true
                    mBinding.rbClose.isChecked = false
                }
            }*/

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

    fun updatePairs() {
        mAdapterPair?.updateList(singleDigitsPairs())
    }

    var lastSelected = 0
    fun refreshAdapter() {
        mAdapterPair = PairsDigitsAdapter(
            listPairNumbers,
            mPref.getMinBid(Constants.MIN_BID),
            object : ItemClickListener {
                override fun onItemClick(position: Int) {
                }
            })
        mBinding.rvPairs.apply {
            mBinding.rvPairs.adapter = mAdapterPair
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
            val mSharedViewModels: SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.CLOSE_GAME_TYPE)
            mPref.setSessionType(Constants.CLOSE_GAME_TYPE)
        } else {
            val mSharedViewModels: SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.OPEN_GAME_TYPE)
            mPref.setSessionType(Constants.OPEN_GAME_TYPE)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun generateJodiNumbers(targetSum: Int): List<String> {
        val jodiList = mutableListOf<String>()

        for (i in 0..99) {
            val numStr = i.toString().padStart(2, '0')

            val digitSum = numStr[0].digitToInt() + numStr[1].digitToInt()
            if (digitSum == targetSum || digitSum.toString().endsWith(targetSum.toString())) {
                jodiList.add(numStr)
            }
        }

        return jodiList
    }
}