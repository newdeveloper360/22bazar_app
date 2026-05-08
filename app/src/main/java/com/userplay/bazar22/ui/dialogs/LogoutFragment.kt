package com.userplay.bazar22.ui.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentLogoutBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogoutFragment : DialogFragment(R.layout.fragment_logout) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentLogoutBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.logout.setOnClickListener {
            activity?.let {
                mPref.setIsUserLogin(false)
                mPref.setIsSubscribedToTopic(false)
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