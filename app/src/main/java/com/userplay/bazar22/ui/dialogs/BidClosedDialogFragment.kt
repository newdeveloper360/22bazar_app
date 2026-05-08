package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentBidClosedDialogBinding
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BidClosedDialogFragment : DialogFragment(R.layout.fragment_bid_closed_dialog),
    View.OnClickListener {

    private var _binding: FragmentBidClosedDialogBinding? = null
    private val mBinding get() = _binding!!
    private val mArgs: BidClosedDialogFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBidClosedDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }


    private fun initView() {
        mBinding.apply {
            okDialogBox.setOnClickListener(this@BidClosedDialogFragment)
            tvOpenTime.text = mArgs.openTime
            title.text = mArgs.bidTitle
            when (mArgs.from) {

                Constants.GENERAL_MARKET -> {
                    tvOpenResultTime.text = mArgs.openResultTime
                    tvCloseTime.text = mArgs.closeTime
                    tvCloseResultTime.text = mArgs.closeResultTime
                }

                Constants.STARLINE_MARKET -> {
                    closedBidLastLyt.visibility = View.GONE
                    closeBidResultLyt.visibility = View.GONE
                    openBidLastLyt.visibility = View.GONE
                }
            }
        }
    }

    private fun observer() {

    }

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.ok_dialog_box -> {
                        dismiss()
                    }
                }
            }
        }
    }
}