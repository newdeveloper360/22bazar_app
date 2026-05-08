package com.userplay.bazar22.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.PayDailogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayDialogFragment : DialogFragment(R.layout.pay_dailog_fragment){
    var binding:PayDailogFragmentBinding?=null
    var mActivity: FragmentActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayDailogFragmentBinding.inflate(layoutInflater)
        mActivity = activity
        isCancelable = false
        bind()
        return binding!!.root
    }
    var amount=0
    private var onItemClick: ((selected:String) -> Unit?)? = null
    companion object {
        fun newInstance(amount:Int,onItemClick: (selected:String) -> Unit): PayDialogFragment = PayDialogFragment().apply {
            this.amount=amount
            this.onItemClick=onItemClick
        }

    }

    fun bind() {
        binding?.llBhim?.setOnClickListener {
            onItemClick?.invoke("bheem")
            dismiss()
        }
        binding?.llPhonepe?.setOnClickListener {
            onItemClick?.invoke("phonepe")
            dismiss()
        }
        binding?.llPaytm?.setOnClickListener {
            onItemClick?.invoke("paytm")
            dismiss()
        }
        binding?.llGpay?.setOnClickListener {
            onItemClick?.invoke("gpay")
            dismiss()
        }
    }
}