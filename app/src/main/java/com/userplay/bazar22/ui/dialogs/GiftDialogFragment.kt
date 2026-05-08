package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentGiftDialogBinding
import com.userplay.bazar22.ui.viewmodels.RedeemGiftViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiftDialogFragment : DialogFragment(R.layout.fragment_gift_dialog),
    View.OnClickListener {

    private var _binding: FragmentGiftDialogBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: RedeemGiftViewModel by viewModels()
    var balance:Int=0
    private var onDismissDialog: ((balance:Int) -> Unit?)? = null
    companion object {
        fun newInstance(onDismiss: (selected:Int) -> Unit): GiftDialogFragment = GiftDialogFragment().apply {
            this.onDismissDialog=onDismiss
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGiftDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mBinding.apply {
            btnSubmitGift.setOnClickListener(this@GiftDialogFragment)
            imgClose.setOnClickListener(this@GiftDialogFragment)
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.imgClose -> {
                        dismiss()
                        onDismissDialog?.let { it1 -> it1(balance) }
                    }
                    R.id.btnSubmitGift -> {
                        if(edGiftCode.text.toString().isNotBlank()){
                            mProgress.visibility=View.VISIBLE
                            btnSubmitGift.visibility=View.GONE
                            viewModel.redeemGift(edGiftCode.text.toString(), onSuccess = {
                                lottieView.visibility=View.VISIBLE
                                lottieView.playAnimation()
                                mProgress.visibility=View.GONE
                                llEditText.visibility=View.GONE
                                tvHeading.text = "Thank you!"
                                tvGiftDescription.text = "Your gift has been redeemed successfully. You have received ${it.response.amountWon}"
                                tvGiftDescription.visibility = View.VISIBLE
                                balance = it.response.userBalance
                                tvErrorGift.visibility=View.GONE
                            }, onFailure = {
                                tvErrorGift.text = it
                                mProgress.visibility=View.GONE
                                btnSubmitGift.visibility=View.VISIBLE
                            })
                        }

                    }
                }
            }
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