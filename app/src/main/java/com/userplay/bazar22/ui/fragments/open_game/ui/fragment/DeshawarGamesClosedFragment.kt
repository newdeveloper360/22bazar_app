package com.userplay.bazar22.ui.fragments.open_game.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentDeshawarGamesClosedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeshawarGamesClosedFragment : DialogFragment(R.layout.fragment_deshawar_games_closed),
    View.OnClickListener {

    private var _binding: FragmentDeshawarGamesClosedBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeshawarGamesClosedBinding.inflate(inflater, container, false)
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

        val bundle = arguments
        val openTime = bundle?.getString("openTime", "")
        val closeTime = bundle?.getString("closeTime", "")
        val openResultTime = bundle?.getString("openResultTime", "")
        val closeResultTime = bundle?.getString("closeResultTime", "")
        val bidName = bundle?.getString("bidName", "")

        mBinding.apply {
            okDialogBox.setOnClickListener(this@DeshawarGamesClosedFragment)

            tvOpenResultTime.text = openTime
            tvCloseResultTime.text = closeTime
            title.text = bidName

            mBinding.apply {
                openBidLastLyt.visibility = View.GONE
                closeBidResultLyt.visibility = View.GONE
            }
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

                    R.id.ok_dialog_box -> {
                        dismiss()
                    }
                }
            }
        }
    }

}