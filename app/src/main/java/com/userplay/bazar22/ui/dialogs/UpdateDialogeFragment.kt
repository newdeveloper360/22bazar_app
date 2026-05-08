package com.userplay.bazar22.ui.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentUpdateDialogeBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants.APP_UPDATE_LINK
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateDialogeFragment : DialogFragment(R.layout.fragment_update_dialoge) {

    @Inject
    lateinit var mPref: MatkaPref

    private var _binding: FragmentUpdateDialogeBinding? = null
    private val mBinding get() = _binding!!
    private val mSharedViewModels: SharedViewModels by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateDialogeBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.submit.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mPref.getAppUpdateLink(APP_UPDATE_LINK))
                )
            )
        }

        mBinding.cancel.setOnClickListener {
            mSharedViewModels.setCanceld(true)
            dismiss()
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