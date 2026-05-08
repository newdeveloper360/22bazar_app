package com.userplay.bazar22.ui.fragments.login


import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentMatkaBinding
import com.userplay.bazar22.preferences.MatkaPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MatkaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MatkaFragment : Fragment(R.layout.fragment_matka){
    var mp: MediaPlayer?=null
    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentMatkaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentMatkaBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding.root
    }


    private fun initView() {
        mBinding.apply {
            btnLogout.setOnClickListener {
                mPref.setMatkaEnable(false)
                val action =
                    MatkaFragmentDirections.actionMatkaFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            buyMatka.setOnClickListener {
                val url = "https://www.etsy.com/in-en/listing/1468067955/water-pot-to-storing-water-cool-and"
                val query = Uri.encode(url, "UTF-8")
                val browserIntent = Intent(Intent.CATEGORY_BROWSABLE, Uri.parse(Uri.decode(query)))
                browserIntent.action = Intent.ACTION_VIEW
                startActivity(browserIntent)
            }
            imgMatka.setOnClickListener {
                    mp = MediaPlayer.create(activity, R.raw.matka_sound)
                    mp?.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp?.release()
        mp=null
    }
}