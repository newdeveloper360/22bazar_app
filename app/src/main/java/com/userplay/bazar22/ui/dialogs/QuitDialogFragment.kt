package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentQuitDialogBinding
import com.userplay.bazar22.preferences.MatkaPref
import javax.inject.Inject


class QuitDialogFragment : DialogFragment(R.layout.fragment_quit_dialog), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentQuitDialogBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuitDialogBinding.inflate(inflater, container, false)
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
            noDialogBox.setOnClickListener(this@QuitDialogFragment)
            yesDialogBox.setOnClickListener(this@QuitDialogFragment)
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
                        activity?.finish()
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