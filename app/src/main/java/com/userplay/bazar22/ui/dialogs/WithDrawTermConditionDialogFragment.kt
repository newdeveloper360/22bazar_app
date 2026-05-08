package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentWithDrawTermConditionDialogBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithDrawTermConditionDialogFragment :
    DialogFragment(R.layout.fragment_with_draw_term_condition_dialog) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentWithDrawTermConditionDialogBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithDrawTermConditionDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.DKGRAY))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            val window: Window = dialog?.window!!
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

           // wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            val attributes = window.attributes
            attributes.y = 80
            dialog?.window!!.attributes = attributes
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.yesDialogBox.setOnClickListener {
            dismiss()
        }
        initView()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.tvInfo.text = Html.fromHtml(
                mPref.getRuleNotice(Constants.WITHDRAW_CONDITION),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            mBinding.tvInfo.text = Html.fromHtml(mPref.getWithDrawCondition(Constants.WITHDRAW_CONDITION))
        }
    }

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}