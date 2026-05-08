package com.userplay.bazar22.ui.fragments.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSupportBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SupportFragment : Fragment(R.layout.fragment_support) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSupportBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url =
            "https://api.whatsapp.com/send?phone=${mPref.getSupportNumber(Constants.SUPPORT_NUMBER)}"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)

        findNavController().popBackStack()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}