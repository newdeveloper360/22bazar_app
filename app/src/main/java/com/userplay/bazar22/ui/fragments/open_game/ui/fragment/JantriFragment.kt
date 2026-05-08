package com.userplay.bazar22.ui.fragments.open_game.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentJantriBinding
import com.userplay.bazar22.models.Game
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.models.jantari_model.JantariResponseItem
import com.userplay.bazar22.models.jantri_request.JantriDetailExpo
import com.userplay.bazar22.models.jantri_request.JantriExpoSummury
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameTypeListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.fragments.open_game.adapter.JantariParentAdapter
import com.userplay.bazar22.ui.fragments.open_game.callback.JantariListener
import com.userplay.bazar22.ui.fragments.open_game.ui.activity.OpenGameActivity
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream


@AndroidEntryPoint
class JantriFragment : Fragment(R.layout.fragment_jantri), OnGameTypeListener, View.OnClickListener,
    JantariListener {

    private var _binding: FragmentJantriBinding? = null
    private val mBinding get() = _binding!!
    private var mGameList: ArrayList<Game> = ArrayList()
    private var mTempList: ArrayList<Game> = ArrayList()
    private lateinit var mJantriNormal: JantriExpoSummury
    private lateinit var mAHaruf: JantriExpoSummury
    private lateinit var mBHaruf: JantriExpoSummury
//    private var mJantriSummuryList: ArrayList<JantriExpoSummury> = ArrayList()

    //    private lateinit var mJantriNumberList: ArrayList<JantriDetailExpo>
    private lateinit var mJantriAHarufList: ArrayList<JantriDetailExpo>
    private lateinit var mJantriBHarufList: ArrayList<JantriDetailExpo>

    private lateinit var mSendBody: SendBody
    private var mTotalAmount: Int = 0

    //    private val mJantriAdapter by lazy { JantriAdapter(mJantriSummuryList, this) }
    private val mJantriAdapter by lazy { JantariParentAdapter(mList, this) }
    private lateinit var mInputStream: InputStream
    private lateinit var mJsonString: String
    private val gson = Gson()

    private var mList: ArrayList<JantariResponseItem> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJantriBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        activity?.let {
            mList.clear()
            mInputStream = it.assets.open("JantriData.json")
            mJsonString = mInputStream.bufferedReader().use { it.readText() }

            val jantriList =
                gson.fromJson(mJsonString, Array<JantariResponseItem>::class.java).toList()
            mList.addAll(jantriList)

        }
        mBinding.finalSubmit.setOnClickListener(this)


//        mJantriNumberList = ArrayList()
//        mJantriAHarufList = ArrayList()
//        mJantriBHarufList = ArrayList()
//
//        for (item in getJantri()) {
//            mJantriNumberList.add(JantriDetailExpo(item.jantriNumber))
//        }
//
//        for (item in getAnderHaruf()) {
//            mJantriAHarufList.add(JantriDetailExpo(item))
//        }
//
//        for (item in getBharHaruf()) {
//            mJantriBHarufList.add(JantriDetailExpo(item))
//        }
//

//        mJantriNormal = JantriExpoSummury(mJantriNumberList, JantriSectionDetails("Jantri"))
//        mAHaruf = JantriExpoSummury(mJantriAHarufList, JantriSectionDetails("Andar Haruf"))
//        mBHaruf = JantriExpoSummury(mJantriBHarufList, JantriSectionDetails("Bahar Haruf"))


        mBinding.apply {
            mBinding.recyclerViewJori.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = mJantriAdapter
            }
        }


//
//        mJantriSummuryList.add(mJantriNormal)
//        mJantriSummuryList.add(mAHaruf)
//        mJantriSummuryList.add(mBHaruf)

        mJantriAdapter.notifyDataSetChanged()
    }

    private fun observer() {

    }

    override fun removeGameType(
        number: String,
        gameType: String?,
        position: Int,
        totalPoints: Int
    ) {

    }

    override fun updateSubmitResult(totalPoints: Int) {

    }

    override fun onCrossingInserted(amount: String, number: String, position: Int) {

    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.final_submit -> {

                        if (mGameList.size == 0) {
                            val bundle = Bundle()
                            val dialog = ErrorDialogFragment()
                            bundle.putString("message", "Please add userplay")
                            dialog.arguments = bundle
                            dialog.show(childFragmentManager, "error")
                        } else {

                            mSendBody = SendBody(
                                gameTypeId = 13,
                                type = DESAWAR_MARKET,
                                marketId = (activity as? OpenGameActivity)?.mMarketID,
                                games = mGameList
                            )

                            val action =
                                JantriFragmentDirections.actionJantriFragmentToSubmitGameDialogFragment2(
                                    mSendBody,
                                    DESAWAR_MARKET,
                                    (activity as? OpenGameActivity)?.mGameName.toString(),
                                    false
                                ).setTotalBids(mGameList.size).setTotalPoints(mTotalAmount)

                            findNavController().navigate(action)

                        }
                    }
                }
            }
        }
    }

    override fun onTextAddListener(
        amount: String,
        number: String,
        position: Int,
        itemId: Int,
        gameTypeId: Int
    ) {
        mTempList.find { it.id==itemId }?.let {game->
            if (amount.toInt() < MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)
            ) {
                Toast.makeText(
                    activity,
                    "Minimum amount is ${MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)}",
                    Toast.LENGTH_SHORT
                ).show()
                mTotalAmount -= game.amount!!
                mTempList.remove(game)
            }else{
                val tempgame= game
                if(mTotalAmount>=tempgame.amount!!){
                    mTotalAmount -= tempgame.amount!!
                }
                mTotalAmount += amount.toInt()
                mTempList.remove(tempgame)
                tempgame.amount=amount.toInt()
                mTempList.add(tempgame)
            }
        }?:run{
            if (amount.toInt() < MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)
            ) {
                Toast.makeText(
                    activity,
                    "Minimum amount is ${MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)}",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                mTotalAmount += amount.toInt()
                mTempList.add(Game(
                    amount.toInt(),
                    number,
                    "null",
                    itemItemPosition = position,
                    gameTypeId = gameTypeId,
                    id = itemId
                ))
            }
        }
        mGameList = ArrayList(mTempList)

        onUpdateText()
    }
   /* override fun onTextAddListener(
        amount: String,
        number: String,
        position: Int,
        itemId: Int,
        gameTypeId: Int
    ) {
        Log.e("position", "" + position)
        var isItemFound = false


        for (item in mTempList) {
            if (item.id == itemId) { // find item by id instead of position
                if (amount.toInt() < MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)
                ) {
                    Toast.makeText(
                        activity,
                        "Minimum amount is ${MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                mTempList.remove(item)
                mTempList.add(
                    Game(
                        amount.toInt(),
                        number,
                        "null",
                        itemItemPosition = position,
                        gameTypeId = gameTypeId,
                        id = item.id // preserve the existing id
                    )
                )
                isItemFound = true
                mTotalAmount += amount.toInt() - item.amount!!
                break
            }
        }


        if (!isItemFound) {

//            check if amount is less than 5. then show toast and return
            if (amount.toInt() < MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)
            ) {
                Toast.makeText(
                    activity,
                    "Min is ${MatkaPref(requireContext()).getMinBid(Constants.MIN_BID)}",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            mTempList.add(
                Game(
                    amount.toInt(),
                    number,
                    "null",
                    itemItemPosition = position,
                    gameTypeId = gameTypeId,
                    id = itemId
                )
            )
            mTotalAmount += amount.toInt()
        }

        mGameList = ArrayList(mTempList)

        onUpdateText()
    }*/

    private fun onUpdateText() {
        mBinding.tvAmount.text = mTotalAmount.toString()
    }


    override fun onTextRemoveListener(amount: String, number: String, position: Int, itemId: Int) {
        mTempList.find { it.id==itemId }?.let {game->
            mTotalAmount -= game.amount!!
            mTempList.remove(game)
        }
      /*  var itemToRemove: Game? = null

        for (item in mTempList) {
            if (item.itemItemPosition == position) {
                itemToRemove = item
                break
            }
        }

        itemToRemove?.let {
            mTempList.remove(it)
            mGameList = ArrayList(mTempList)

            mTotalAmount -= it.amount ?: 0
            onUpdateText()
        }*/

        mGameList = ArrayList(mTempList)

        onUpdateText()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}