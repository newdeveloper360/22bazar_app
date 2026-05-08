package com.userplay.bazar22.ui.fragments.funds.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentAddFundWebViewBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddFundWebViewFragment : Fragment(R.layout.fragment_add_fund_web_view) {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentAddFundWebViewBinding? = null
    private val mBinding get() = _binding!!
    private val mArgs: AddFundWebViewFragmentArgs by navArgs()
    private var amount: Int? = null
    private var phone: String = ""
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: android.os.Bundle?
    ): View = FragmentAddFundWebViewBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val payUrl = mArgs.payUrl
        amount = mArgs.amount
        phone = mPref.getPhone(Constants.PHONE).toString()
        email = "$phone@gmail.com"

        setupBackHandler()
        configureWebView()
        mBinding.webView.loadUrl(payUrl)
    }

    private fun setupBackHandler() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mBinding.webView.canGoBack()) mBinding.webView.goBack()
                    else findNavController().popBackStack()
                }
            }
        )
        mBinding.webView.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                (v as WebView).takeIf { it.canGoBack() }?.goBack()
                    ?: findNavController().popBackStack()
                true
            } else false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        with(mBinding.webView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString = CHROME_MOBILE_UA
            }

            // JS bridge for toasts
            addJavascriptInterface(JsBridge(requireContext()), "Android")

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(cm: ConsoleMessage) =
                    Log.d("WEBXView", cm.message()).let { true }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onCreateWindow(
                    view: WebView, isDialog: Boolean,
                    isUserGesture: Boolean, resultMsg: Message
                ): Boolean {
                    val transport = resultMsg.obj as WebView.WebViewTransport
                    transport.webView = WebView(view.context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = mBinding.webView.webViewClient
                    }
                    resultMsg.sendToTarget()
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?, url: String?, favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    mBinding.progressBar.visibility = View.VISIBLE
                    mBinding.webView.visibility = View.GONE
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    mBinding.progressBar.visibility = View.GONE
                    mBinding.webView.visibility = View.VISIBLE
                    injectAutoFillJs(view)
                }


                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {

                    if (!URLUtil.isNetworkUrl(url)) {
                        try {
                            val intent = Intent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(activity, "App Not found.", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                    if (url?.contains("exitme")!! || url.contains("exit")) {
                        findNavController().popBackStack()
                        return true
                    }
                    return false
                }


                @SuppressLint("DeprecatedInJava")
                override fun onReceivedError(
                    view: WebView?, errorCode: Int, description: String,
                    failingUrl: String?
                ) {
                    activity?.showToast("Error:$description")
                }
            }
        }
    }

//    private fun injectAutoFillJs(webView: WebView) {
//        webView.evaluateJavascript(AUTO_FILL_JS, null)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class JsBridge(private val context: Context) {
        @JavascriptInterface
        fun showToast(msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun injectAutoFillJs(webView: WebView) {
        // Get the current values
        val amountValue = amount ?: 1
        val phoneValue = if (phone.isNotEmpty()) phone else "8855885588"
        val emailValue = if (email.isNotEmpty()) email else "$phone@gmail.com"

        // Create the JS script with current values
        val jsScript = """
    (function(){
        // Common functions
        function waitForElement(selector, maxTries, callback) {
            var tries = 0;
            var interval = setInterval(function() {
                var element = document.querySelector(selector);
                if (element) {
                    clearInterval(interval);
                    callback(element);
                } else if (++tries >= maxTries) {
                    clearInterval(interval);
                }
            }, 500);
        }
        
        function setValueWithEvents(element, value) {
            element.focus();
            element.value = value;
            element.dispatchEvent(new Event('input', {bubbles: true}));
            element.dispatchEvent(new Event('change', {bubbles: true}));
            element.readOnly = true;
            element.blur();
            
            element.style.backgroundColor = '#f0f0f0';
            element.style.cursor = 'not-allowed';
        }
        
        function clickAfterDelay(element, delay, message) {
                element.click();
                console.log(message);
        }
        
        // Step 1: Amount & Next
        waitForElement('input.Field-el[type=tel]', 20, function(amt) {
            setValueWithEvents(amt, '$amountValue');
            
            waitForElement('button.footer-btn', 5, function(next) {
                clickAfterDelay(next, 1000, '👉 Next clicked');
                
                // Step 2: Email/Phone & Proceed
                waitForElement('input[name="email"], input[name="phone"]', 20, function() {
                    var email = document.querySelector('input[name="email"]');
                    var phone = document.querySelector('input[name="phone"]');
                    var pay = document.querySelector('button.footer-btn');
                    
                    if (email && phone && pay) {
                        setValueWithEvents(email, '$emailValue');
                        setValueWithEvents(phone, '$phoneValue');
                        clickAfterDelay(pay, 1000, '💳 Proceed clicked');
                    }
                });
            });
        });
    })();
    """.trimIndent()

        // Execute the script
        webView.evaluateJavascript(jsScript, null)
    }

    private val CHROME_MOBILE_UA =
        "Mozilla/5.0 (Linux; Android 11; Pixel 5) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/114.0.5735.61 Mobile Safari/537.36"
}