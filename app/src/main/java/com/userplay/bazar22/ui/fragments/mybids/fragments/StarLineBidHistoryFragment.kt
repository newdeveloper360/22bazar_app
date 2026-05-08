package com.userplay.bazar22.ui.fragments.mybids.fragments

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
import com.userplay.bazar22.databinding.FragmentStarLineBidHistoryBinding
import com.userplay.bazar22.models.get_game_history.Data
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.mybids.adapters.MyBidsHistoryAdapter
import com.userplay.bazar22.ui.fragments.mybids.view_models.MyBidsViewModels
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StarLineBidHistoryFragment : Fragment(R.layout.fragment_star_line_bid_history),
    View.OnClickListener {

    private var _binding: FragmentStarLineBidHistoryBinding? = null
    private val mBinding get() = _binding!!
    private val mMyBidsViewModels: MyBidsViewModels by viewModels()
    private val mList: ArrayList<Data> = ArrayList()
    private val mMyBidsHistoryAdapter: MyBidsHistoryAdapter by lazy { MyBidsHistoryAdapter(mList,requireContext()) }
    private var mCurrentPageNumber = 1
    private var mTotalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarLineBidHistoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }


    private fun initView() {

        mBinding.apply {
            back.setOnClickListener(this@StarLineBidHistoryFragment)
            btnNext.setOnClickListener(this@StarLineBidHistoryFragment)
            btnPrevious.setOnClickListener(this@StarLineBidHistoryFragment)

        }

        activity?.let {
            mBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                adapter = mMyBidsHistoryAdapter
            }

            if (CheckNetwork.isNetworkConnected) {
                mMyBidsViewModels.getGameHistory(STARLINE_MARKET, mCurrentPageNumber)
            } else {
                val dialog = InternetErrorDialogFragment()
                dialog.show(childFragmentManager, "internet")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observer() {
        activity?.let {
            mMyBidsViewModels.mGetGameHistoryResponse.observe(it) { response ->
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

                                if (response.data.response?.gameHistory?.data?.size != null && response.data.response.gameHistory.data.isNotEmpty()) {
                                    response.data.response.gameHistory.data.let { it1 ->
                                        mList.addAll(it1)

                                    }
                                    response.data.response.gameHistory.lastPage.let {
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

                                mMyBidsHistoryAdapter.notifyDataSetChanged()
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
                            mMyBidsViewModels.getGameHistory(STARLINE_MARKET, mCurrentPageNumber)
                        }
                    }

                    R.id.btnNext -> {
                        if (mCurrentPageNumber < mTotalPages) {
                            mCurrentPageNumber++
                            mMyBidsViewModels.getGameHistory(STARLINE_MARKET, mCurrentPageNumber)
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