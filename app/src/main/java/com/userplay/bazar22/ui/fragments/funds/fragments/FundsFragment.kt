package com.userplay.bazar22.ui.fragments.funds.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentFundsBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FundsFragment : Fragment(R.layout.fragment_funds), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentFundsBinding? = null
    private val mBinding get() = _binding!!
    private val mSharedViewModels: SharedViewModels by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFundsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun observer() {
        activity?.let {
            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvBalance.text = it
            }
        }
    }

    private fun initView() {

        mBinding.apply {
            back.setOnClickListener(this@FundsFragment)
            addFund.setOnClickListener(this@FundsFragment)
            withdrawFund.setOnClickListener(this@FundsFragment)
            addBankDetails.setOnClickListener(this@FundsFragment)
            addUpiDetails.setOnClickListener(this@FundsFragment)
            fundDeposit.setOnClickListener(this@FundsFragment)
            fundWithdrawHistory.setOnClickListener(this@FundsFragment)
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.add_fund -> {
                        findNavController().navigate(R.id.addFundsFragment)
                    }

                    R.id.withdraw_fund -> {
                        findNavController().navigate(R.id.withdrawFundsFragment)
                    }

                    R.id.add_bank_details -> {
                        findNavController().navigate(R.id.addBankDetailsFragment)
                    }

                    R.id.add_upi_details -> {
                        findNavController().navigate(R.id.addUpiFragment)
                    }
                    R.id.fund_deposit -> {
                        findNavController().navigate(R.id.fundsDepositeHistoryFragment)
                    }
                    R.id.fund_withdraw_history -> {
                        findNavController().navigate(R.id.fundWithdrawHistoryFragment)
                    }
                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
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