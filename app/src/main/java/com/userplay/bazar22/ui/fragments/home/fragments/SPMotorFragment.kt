package com.userplay.bazar22.ui.fragments.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSpMotorBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.GamesAddAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.SINGLE_PANA_GAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SPMotorFragment : Fragment(R.layout.fragment_sp_motor), View.OnClickListener,
    OnGameTypeListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSpMotorBinding? = null
    private val mBinding get() = _binding!!
    private var mGameType: String? = null
    private val mGamesList: ArrayList<Game> = ArrayList()
    private val mGamesAddAdapter by lazy { GamesAddAdapter(mGamesList, this, isTypeShow) }
    private var mTotalPoints: Int = 0
    private lateinit var mSendBody: SendBody
    private val mArgs: SPMotorFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var isTypeShow: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpMotorBinding.inflate(inflater, container, false)
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
            gameTypeId=10
            mBinding.gameTypeLyt.visibility = View.GONE
            mBinding.tvType.visibility = View.GONE
            isTypeShow = false
        }

        mBinding.apply {
            gameType.setOnClickListener(this@SPMotorFragment)
            tvGameType.setOnClickListener(this@SPMotorFragment)
            add.setOnClickListener(this@SPMotorFragment)
            finalSubmit.setOnClickListener(this@SPMotorFragment)
            back.setOnClickListener(this@SPMotorFragment)
            edNumber.validateSPMotorListinar()
         /*   edNumber.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(4),
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
            )*/
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
                                SINGLE_PANA_GAME
                            )
                        findNavController().navigate(action)
                    }

                    R.id.add -> {
                        if (edNumber.text.isNullOrEmpty()) {
                            edNumber.error = "Enter Valid Sp Motor Number"
                            return@let
                        } else if (!edNumber.validateSPMotor()) {
                            return@let
                        } else if (edAmount.text.isNullOrEmpty()) {
                            edAmount.error = "Please Enter Points"
                            return@let
                        } else if (edAmount.text.toString()
                                .toLong() < mPref.getMinBid(Constants.MIN_BID)
                        ) {
                            edAmount.error =
                                "Minimum Bid Amount is " + mPref.getMinBid(Constants.MIN_BID)
                            return@let
                        } else {
                            val list=edNumber.text.toString().getSpMotorData()
                            if (list.isEmpty()){
                                edNumber.error = "Enter Valid Sp Motor Number"
                            }else{
                                list.forEachIndexed { index, item ->
                                    mGamesList.add(
                                        Game(
                                            edAmount.text.toString().trim().toInt(),
                                            item.trim(),
                                            mGameType,
                                            gameTypeId,
                                        )
                                    )
                                    mTotalPoints += edAmount.text.toString().toInt()
                                }
                                mGamesAddAdapter.notifyDataSetChanged()
                                edNumber.text.clear()
                                edAmount.text.clear()
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
                                SPMotorFragmentDirections.actionGlobalSubmitGameDialogFragment(
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
        }else{
            val mSharedViewModels : SharedViewModels by activityViewModels()
            mSharedViewModels.setGameType(Constants.OPEN_GAME_TYPE)
            mPref.setSessionType(Constants.OPEN_GAME_TYPE)
        }
        mGameType = mPref.getSessionType(Constants.SESSION_TYPE)
        mBinding.tvGameType.text = mGameType
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun EditText.validateSPMotorListinar() {
        this.doOnTextChanged { text, start, before, count ->
            if (text.toString().isNotBlank() && text.toString().length>1){
                val newText=text.toString().substring(0,text.toString().lastIndex)
                if (newText.contains(text.toString()[text.toString().lastIndex])){
                    this.setText(newText)
                    this.setSelection(newText.length)
                }
            }
        }
    }

    private fun EditText.validateSPMotor(): Boolean {
        return if (this.text.isNotBlank() && this.length() >= 4) {
            this.error = null
            true

        } else {
            this.error = "Provide 4 Digits"
            false
        }
    }

    private fun String.getSpMotorData(): List<String> {
        val numberList = this.toSet().map { it.toString() }.sorted()
        val uniqueTrios = mutableListOf<String>()

        for (i in 0 until numberList.size - 2) {
            for (j in i + 1 until numberList.size - 1) {
                for (k in j + 1 until numberList.size) {
                    val trio = "${numberList[i]}${numberList[j]}${numberList[k]}"
                    if (!uniqueTrios.contains(trio)) {
                        if (trio.startsWith("0")) {
                            uniqueTrios.add("${trio.replace("0","")}0")
                        }else{
                            uniqueTrios.add(trio)
                        }
                    }
                }
            }
        }
        return uniqueTrios.sorted()
    }
}