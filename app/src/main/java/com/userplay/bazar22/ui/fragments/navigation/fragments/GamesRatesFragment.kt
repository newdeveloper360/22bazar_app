package com.userplay.bazar22.ui.fragments.navigation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentGamesRatesBinding
import com.userplay.bazar22.models.get_rate_new.Data
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.navigation.adapters.GameRateParentAdapter
import com.userplay.bazar22.ui.fragments.navigation.viewmodels.NavigationViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GamesRatesFragment : Fragment(R.layout.fragment_games_rates), View.OnClickListener {


    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentGamesRatesBinding? = null
    private val mBinding get() = _binding!!
    private val mNavigationViewModel: NavigationViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private val mGameTypeList: ArrayList<Data> = ArrayList()
    private val mGamesRateParentAdapter: GameRateParentAdapter by lazy {
        GameRateParentAdapter(
            mGameTypeList
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesRatesBinding.inflate(inflater, container, false)
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

        mBinding.back.setOnClickListener(this@GamesRatesFragment)

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGamesRateParentAdapter
        }

        if (CheckNetwork.isNetworkConnected) {
            mNavigationViewModel.getGameRates()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
            //activity?.showToast(resources.getString(R.string.check_your_internet))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {
        activity?.let {
            mNavigationViewModel.mGameRatesResponse.observe(it) { response ->
                when (response) {

                    is ApiState.Success -> {
                        it.dismissDialog()
                        if (response.data?.error != null) {
                            if (!response.data.error) {
                                mGameTypeList.clear()
                                if (response.data.response?.data != null) {
                                    mGameTypeList.addAll(response.data.response.data)
                                    mGamesRateParentAdapter.notifyDataSetChanged()
                                }
                            } else {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
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

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvAmount.text = it
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

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}