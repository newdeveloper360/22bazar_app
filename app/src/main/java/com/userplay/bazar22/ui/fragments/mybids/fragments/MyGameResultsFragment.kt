package com.userplay.bazar22.ui.fragments.mybids.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentMyGameResultsBinding
import com.userplay.bazar22.models.get_result.GameResult
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.mybids.adapters.MyBidsResultAdapter
import com.userplay.bazar22.ui.fragments.mybids.view_models.MyBidsViewModels
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyGameResultsFragment : Fragment(R.layout.fragment_my_game_results), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentMyGameResultsBinding? = null
    private val mBinding get() = _binding!!
    private val mMyBidsViewModels: MyBidsViewModels by viewModels()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private var mList: ArrayList<GameResult> = ArrayList()
    private val mMyBidsResultAdapter: MyBidsResultAdapter by lazy { MyBidsResultAdapter(mList) }
    private var calendar = Calendar.getInstance()
    private var year = calendar[Calendar.YEAR]
    private var month = calendar[Calendar.MONTH] + 1 // add 1 because months are zero-based
    private var day = calendar[Calendar.DAY_OF_MONTH]
    private lateinit var currentDate: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyGameResultsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        currentDate = "$year-$month-$day"
        mBinding.back.setOnClickListener(this)
        mBinding.calendarTextLy.setOnClickListener(this)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mMyBidsResultAdapter
        }

        mBinding.calendarText.text = currentDate

        if (CheckNetwork.isNetworkConnected) {
            mMyBidsViewModels.getGameResult(Constants.GENERAL_MARKET, currentDate)
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {

        activity?.let {
            mMyBidsViewModels.mGetResultResponse.observe(it) { response ->
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
                                mBinding.tvNotFound.visibility = View.VISIBLE
                                mBinding.recyclerView.visibility = View.GONE
                            } else {
                                mList.clear()
                                if (response.data.response?.gameResults != null) {
                                    if (response.data.response.gameResults.size > 0) {
                                        mBinding.tvNotFound.visibility = View.GONE
                                        mBinding.recyclerView.visibility = View.VISIBLE
                                    } else {
                                        mBinding.tvNotFound.visibility = View.VISIBLE
                                        mBinding.recyclerView.visibility = View.GONE
                                    }
                                }
                                response.data.response?.gameResults?.let { it1 -> mList.addAll(it1) }
                                mMyBidsResultAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        mBinding.tvNotFound.visibility = View.VISIBLE
                        mBinding.recyclerView.visibility = View.GONE
                        Log.e("market_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("market_loading", "loading---->>>>")
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
                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.calendar_text_ly -> {
                        getDate()
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getDate() {

        activity?.let {

            val dpd = DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    currentDate = "$year-$month-$dayOfMonth"
                    mBinding.calendarText.text = currentDate
                    if (CheckNetwork.isNetworkConnected) {
                        mMyBidsViewModels.getGameResult(Constants.GENERAL_MARKET, currentDate)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }
                },
                year,
                month,
                day
            )

            dpd.show()

        }

    }

    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(Constants.BALANCE).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}