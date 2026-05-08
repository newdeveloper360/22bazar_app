package com.userplay.bazar22.ui.fragments.funds.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentAddBankDetailsBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.SuccessDialogFragment
import com.userplay.bazar22.ui.fragments.funds.viewmodel.FundsViewModel
import com.userplay.bazar22.utils.Constants.ACCOUNT_HOLDER_NAME
import com.userplay.bazar22.utils.Constants.ACCOUNT_NUMBER
import com.userplay.bazar22.utils.Constants.IFSC_CODE
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBankDetailsFragment : Fragment(R.layout.fragment_add_bank_details), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentAddBankDetailsBinding? = null
    private val mBinding get() = _binding!!
    private val mFundsViewModel: FundsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBankDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {

        mBinding.apply {
            btnSave.setOnClickListener(this@AddBankDetailsFragment)
            edName.setText(mPref.getAccountHolderName(ACCOUNT_HOLDER_NAME))
            edAcNumber.setText(mPref.getAccountNumber(ACCOUNT_NUMBER))
            edIfsc.setText(mPref.getIFSCode(IFSC_CODE))
        }
    }

    private fun observer() {
        mBinding.back.setOnClickListener(this)
        activity?.let {
            mFundsViewModel.mSaveBankDetailsResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        if (response.data?.error != null) {
                            if (response.data.error) {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
                            } else {
                                val bundle = Bundle()
                                val dialog = SuccessDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "success")

                                mPref.setAccountHolderName(mBinding.edName.text.toString())
                                mPref.setAccountNumber(mBinding.edAcNumber.text.toString())
                                mPref.setIFSCode(mBinding.edIfsc.text.toString())

                            }
                        }
                        it.dismissDialog()
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

                    R.id.btnSave -> {
                        if (CheckNetwork.isNetworkConnected) {
                            if (edName.text.isEmpty()) {
                                edName.error = "Account Holder Name can't be empty"
                                return@let
                            } else if (edAcNumber.text.isEmpty()) {
                                edAcNumber.error = "Account Number can't be empty"
                                return@let
                            } else if (edIfsc.text.isEmpty()) {
                                edIfsc.error = "IFSC code can't be empty"
                                return@let
                            } else {
                                mFundsViewModel.saveBankDetails(
                                    edName.text.trim().toString(),
                                    edAcNumber.text.trim().toString(),
                                    edIfsc.text.toString().trim()
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

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}