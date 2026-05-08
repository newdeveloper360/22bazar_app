package com.userplay.bazar22.ui.fragments.funds.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentAddUpiBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.SuccessDialogFragment
import com.userplay.bazar22.ui.fragments.funds.viewmodel.FundsViewModel
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.ACCOUNT_HOLDER_NAME
import com.userplay.bazar22.utils.Constants.USER_UPI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddUpiFragment : Fragment(R.layout.fragment_add_upi), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentAddUpiBinding? = null
    private val mBinding get() = _binding!!
    private val mFundsViewModel: FundsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpiBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {

        mBinding.apply {
            btnSave.setOnClickListener(this@AddUpiFragment)
            back.setOnClickListener(this@AddUpiFragment)
            edName.setText(mPref.getAccountHolderName(ACCOUNT_HOLDER_NAME))
            edUpi.setText(mPref.getUserUpi(USER_UPI))
        }
    }

    private fun observer() {
        activity?.let {
            mFundsViewModel.mSaveUpiResponse.observe(it) { response ->
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
                                val bundle = Bundle()
                                val dialog = SuccessDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "success")

                                mPref.setAccountHolderName(mBinding.edName.text.toString())
                                mPref.setUserUpi(mBinding.edUpi.text.toString())
                            }
                        }
                    }

                    is ApiState.Loading -> {
                        it.hideKeyboard()
                        it.showProgressDialog()
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
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
                                edName.error = "Account Holder Name can't empty"
                                return@let
                            }
                            if (edUpi.text.isEmpty()) {
                                edUpi.error = "upi id can't empty"
                                return@let
                            } else {
                                mFundsViewModel.saveUpiDetails(
                                    edName.text.toString().trim(),
                                    edUpi.text.toString().trim()
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