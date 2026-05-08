package com.userplay.bazar22.ui.fragments.navigation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSettingsBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.navigation.viewmodels.NavigationViewModel
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.Constants.DESAWAR_NOTIFICATION
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.GENERAL_NOTIFICATION
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import com.userplay.bazar22.utils.Constants.START_LINE_NOTIFICATION
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSettingsBinding? = null
    private val mBinding get() = _binding!!
    private val mNavigationViewModel: NavigationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {

        activity?.let {
            val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        findNavController().popBackStack()
                    }
                }
            it.onBackPressedDispatcher.addCallback(it, callback)
        }

        mBinding.apply {

            back.setOnClickListener(this@SettingsFragment)
            swAppLock.isChecked = mPref.getIsUserLoginWithPin(Constants.IS_USER_LOGIN_WITH_MPIN)
            generalNotification.isChecked = mPref.getGeneralNotification(GENERAL_NOTIFICATION) == 1

            starlineSwitch.isChecked = mPref.getStartLineNotification(START_LINE_NOTIFICATION) == 1

            desawarSwitch.isChecked = mPref.getDesawarNotification(DESAWAR_NOTIFICATION) == 1

            generalNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(GENERAL_MARKET)
                        mPref.setGeneralNotification(1)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }

                } else {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(GENERAL_MARKET)
                        mPref.setGeneralNotification(0)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }

                }
            }

            swAppLock.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mPref.setPinLock(Constants.IS_USER_LOGIN_WITH_MPIN, true)
                } else {
                    mPref.setPinLock(Constants.IS_USER_LOGIN_WITH_MPIN, false)
                }
            }

            starlineSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(STARLINE_MARKET)
                        mPref.setStartLineNotification(1)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }

                } else {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(STARLINE_MARKET)
                        mPref.setStartLineNotification(0)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }

                }
            }

            desawarSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(DESAWAR_MARKET)
                        mPref.setDesawarNotification(1)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }

                } else {
                    if (CheckNetwork.isNetworkConnected) {
                        mNavigationViewModel.changeNotification(DESAWAR_MARKET)
                        mPref.setDesawarNotification(0)
                    } else {
                        val dialog = InternetErrorDialogFragment()
                        dialog.show(childFragmentManager, "internet")
                    }
                }
            }
        }
    }

    private fun observer() {
        activity?.let {

            mNavigationViewModel.mNotificationChangeResponse.observe(it) { response ->

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
                                it.showToast(response.data.message.toString())
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