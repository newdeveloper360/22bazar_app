package com.userplay.bazar22.ui.fragments.navigation.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentInviteAndEarnBinding
import com.userplay.bazar22.models.refferal.ReferralUser
import com.userplay.bazar22.models.user_level.UserLevelData
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.navigation.adapters.ReferralAdapter
import com.userplay.bazar22.ui.fragments.navigation.adapters.UserLevelAdapter
import com.userplay.bazar22.ui.fragments.navigation.viewmodels.NavigationViewModel
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.INVITE_BONUS
import com.userplay.bazar22.utils.Constants.OWN_CODE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class InviteAndEarnFragment : Fragment(R.layout.fragment_invite_and_earn), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentInviteAndEarnBinding? = null
    private val mBinding get() = _binding!!
    private val mNavigationViewModel: NavigationViewModel by viewModels()
    private var mList: ArrayList<ReferralUser> = ArrayList()
    private val mAdapter: ReferralAdapter by lazy { ReferralAdapter(mList) }
    private lateinit var userLevelAdapter: UserLevelAdapter
    private val userLevelList: ArrayList<UserLevelData> = ArrayList()

    private fun  setupUserLevelRecycler() {
        userLevelAdapter = UserLevelAdapter(userLevelList)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userLevelAdapter
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteAndEarnBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }


    @SuppressLint("SetTextI18n")
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

        mBinding.tvBonus.text = mPref.getInviteBonus(INVITE_BONUS).toString()

        mBinding.apply {
            copy.setOnClickListener(this@InviteAndEarnFragment)
            btnShare.setOnClickListener(this@InviteAndEarnFragment)
            back.setOnClickListener(this@InviteAndEarnFragment)
            edOwnCode.setText(mPref.getOwnCode(OWN_CODE).toString()+"%")

            //   Set Level
            val level = arrayOf(1, 2, 3, 4, 5)
            val adapter = ArrayAdapter (
                requireContext(),
                android.R.layout.simple_spinner_item,
                level
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            etSelectLevel.adapter = adapter
//            etSelectLevel.setSelection(0)

            // OnClickListener for the button
            var isSpinnerInitial = true
            mBinding.etSelectLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (isSpinnerInitial) {
                        isSpinnerInitial = false
                        return
                    }
                    val selectedLevelId = parent.getItemAtPosition(position) as Int
                    mNavigationViewModel.getUserLevel(selectedLevelId)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }


        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }

        if (CheckNetwork.isNetworkConnected) {
//            mNavigationViewModel.getReferral()
            if (mPref.getUserLevelSystem() == true) {
                setupUserLevelRecycler()
                mNavigationViewModel.getUserLevel(null)

                mBinding.tvTodayCommissionTh.visibility = View.VISIBLE
                mBinding.tvTotalCommissionTh.visibility = View.VISIBLE
                mBinding.levelBox.visibility = View.VISIBLE

            } else {
                mNavigationViewModel.getReferral()
            }
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observer() {
        activity?.let {

            mNavigationViewModel.mReferralResponse.observe(it) { response ->
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
                                if (response.data.response != null) {
                                    mList.clear()
                                    mList.addAll(response.data.response.referralUsers)
                                    mBinding.apply {
                                        tvTotalEarned.text =
                                            "₹ " + response.data.response.totalEarned.toString()
                                        tvTotalReferred.text =
                                            response.data.response.totalInvited.toString()
                                    }
                                    mAdapter.notifyDataSetChanged()
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

            mNavigationViewModel.mUserLevelResponse.observe(it) { response ->
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
                                if (response.data.response != null) {
                                    userLevelList.clear()
                                    userLevelList.addAll(response.data.response.userlevelsData)

//                                    Toast.makeText(requireContext(), "Received: ${response.data.response.userlevelsData} items", Toast.LENGTH_SHORT).show()

                                    mBinding.apply {
                                        tvTotalEarned.text = "₹ " + response.data.response.totalEarned.toString()
                                        tvTotalReferred.text = response.data.response.totalInvited.toString()
                                    }
                                    userLevelAdapter.notifyDataSetChanged()
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

                    R.id.copy -> {
                        it.showToast("Invite code copy")
                        val clipboard = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                        val clip = ClipData.newPlainText("label", edOwnCode.text)
                        clipboard!!.setPrimaryClip(clip)
                    }
                    R.id.btnShare -> {
                        it.shareData(mPref.getOwnCode(Constants.OWN_CODE).toString())
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