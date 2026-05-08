package com.userplay.bazar22.ui.fragments.passbook.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentPassBookBinding
import com.userplay.bazar22.models.getpassbook.Data
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.passbook.adapter.PassBookAdapter
import com.userplay.bazar22.ui.fragments.passbook.viewmodel.PassBookViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PassBookFragment : Fragment(R.layout.fragment_pass_book), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentPassBookBinding? = null
    private val mBinding get() = _binding!!
    private val mPassBookViewModel: PassBookViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    private val mList: ArrayList<Data> = ArrayList()
    private val mPassBookAdapter: PassBookAdapter by lazy {
        PassBookAdapter(
            mList,
            requireContext()
        )
    }

    private var mCurrentPageNumber = 1
    private var mTotalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassBookBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {

        mBinding.apply {
            btnNext.setOnClickListener(this@PassBookFragment)
            btnPrevious.setOnClickListener(this@PassBookFragment)
            rotate.setOnClickListener(this@PassBookFragment)
            back.setOnClickListener(this@PassBookFragment)
        }

        activity?.let {

            mBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                adapter = mPassBookAdapter
            }

            if (CheckNetwork.isNetworkConnected) {
                mPassBookViewModel.getPassBook(mCurrentPageNumber)
            } else {
                val dialog = InternetErrorDialogFragment()
                dialog.show(childFragmentManager, "internet")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observer() {
        activity?.let {

            mPassBookViewModel.mGetPassbookResponse.observe(it) { response ->
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

                                response.data.response?.transactions?.data?.let { it1 ->
                                    mList.addAll(it1)
                                }
                                response.data.response?.transactions?.lastPage.let {
                                    if (it != null) {
                                        mTotalPages = it
                                    }
                                }

                                if (response.data.response?.transactions?.data != null) {
                                    if (response.data.response.transactions.data.size > 0) {
                                        mBinding.scroolview.visibility = View.VISIBLE
                                        mBinding.tvNotFound.visibility = View.GONE
                                    } else {
                                        mBinding.scroolview.visibility = View.GONE
                                        mBinding.tvNotFound.visibility = View.VISIBLE
                                    }
                                }

                                mBinding.tvpages.text = "($mCurrentPageNumber/$mTotalPages)"
                                mPassBookAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        mBinding
                        Log.e("error", "" + response.message)
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

    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(Constants.BALANCE).toString()
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.btnPrevious -> {
                        if (mCurrentPageNumber != 1) {
                            mCurrentPageNumber--
                            mPassBookViewModel.getPassBook(mCurrentPageNumber)
                        }
                    }

                    R.id.btnNext -> {
                        if (mCurrentPageNumber < mTotalPages) {
                            mCurrentPageNumber++
                            mPassBookViewModel.getPassBook(mCurrentPageNumber)
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }

                    R.id.rotate -> {
                        toggleOrientation()
                    }
                }
            }
        }
    }

    private fun toggleOrientation() {
        // Get the current orientation of the activity
        val currentOrientation = resources.configuration.orientation

        // Check the current orientation and set the opposite orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}