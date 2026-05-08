package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
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
import com.userplay.bazar22.databinding.FragmentPanaFamilyBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.fragments.home.models.NumberParentDataModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.SESSION_TYPE
import com.userplay.bazar22.utils.Constants.TRIPLE_PANA_GAME_TYPE
import com.userplay.bazar22.utils.getDoublePanaData
import com.userplay.bazar22.utils.getListPanaFamily
import com.userplay.bazar22.utils.getSinglePanaData
import com.userplay.bazar22.utils.getTripplePanaData
import com.userplay.bazar22.utils.validatePanaFamily
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PanaFamilyFragment : Fragment(R.layout.fragment_pana_family), View.OnClickListener,
    OnGameTypeListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentPanaFamilyBinding? = null
    private val mBinding get() = _binding!!
    private var mGameType: String? = null
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private var isTypeShow: Boolean = true
    private val mArgs: TriplePanaFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    var list: HashMap<String, List<String>> = hashMapOf()
    var listSinglePana: ArrayList<NumberParentDataModel> = arrayListOf()
    var listDoublePana: ArrayList<NumberParentDataModel> = arrayListOf()
    var listTripplePana: ArrayList<NumberParentDataModel> = arrayListOf()
    var suggestedList: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPanaFamilyBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listSinglePana = getSinglePanaData()
        listDoublePana = getDoublePanaData()
        listTripplePana = getTripplePanaData()
        list = getListPanaFamily()
        list.forEach {
            suggestedList.addAll(it.value)
        }
        suggestedList = suggestedList.toSet().toMutableList()
        initView()
        observer()
    }

    var gameTypeId: Int = 0
    private fun initView() {
        if (mArgs.from == Constants.STARLINE_MARKET) {
            gameTypeId=12
            mBinding.gameTypeLyt.visibility = View.GONE
            mBinding.tvType.visibility = View.GONE
            isTypeShow = false
        }

        mBinding.apply {
            gameType.setOnClickListener(this@PanaFamilyFragment)
            tvGameType.setOnClickListener(this@PanaFamilyFragment)
            add.setOnClickListener(this@PanaFamilyFragment)
            finalSubmit.setOnClickListener(this@PanaFamilyFragment)
            back.setOnClickListener(this@PanaFamilyFragment)
            edNumber.filters = arrayOf<InputFilter>(
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
        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesAddAdapter
        }
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mGameType?.observe(viewLifecycleOwner) {
                mGameType = it
                mPref.setSessionType(it)
                mBinding.tvGameType.text = it
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
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
                            SingleDigitsFragmentDirections.actionGlobalGameTypeDialogFragment(
                                TRIPLE_PANA_GAME_TYPE
                            )
                        findNavController().navigate(action)
                    }

                    R.id.add -> {
                        if (edNumber.text.length != 3) {
                            edNumber.error = "Enter Valid Pana Family"
                            return@let
                        } else if (edNumber.text.isNullOrEmpty()) {
                            edNumber.error = "Enter Valid Pana Family"
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
                            if (edNumber.validatePanaFamily()) {
                                if (suggestedList.contains(edNumber.text.toString())) {
                                    val findKey =
                                        list.filter { it.value.contains(edNumber.text.toString()) }
                                    if (findKey.isNotEmpty()) {
                                        edNumber.error = null
                                        findKey.forEach { (s, list) ->
                                            list.forEach {
                                                getGameTypeId(it) {itemGameTypeId->
                                                    mGamesList.add(
                                                        Game(
                                                            edAmount.text.toString().trim().toInt(),
                                                            it.trim(),
                                                            mGameType,
                                                            itemGameTypeId,
                                                        )
                                                    )
                                                    val amount = edAmount.text.toString().toInt()
                                                    mTotalPoints += amount
                                                }
                                            }
                                        }

                                        mGamesAddAdapter.notifyDataSetChanged()
                                        edNumber.text.clear()
                                        edAmount.text.clear()
                                    } else {
                                        edNumber.error = "Invalid Pana Family"
                                    }
                                } else {
                                    edNumber.error = "Invalid Pana Family"
                                }
                            }else {
                                edNumber.error = "Invalid Pana Family"
                            }
                        }
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
                                SinglePanaFragmentDirections.actionGlobalSubmitGameDialogFragment(
                                    mSendBody, mArgs.from, mArgs.gameName, true
                                ).setTotalBids(mGamesList.size).setTotalPoints(mTotalPoints)
                            findNavController().navigate(action)
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setFinalSubmitData(totalBids: Int, totalPoints: Int) {
        mBinding.apply {
            tvBids.text = totalBids.toString()
            tvPoints.text = totalPoints.toString()
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

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
        if (!mArgs.openStatus) {
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(CLOSE_GAME_TYPE)
            mPref.setSessionType(CLOSE_GAME_TYPE)
        }
        mGameType = mPref.getSessionType(SESSION_TYPE)
        mBinding.tvGameType.text = mGameType
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun getGameTypeId(number: String, result: (gameTypeId: Int) -> Unit) {
        val foundSinglePanaItem = listSinglePana.find { outerItem ->
            outerItem.numberDataList.any { innerItem ->
                innerItem.number == number
            }
        }
        if (foundSinglePanaItem != null) {
            result.invoke(3)
        }
        val foundDoublePanaItem = listDoublePana.find { outerItem ->
            outerItem.numberDataList.any { innerItem ->
                innerItem.number == number
            }
        }

        if (foundDoublePanaItem != null) {
            result.invoke(4)
        }
        val foundTripplePanaItem = listTripplePana.find { outerItem ->
            outerItem.numberDataList.any { innerItem ->
                innerItem.number == number
            }
        }
        if (foundTripplePanaItem != null) {
            result.invoke(5)
        }
    }

}