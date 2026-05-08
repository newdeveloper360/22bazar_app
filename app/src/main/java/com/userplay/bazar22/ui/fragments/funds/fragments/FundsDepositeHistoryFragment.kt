package com.userplay.bazar22.ui.fragments.funds.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentFundsDepositeHistoryBinding
import com.userplay.bazar22.models.get_deposit_history.Data
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.funds.adapters.DepositFundAdapter
import com.userplay.bazar22.ui.fragments.funds.viewmodel.FundsViewModel
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FundsDepositeHistoryFragment : Fragment(R.layout.fragment_funds_deposite_history),
    View.OnClickListener {

    private var _binding: FragmentFundsDepositeHistoryBinding? = null
    private val mBinding get() = _binding!!
    private val mList: ArrayList<Data> = ArrayList()
    private val mFundsViewModel: FundsViewModel by viewModels()
    private val mDepositFundAdapter: DepositFundAdapter by lazy {
        DepositFundAdapter(
            requireContext(),
            mList
        )
    }

    private var mCurrentPageNumber = 1
    private var mTotalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFundsDepositeHistoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {


        mBinding.apply {
            back.setOnClickListener(this@FundsDepositeHistoryFragment)
            btnNext.setOnClickListener(this@FundsDepositeHistoryFragment)
            btnPrevious.setOnClickListener(this@FundsDepositeHistoryFragment)
        }
        activity?.let {

            mBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                adapter = mDepositFundAdapter
            }

            if (CheckNetwork.isNetworkConnected) {
                mFundsViewModel.getDepositeList(mCurrentPageNumber)
            } else {
                val dialog = InternetErrorDialogFragment()
                dialog.show(childFragmentManager, "internet")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observer() {
        activity?.let {

            mFundsViewModel.mGetDepositHistoryResponse.observe(it) { response ->
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
                                mList.clear()

                                if (response.data.response?.depositHistory?.data?.size != null && response.data.response.depositHistory.data.isNotEmpty()) {
                                    response.data.response.depositHistory.data.let { it1 ->
                                        mList.addAll(it1)

                                    }
                                    response.data.response.depositHistory.lastPage.let {
                                        if (it != null) {
                                            mTotalPages = it
                                        }
                                    }
                                    mBinding.apply {
                                        pagingLyt.visibility = View.VISIBLE
                                        recyclerView.visibility = View.VISIBLE
                                        tvNotFound.visibility = View.GONE
                                        tvpages.text = "($mCurrentPageNumber/$mTotalPages)"
                                    }

                                } else {
                                    mBinding.apply {
                                        pagingLyt.visibility = View.GONE
                                        tvNotFound.visibility = View.VISIBLE
                                        recyclerView.visibility = View.GONE
                                    }
                                }

                                mDepositFundAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("error", "" + response.message)
                    }
                    is ApiState.Loading -> {
                        it.showProgressDialog()

                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.btnPrevious -> {
                        if (mCurrentPageNumber != 1) {
                            mCurrentPageNumber--
                            mFundsViewModel.getDepositeList(mCurrentPageNumber)
                        }
                    }

                    R.id.btnNext -> {
                        if (mCurrentPageNumber < mTotalPages) {
                            mCurrentPageNumber++
                            mFundsViewModel.getDepositeList(mCurrentPageNumber)
                        }
                    }
                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}