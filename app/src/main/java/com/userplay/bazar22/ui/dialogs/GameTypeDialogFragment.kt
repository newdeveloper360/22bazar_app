package com.userplay.bazar22.ui.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentGameTypeDialogBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants.CLOSE_GAME_TYPE
import com.userplay.bazar22.utils.Constants.OPEN_GAME_TYPE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameTypeDialogFragment : DialogFragment(R.layout.fragment_game_type_dialog),
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentGameTypeDialogBinding? = null
    private val mBinding get() = _binding!!
    private val mSharedViewModels : SharedViewModels by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameTypeDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mBinding.apply {
            open.setOnClickListener(this@GameTypeDialogFragment)
            close.setOnClickListener(this@GameTypeDialogFragment)
            cross.setOnClickListener(this@GameTypeDialogFragment)
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {

                    R.id.open -> {
                        mPref.setSessionType(OPEN_GAME_TYPE)
                        mSharedViewModels.setGameType(OPEN_GAME_TYPE)
                        findNavController().popBackStack()
                    }

                    R.id.close -> {
                        mPref.setSessionType(CLOSE_GAME_TYPE)
                        mSharedViewModels.setGameType(CLOSE_GAME_TYPE)
                        findNavController().popBackStack()
                    }

                    R.id.cross -> {
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult("DIALOG_REQUEST_KEY", bundleOf("DIALOG_RESULT_KEY" to true))
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