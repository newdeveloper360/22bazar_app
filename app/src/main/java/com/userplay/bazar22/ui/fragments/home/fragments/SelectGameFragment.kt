package com.userplay.bazar22.ui.fragments.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSelectGameNewBinding
import com.userplay.bazar22.models.SelectGameModel
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemGameClickListener
import com.userplay.bazar22.ui.fragments.home.adapters.SelectGameAdapter
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SelectGameFragment : Fragment(R.layout.fragment_select_game_new), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSelectGameNewBinding? = null
    private val mBinding get() = _binding!!
    private val mArgs: SelectGameFragmentArgs by navArgs()
    private var mGameName: String = ""
    private val mSharedViewModels: SharedViewModels by viewModels()
    private var mAdapterSelectGame: SelectGameAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectGameNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
        mAdapterSelectGame = SelectGameAdapter(gamesList(), mListener = object : ItemGameClickListener {
            override fun onItemClick(item: SelectGameModel) {
                navigateToGame(item)
            }
        })
        mBinding.rvGames.adapter=mAdapterSelectGame
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
            }
        }
    }

    private fun initView() {

        mBinding.apply {
            /*singleDigit.setOnClickListener(this@SelectGameFragment)
            singleDigitBulk.setOnClickListener(this@SelectGameFragment)
            jodiDigits.setOnClickListener(this@SelectGameFragment)
            jodiDigitsBulk.setOnClickListener(this@SelectGameFragment)
            singlePana.setOnClickListener(this@SelectGameFragment)
            singlePanaBulk.setOnClickListener(this@SelectGameFragment)
            doublePana.setOnClickListener(this@SelectGameFragment)
            doublePanaBulk.setOnClickListener(this@SelectGameFragment)
            triplePana.setOnClickListener(this@SelectGameFragment)
            halfSangamABoard.setOnClickListener(this@SelectGameFragment)
            halfSangamBBoard.setOnClickListener(this@SelectGameFragment)
            fullSangamBoard.setOnClickListener(this@SelectGameFragment)
            llspDpTp.setOnClickListener(this@SelectGameFragment)
            llJodiFamily.setOnClickListener(this@SelectGameFragment)
            llPanaFamily.setOnClickListener(this@SelectGameFragment)
            llJodiGroup.setOnClickListener(this@SelectGameFragment)
            llJodiTotal.setOnClickListener(this@SelectGameFragment)
            llSpMotor.setOnClickListener(this@SelectGameFragment)
            llDpMotor.setOnClickListener(this@SelectGameFragment)*/
            back.setOnClickListener(this@SelectGameFragment)
            mGameName = mArgs.gameName
            headerText.text = mGameName

        }

      /*  when (mArgs.from) {

            STARLINE_MARKET -> {
                mBinding.apply {
                    halfSangamABoard.visibility = View.GONE
                    halfSangamBBoard.visibility = View.GONE
                    fullSangamBoard.visibility = View.GONE
                    jodiDigits.visibility = View.GONE
                    jodiDigitsBulk.visibility = View.GONE
                    llJodiFamily.visibility = View.GONE
                }
            }
        }*/
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun gamesList():ArrayList<SelectGameModel>{
        val gamesList:ArrayList<SelectGameModel> = arrayListOf()
        gamesList.add(SelectGameModel(name = "Single Digits", icon = R.drawable.signle_digit, bgColor = "#f5dacf"))
        if(!Constants.enableSingleBulkDigitsNewDesign){
            gamesList.add(SelectGameModel(name = "Single Digits Bulk", icon = R.drawable.signle_digit, bgColor = "#f1ead0"))
        }
        if(mArgs.from!= STARLINE_MARKET){
            gamesList.add(SelectGameModel(name = "Jodi Digits",icon = R.drawable.ic_jodidigits, bgColor = "#d7e0f1"))
            gamesList.add(SelectGameModel(name = "Jodi Digits Bulk",icon = R.drawable.ic_jodidigits, bgColor = "#d7eced"))
        }
        if(Constants.newPanaPage){
            gamesList.add(SelectGameModel(name = "Patti", icon = R.drawable.single_pana, bgColor = "#f1ead0"))
        }else{
            gamesList.add(SelectGameModel(name = "Single Pana", icon = R.drawable.single_pana, bgColor = "#e3d3f0"))
            gamesList.add(SelectGameModel(name = "Single Pana Bulk", icon = R.drawable.single_pana, bgColor = "#f5dae1"))
            gamesList.add(SelectGameModel(name = "Double Pana", icon = R.drawable.double_pana, bgColor = "#d0e8d0"))
            gamesList.add(SelectGameModel(name = "Double Pana Bulk", icon = R.drawable.double_pana, bgColor = "#f5dacf"))
            gamesList.add(SelectGameModel(name = "Triple Pana", icon = R.drawable.ic_triplepana, bgColor = "#f1ead0"))

        }
        if(mArgs.from!= STARLINE_MARKET){
            gamesList.add(SelectGameModel(name = "Jodi Family",icon = R.drawable.ic_jodidigits, bgColor = "#d0e8d0"))
            gamesList.add(SelectGameModel(name = "Jodi Group", icon = R.drawable.ic_jodidigits, bgColor = "#d7e0f1"))
            gamesList.add(SelectGameModel(name = "Total Jodi", icon = R.drawable.ic_jodidigits, bgColor = "#f1ead0"))
            if(Constants.enableHalfSangam){
                gamesList.add(SelectGameModel(name = "Half Sangam", icon = R.drawable.ic_choicepana, bgColor = "#f5dacf"))
            }else{
                gamesList.add(SelectGameModel(name = "Half Sangam A", icon = R.drawable.ic_choicepana, bgColor = "#f5dacf"))
                gamesList.add(SelectGameModel(name = "Half Sangam B", icon = R.drawable.ic_choicepana, bgColor = "#f5dacf"))
            }
            gamesList.add(SelectGameModel(name = "Full Sangam", icon = R.drawable.ic_choicepana, bgColor = "#d7e0f1"))
        }

        gamesList.add(SelectGameModel(name = "SP Motor", icon = R.drawable.single_pana, bgColor = "#f5dacf"))
        gamesList.add(SelectGameModel(name = "DP Motor", icon = R.drawable.double_pana, bgColor = "#f5dacf"))
        gamesList.add(SelectGameModel(name = "SP,DP,TP", icon = R.drawable.ic_choicepana, bgColor = "#f5dacf"))
        gamesList.add(SelectGameModel(name = "Pana Family", icon = R.drawable.double_pana, bgColor = "#f5dacf"))

        return gamesList
    }


    fun navigateToGame(item:SelectGameModel){
        mPref.setGameSubName(item.name)
        when(item.name){

            "Single Digits" -> {
                if(Constants.enableSingleDigitsNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalSingleDigitsNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else {

                    val action =
                        SelectGameFragmentDirections.actionGlobalSingleDigitsFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }
            }

            "Single Digits Bulk" -> {
                if(Constants.enableSingleBulkDigitsNewDesign){

                    val action =
                        SelectGameFragmentDirections.actionGlobalSingleDigitsBulkNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else {

                    val action =
                        SelectGameFragmentDirections.actionGlobalSingleDigitsBulkFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }
            }

            "Jodi Digits" -> {
                if (Constants.enableJodiDigitsNewDesign) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiDigitsNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                } else {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(),"Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiDigitsFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from
                        )
                    findNavController().navigate(action)
                }
            }

            "Jodi Digits Bulk" -> {
                if (Constants.enableJodiBulkDigitsNewDesign) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiDigitBulkNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                } else {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiDigitBulkFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }
            }

            "Single Pana" -> {
                if(Constants.enableSinglePanaNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalSinglePannaNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else{
                    val action =
                        SelectGameFragmentDirections.actionGlobalSinglePanaFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }



            }

            "Single Pana Bulk" -> {
                if(Constants.enableSinglePanaBulkNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalSinglePannaBulkNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else{
                    val action =
                        SelectGameFragmentDirections.actionGlobalSinglePanaBulkFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }

            }

            "Double Pana" -> {
                if(Constants.enableDoublePanaNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalDoublePanaNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else{
                    val action =
                        SelectGameFragmentDirections.actionGlobalDoublePanaFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }

            }

            "Double Pana Bulk" -> {
                if(Constants.enableDoublePanaBulkNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalDoublePanaBulkNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else{
                    val action =
                        SelectGameFragmentDirections.actionGlobalDoublePanaBulkFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }

            }

            "Triple Pana" -> {
                if(Constants.enableTriplePanaNewDesign){
                    val action =
                        SelectGameFragmentDirections.actionGlobalTriplePannaNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }else{
                    val action =
                        SelectGameFragmentDirections.actionGlobalTriplePanaFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                }

            }

            "Patti"->{
                    val action =
                        SelectGameFragmentDirections.actionGlobalPattiFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
            }

            "Panel"->{
                val action =
                    SelectGameFragmentDirections.actionGlobalPattiFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }

            "Jodi Family" -> {
                val action =
                    SelectGameFragmentDirections.actionGlobalJodiFamilyFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }

            "Pana Family" -> {
                val action =
                    SelectGameFragmentDirections.actionGlobalPanaFamilyFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }

            "Half Sangam"->{
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalHalfSangamNewFragment(
                            mArgs.marketID,
                            mGameName,
                        )
                    findNavController().navigate(action)
            }

            "Half Sangam A" -> {
                if(Constants.enableHalfSangamA) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalHalfSangamANewFragment(
                            mArgs.marketID,
                            mGameName,
                        )
                    findNavController().navigate(action)
                }else{
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalHalfSangamAFragment(
                            mArgs.marketID,
                            mGameName,
                        )
                    findNavController().navigate(action)
                }
            }

            "Half Sangam B" -> {
                if(Constants.enableHalfSangamB) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalHalfSangamBNewFragment(
                            mArgs.marketID,
                            mGameName
                        )
                    findNavController().navigate(action)
                }else{
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalHalfSangamBFragment(
                            mArgs.marketID,
                            mGameName
                        )
                    findNavController().navigate(action)
                }
            }

            "Full Sangam" -> {
                if(Constants.enableFullSangam) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalFullSangamNewFragment(
                            mArgs.marketID,
                            mGameName
                        )
                    findNavController().navigate(action)
                }else{
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalFullSangamFragment(
                            mArgs.marketID,
                            mGameName
                        )
                    findNavController().navigate(action)
                }
            }

            "Jodi Group" -> {
                if (Constants.enableJodiGroupNewDesign) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiGroupNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                } else {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiGroupFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from
                        )
                    findNavController().navigate(action)
                }
            }

            "Total Jodi" -> {
                if (Constants.enableJodiTotalNewDesign) {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiTotalNewFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from,
                            mArgs.openStatus
                        )
                    findNavController().navigate(action)
                } else {
                    if (!mArgs.openStatus) {
                        Toast.makeText(requireContext(), "Open Game is closed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val action =
                        SelectGameFragmentDirections.actionGlobalJodiTotalFragment(
                            mArgs.marketID,
                            mGameName,
                            mArgs.from
                        )
                    findNavController().navigate(action)
                }
            }




            "SP Motor" -> {
                val action =
                    SelectGameFragmentDirections.actionGlobalSPMotorFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }

            "DP Motor" -> {
                val action =
                    SelectGameFragmentDirections.actionGlobalDPMotorFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }

            "SP,DP,TP" -> {
                val action =
                    SelectGameFragmentDirections.actionGlobalSPDPTPBulkFragment(
                        mArgs.marketID,
                        mGameName,
                        mArgs.from,
                        mArgs.openStatus
                    )
                findNavController().navigate(action)
            }



























        }
    }
}