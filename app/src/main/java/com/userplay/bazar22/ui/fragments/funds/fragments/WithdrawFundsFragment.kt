package com.userplay.bazar22.ui.fragments.funds.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentWithdrawFundsBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.SuccessDialogFragment
import com.userplay.bazar22.ui.fragments.funds.viewmodel.FundsViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.BANK_WITHDRAW_ENABLE
import com.userplay.bazar22.utils.Constants.MIN_WITHDRAW
import com.userplay.bazar22.utils.Constants.NAME
import com.userplay.bazar22.utils.Constants.PHONE
import com.userplay.bazar22.utils.Constants.SUPPORT_NUMBER
import com.userplay.bazar22.utils.Constants.SUPPORT_TIME
import com.userplay.bazar22.utils.Constants.TELEGRAM_ENABLE
import com.userplay.bazar22.utils.Constants.TELEGRAM_LINK
import com.userplay.bazar22.utils.Constants.UPI_WITHDRAW_ENABLE
import com.userplay.bazar22.utils.Constants.WHATSAPP_ENABLE
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawFundsFragment : Fragment(R.layout.fragment_withdraw_funds), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentWithdrawFundsBinding? = null
    private val mBinding get() = _binding!!
    private val mFundsViewModel: FundsViewModel by viewModels()
    private var mMode = ""
    private val mSharedViewModels: SharedViewModels by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawFundsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {
        mBinding.apply {
            btnSend.setOnClickListener(this@WithdrawFundsFragment)
            back.setOnClickListener(this@WithdrawFundsFragment)
            supportLyt.setOnClickListener(this@WithdrawFundsFragment)
            radioGroup.setOnCheckedChangeListener(this@WithdrawFundsFragment)
        }

        findNavController().navigate(R.id.withDrawTermConditionDialogFragment)
    }

    private fun observer() {
        activity?.let {
            mFundsViewModel.mWithDrawBalanceResponse.observe(it) { response ->

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
                                mPref.setBalance(response.data.response?.balanceLeft)
                                mSharedViewModels.setBalance(response.data.response?.balanceLeft.toString())

                                val bundle = Bundle()
                                val dialog = SuccessDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "success")

                                mBinding.apply {
                                    tvBalance.text = response.data.response?.balanceLeft.toString()
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
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.btnSend -> {
                        if (CheckNetwork.isNetworkConnected) {
                            if (edAmount.text.isEmpty()) {
                                edAmount.error = "Amount can't be empty"
                                return@let
                            } else if (edAmount.text.toString().toLong() < mPref.getMinWithdraw(
                                    MIN_WITHDRAW
                                )
                            ) {
                                edAmount.error =
                                    "Minimum amount required " + mPref.getMinWithdraw(MIN_WITHDRAW)
                                return@let
                            } else if (mMode.isEmpty()) {
                                if (mPref.getUpiWithDrawEnable(UPI_WITHDRAW_ENABLE) == 1 || mPref.getBankWithDrawEnable(BANK_WITHDRAW_ENABLE) == 1
                                ) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", "Withdraw mode field is required")
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                } else {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", "Withdraw mode currently disable")
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                }
                            } else {
                                mFundsViewModel.withdrawAmount(
                                    edAmount.text.toString().trim(),
                                    mMode
                                )
                            }
                        } else {
                            val dialog = InternetErrorDialogFragment()
                            dialog.show(childFragmentManager, "internet")
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.supportLyt -> {
                        if (mPref.getWhatsAppEnable(WHATSAPP_ENABLE) == 1) {
                            val url =
                                "https://api.whatsapp.com/send?phone=${
                                    mPref.getWhatsAppNumber(
                                        Constants.WHATSAPP_NUMBER
                                    )
                                }"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } else if (mPref.getTelegramEnable(TELEGRAM_ENABLE) == 1) {
                            val telegramIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(mPref.getTelegramLink(TELEGRAM_LINK))
                                )
                            startActivity(telegramIntent)
                        }
                    }

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        mBinding.apply {

            tvBalance.text = resources.getString(R.string.ruppes_symbol) + mPref.getBalance(BALANCE)
                .toString()
            tvUserName.text = mPref.getName(NAME).toString()
            tvMobile.text = mPref.getPhone(PHONE).toString()

            if (mPref.getWhatsAppEnable(WHATSAPP_ENABLE) == 1) {
                tvSupportNumber.text = mPref.getSupportTime(SUPPORT_TIME)
                tvSupportTime.text = mPref.getSupportNumber(SUPPORT_NUMBER)
                imgIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.whatsapp
                    )
                )
            } else if (mPref.getTelegramEnable(TELEGRAM_ENABLE) == 1) {
                tvSupportNumber.text = mPref.getTelegramLink(TELEGRAM_LINK)
                tvSupportTime.text = mPref.getSupportTime(SUPPORT_TIME)
                imgIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.telegram
                    )
                )
            } else {
                supportLyt.visibility = View.GONE
            }
            if (mPref.getUpiWithDrawEnable(UPI_WITHDRAW_ENABLE) == 1) {
                radioUpi.visibility = View.VISIBLE
               // mMode = "upi"
            } else if (mPref.getBankWithDrawEnable(BANK_WITHDRAW_ENABLE) == 1) {
                radioBank.visibility = View.VISIBLE
               // mMode = "bank"
            } else {
                radioGroup.visibility = View.GONE
            }
        }

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.radioBank -> {
                mMode = "bank"
            }

            R.id.radioUpi -> {
                mMode = "upi"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}