package com.userplay.bazar22.ui.fragments.navigation.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentChartsBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.CHART_URL
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChartsFragment : Fragment(R.layout.fragment_charts) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentChartsBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {

        activity?.let {
            val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        findNavController().popBackStack()
                    }
                }
            it.onBackPressedDispatcher.addCallback(it, callback)
        }





        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mBinding.progressBar.visibility = View.VISIBLE
                mBinding.webView.visibility = View.GONE
            }


            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {

                if (url != null) {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                mBinding.progressBar.visibility = View.GONE
                mBinding.webView.visibility = View.VISIBLE
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String,
                failingUrl: String?
            ) {
                activity?.showToast("Error:$description")

            }
        }
        mBinding.webView.loadUrl(mPref.getChartsUrl(CHART_URL).toString())

//        mBinding.webView.loadUrl(mPref.getChartsUrl(CHART_URL).toString())
//        // Enable Javascript
        val webSettings: WebSettings = mBinding.webView.settings
        webSettings.javaScriptEnabled = true
        // Force links and redirects to open in the WebView instead of in a browser
//        mBinding.webView.webViewClient = WebViewClient()
    }

    private fun observer() {

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}