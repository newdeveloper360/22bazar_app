package com.userplay.bazar22.ui.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentErrorDialogBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BlockDialogFragment : DialogFragment(R.layout.fragment_error_dialog) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentErrorDialogBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErrorDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            val window: Window = dialog?.window!!
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            val attributes = window.attributes
            attributes.y = 80
            dialog?.window!!.attributes = attributes
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val message = bundle?.getString("message", "")
        val type = bundle?.getString("type", "")
        mBinding.tvError.text = message

        if (type.equals("maintain", true)) mBinding.ok.text = "OK"
        else mBinding.ok.text = "LOGOUT"

        mBinding.ok.setOnClickListener {
//            findNavController().popBackStack()
            activity?.let {
                if (type.equals("maintain", true)) {
                    it.finish()
                    return@setOnClickListener
                }
                mPref.setIsUserLogin(false)
                val intent = Intent(it, LoginActivity::class.java)
                startActivity(intent)
                it.finish()

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