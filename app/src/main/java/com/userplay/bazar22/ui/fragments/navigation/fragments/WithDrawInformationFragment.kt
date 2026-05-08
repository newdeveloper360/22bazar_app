package com.userplay.bazar22.ui.fragments.navigation.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentWithDrawInformationBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.RULE_NOTICE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithDrawInformationFragment : Fragment(R.layout.fragment_with_draw_information),
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentWithDrawInformationBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View {
        _binding = FragmentWithDrawInformationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {


        mBinding.apply {

            back.setOnClickListener(this@WithDrawInformationFragment)


            // tvSupportNumber.text = mPref.getSupportNumber(SUPPORT_NUMBER)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding.tvInfo.text = Html.fromHtml(
                    mPref.getRuleNotice(RULE_NOTICE),
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                mBinding.tvInfo.text = Html.fromHtml(mPref.getWithDrawCondition(RULE_NOTICE))
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