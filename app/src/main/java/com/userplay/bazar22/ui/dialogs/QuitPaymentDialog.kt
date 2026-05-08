package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentQuitPaymentDialogBinding
import com.userplay.bazar22.preferences.MatkaPref
import javax.inject.Inject


class QuitPaymentDialog : DialogFragment(R.layout.fragment_quit_payment_dialog),
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentQuitPaymentDialogBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuitPaymentDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            noDialogBox.setOnClickListener(this@QuitPaymentDialog)
            yesDialogBox.setOnClickListener(this@QuitPaymentDialog)
        }
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
                    R.id.no_dialog_box -> {
                        dismiss()
                    }
                    R.id.yes_dialog_box -> {
                        //go back to preivosu screen
                        dismiss()
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