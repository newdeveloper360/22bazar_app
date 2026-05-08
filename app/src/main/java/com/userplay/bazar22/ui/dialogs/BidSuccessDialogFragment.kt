package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentBidSuccessDialogBinding
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BidSuccessDialogFragment : DialogFragment(R.layout.fragment_bid_success_dialog),
    View.OnClickListener {

    private var _binding: FragmentBidSuccessDialogBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mFrom: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBidSuccessDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("submit userplay", "onviewcreated")
        //  mFrom = arguments?.getString("from").toString()

        val bundle = arguments
        mFrom = bundle?.getString("from", "").toString()
        initView()
    }

    private fun initView() {
        mBinding.apply {
            btnOk.setOnClickListener(this@BidSuccessDialogFragment)
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.btnOk -> {
                        when (mFrom) {

                            STARLINE_MARKET -> {
                                dismiss()
                                findNavController().popBackStack(R.id.homeFragment, false)
                            }

                            GENERAL_MARKET -> {
                                dismiss()
                                findNavController().popBackStack(R.id.homeFragment, false)
                            }

                            DESAWAR_MARKET -> {
                                activity?.finish()
                                //  findNavController().popBackStack(R.id.jantriFragment, false)
                            }
                        }
                        dismiss()
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