package com.userplay.bazar22.ui.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSubmitGameDialogBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork.Companion.isNetworkConnected
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.fragments.home.adapters.GameSubmitDialogAdapter
import com.userplay.bazar22.ui.viewmodels.GameTypeViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubmitGameTotalJodiDialogFragment : DialogFragment(R.layout.fragment_submit_game_dialog),
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSubmitGameDialogBinding? = null
    private val mBinding get() = _binding!!
    private val mGameTypeViewModel: GameTypeViewModel by viewModels()
    private val mArgs: SubmitGameTotalJodiDialogFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()

    private val mGameSubmitDialogAdapter: GameSubmitDialogAdapter by lazy {
        GameSubmitDialogAdapter(
            mArgs.sendBody.games,
            mArgs.from,
            mArgs.gameType
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmitGameDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBinding.apply {
            tvTotalBids.text = mArgs.totalBids.toString()
            tvTotalPoints.text = mArgs.totalPoints.toString()
            submit.setOnClickListener(this@SubmitGameTotalJodiDialogFragment)
            cancel.setOnClickListener(this@SubmitGameTotalJodiDialogFragment)
            tvDate.text = mArgs.gameName + " - " + currentDate()

            when (mArgs.from) {
                DESAWAR_MARKET, STARLINE_MARKET -> {
                    tvType.visibility = View.GONE
                }
                GENERAL_MARKET ->{
                    when (mArgs.gameType) {
                        true -> {
                            tvType.visibility = View.VISIBLE
                        }
                        false -> {
                            tvType.visibility = View.GONE
                        }
                    }
                }
            }
        }

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGameSubmitDialogAdapter
        }
    }

    private fun observer() {
        activity?.let {
            mGameTypeViewModel.mGameSubmitResponse.observe(it) { response ->

                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        if (response.data?.error != null) {
                            if (response.data.error) {


                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")

                            } else {
                                mSharedViewModels.setBalance(response.data.response?.balanceLeft.toString())
                                mPref.setBalance(response.data.response?.balanceLeft)
                                when (mArgs.from) {

                                    GENERAL_MARKET -> {
                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", GENERAL_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")
                                    }

                                    STARLINE_MARKET -> {

                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", STARLINE_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")
                                    }

                                    DESAWAR_MARKET -> {

                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", DESAWAR_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")

                                    }

                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                    }
                }
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvBalance.text = it
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.submit -> {
                        if (isNetworkConnected) {
                            it.hideKeyboard()
                            mGameTypeViewModel.gameSubmitTotal(mArgs.sendBodyActual)
                        } else {
                            val dialog = InternetErrorDialogFragment()
                            dialog.show(childFragmentManager, "internet")
                            // it.showToast(resources.getString(R.string.check_your_internet))
                        }
                    }
                    R.id.cancel -> {
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(BALANCE).toString()
        val afterBalance = mPref.getBalance(BALANCE) - mArgs.totalPoints
        mBinding.afterBalance.text = afterBalance.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}