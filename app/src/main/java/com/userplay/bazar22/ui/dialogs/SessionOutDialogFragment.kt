package com.userplay.bazar22.ui.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSessionOutDialogBinding
import com.userplay.bazar22.ui.activities.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionOutDialogFragment : DialogFragment(R.layout.fragment_session_out_dialog),
    View.OnClickListener {

    private var _binding: FragmentSessionOutDialogBinding? = null
    private val mBinding get() = _binding!!
    private var onDismissDialog: (() -> Unit)? = null
    companion object {
        fun newInstance(onDismiss: () -> Unit): SessionOutDialogFragment = SessionOutDialogFragment().apply {
            this.onDismissDialog=onDismiss
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionOutDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mBinding.apply {
            logout.setOnClickListener(this@SessionOutDialogFragment)
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.logout -> {
                        activity?.let {
                            val intent = Intent(it, LoginActivity::class.java)
                            startActivity(intent)
                            it.finish()
                            dismiss()
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