package com.userplay.bazar22.ui.fragments.funds.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.lib.pay.from.libpfu.PaymentManager
import com.lib.pay.from.libpfu.PaymentManagerImp
import com.lib.pay.from.libpfu.callbacks.PaymentCallbacks
import com.lib.pay.from.libpfu.models.CreatePaymentResponse
import com.lib.pay.from.libpfu.models.SubmitPaymentResponse
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentAddFundsBinding
import com.userplay.bazar22.models.PayUrlIntentResponse
import com.userplay.bazar22.models.UpiMoneyResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.ItemClickListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.PayDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.AmountsAdapter
import com.userplay.bazar22.ui.viewmodels.PayUrlViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.NAME
import com.userplay.bazar22.utils.Constants.PHONE
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AddFundsFragment : Fragment(R.layout.fragment_add_funds), View.OnClickListener,
    PaymentCallbacks {

    @Inject
    lateinit var mPref: MatkaPref
    var amountDeposit: Int = 0

    @Inject
    lateinit var mApiInterface: ApiInterface
    private var pay_url: String = ""
    private var _binding: FragmentAddFundsBinding? = null
    private val mBinding get() = _binding!!
    private val payUrlViewModel: PayUrlViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by activityViewModels()
    lateinit var paymentManager:PaymentManager


    var mAdapter: AmountsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFundsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentManager= PaymentManagerImp()
        paymentManager.initialize(requireActivity())
        // Set the callbacks
        paymentManager.setCallbacks(this,false)

        initView()
        observer()
        appUpdateDataObserver()
    }

    private fun initView() {

        mBinding.apply {
            btnAddCash.setOnClickListener(this@AddFundsFragment)
            back.setOnClickListener(this@AddFundsFragment)
            name.text = mPref.getName(NAME)
            tvPhone.text = mPref.getPhone(PHONE)
            mAdapter = AmountsAdapter(payUrlViewModel.listAmount, object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    edAmount.setText("$position")
                }
            }
            )
            rvAmounts.adapter = mAdapter
        }
    }

    private var open_screen = true

//    private fun openUrlInChrome(url: String) {
//        val builder = CustomTabsIntent.Builder()
//        builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.red))
//        val customTabsIntent = builder.build()
//        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
//    }

    private fun openUrlInChrome(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.red))
        val customTabsIntent = builder.build()

        // Try to force Chrome first
        val chromePackage = "com.android.chrome"
        val packageManager = requireContext().packageManager

        // Check if Chrome is installed
        val isChromeInstalled = try {
            packageManager.getPackageInfo(chromePackage, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        // If Chrome is installed, force it; otherwise, let the system decide
        if (isChromeInstalled) {
            customTabsIntent.intent.setPackage(chromePackage)
        }
        // Launch the URL
        try {
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        } catch (e: ActivityNotFoundException) {
            // Fallback: Open in default browser if Custom Tabs is not supported
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            requireContext().startActivity(intent)
        }
    }

    private fun observer() {
        activity?.let {

            //for getting payment link
            payUrlViewModel.mPayFromUpiUrlResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        if (response.data?.error != null) {
                            if (response.data.error) {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
                            } else {
                                if (mBinding.edAmount.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please Enter amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@observe
                                }
                                pay_url = response.data.response?.paymentUrl.toString()
                                val openWebView = response.data.response?.openWebView ?: false
                                if (!openWebView) {
                                    openUrlInChrome(pay_url)
                                } else {
                                    open_screen = false
                                    val action =
                                        AddFundsFragmentDirections.actionAddFundsFragmentToAddFundWebViewFragment(
                                            mBinding.edAmount.text.toString().toInt(), pay_url
                                        )
                                    findNavController().navigate(action)
                                }
                                mBinding.edAmount.text?.clear()
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("login_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("signup_loading", "loading---->>>>")
                    }
                }
            }

            //for getting payment link
            payUrlViewModel.mPayUrlResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        if (response.data?.error != null) {
                            if (response.data.error) {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
                            } else {
                                pay_url = response.data.response?.paymentUrl.toString()
//                                if (open_screen) {
//                                    open_screen = false
//                                    val action =
//                                        AddFundsFragmentDirections.actionAddFundsFragmentToAddFundWebViewFragment(
//                                            0, pay_url
//                                        )
//                                    findNavController().navigate(action)
//                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("login_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("signup_loading", "loading---->>>>")
                    }
                }
            }

            //for updating balance
            payUrlViewModel.mUpdateBalanceResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        if (response.data?.error != null) {
                            if (response.data.error) {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
                            } else {
                                //GatewayTest
                                Toast.makeText(
                                    requireContext(),
                                    response.data.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                mBinding.tvBalance.text =
                                    response.data.response?.balanceLeft.toString()
                                mSharedViewModels.setBalance(response.data.response?.balanceLeft.toString())
                                mPref.setBalance(response.data.response?.balanceLeft)
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("login_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("signup_loading", "loading---->>>>")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        payUrlViewModel.getAppData()
        /*mBinding.tvBalance.text =
            resources.getString(R.string.ruppes_symbol) + mPref.getBalance(Constants.BALANCE)
                .toString()*/
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val response = data?.getStringExtra("response")
                Log.d("GatewayTest", "response: $response")
                if (response != null) {
                    // Parse the response to determine the payment status
                    payUrlViewModel.updateBalance(
                        mBinding.edAmount.text.toString().toInt(),
                        response
                    )
                } else {
                    payUrlViewModel.updateBalance(
                        mBinding.edAmount.text.toString().toInt(),
                        "null"
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Payment Not Completed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    var resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val response = data?.getStringExtra("response")
                activity?.let {
                    it.showToast("Balance will update within 60 Seconds")
                }

            } else {
                Toast.makeText(requireContext(), "Payment Not Completed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    @SuppressLint("SetTextI18n")
    private fun appUpdateDataObserver() {
        payUrlViewModel.mGetAppDataResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiState.Success -> {
                    requireActivity()?.dismissDialog()
                    if (response.data?.error != null) {
                        if (response.data.error.not()) {
                            if (response.data.response?.appData?.version != null) {
                                response.data.response.appData.let { data ->
                                    data.let {
                                        mPref.setBalance(response.data.response.user?.balance)
                                        mSharedViewModels.setBalance(response.data.response.user?.balance.toString())
                                        mBinding.tvBalance.text =
                                            resources.getString(R.string.ruppes_symbol) + mPref.getBalance(
                                                Constants.BALANCE
                                            ).toString()
                                    }
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    requireActivity()?.dismissDialog()
                }

                is ApiState.Loading -> {
                    requireActivity()?.showProgressDialog()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.btn_add_cash -> {
                        if (edAmount.text.isEmpty()) {
                            edAmount.error = " please add amount"
                            return@let
                        } else {
                            if (CheckNetwork.isNetworkConnected) {
                                if (edAmount.text.toString().toLong() < mPref.getMinDeposit(
                                        Constants.MIN_DEPOSIT
                                    )
                                ) {
                                    edAmount.error =
                                        "Minimum Deposit Amount is " + mPref.getMinDeposit(
                                            Constants.MIN_DEPOSIT
                                        )
                                    return@let
                                }
                                if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("ibr_pay")
                                ) {
                                    payUpiPaymentUrl(
                                        edAmount.text.toString().toInt()
                                    ) { resp ->
                                        PayDialogFragment.newInstance(
                                            edAmount.text.toString().toInt()
                                        ) {
                                            amountDeposit = edAmount.text.toString().toInt()
                                            when (it) {
                                                "close" -> {}
                                                "bheem" -> {
                                                    /* parentFragmentListener?.navigateFragment(
                                                         WalletFragmentDirections.actionFragmentWalletToAddFundWebViewFragment(
                                                             edAmount.text.toString().toInt(), resp.response.upiIntent.bhimLink
                                                         )
                                                     )*/
                                                    openNewUpi(resp.response.upiIntent)
                                                    // mActivity?.openBrowser(resp.response.upiIntent.bhimLink)
                                                }

                                                "phonepe" -> {
                                                    openNewUpi(resp.response.upiIntent)
                                                    //mActivity?.openBrowser(resp.response.upiIntent.phonepeLink)
                                                }

                                                "paytm" -> {
                                                    openNewUpi(resp.response.upiIntent)
                                                    //mActivity?.openBrowser(resp.response.upiIntent.paytmLink)
                                                }

                                                "gpay" -> {
                                                    openNewUpi(resp.response.upiIntent)
                                                    //mActivity?.openBrowser(resp.response.upiIntent.gpayLink)
                                                }
                                            }
                                        }.show(childFragmentManager, "PaymentDialog")
                                    }
                                } else if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("direct_upi")
                                ) {
                                    //upi intent
                                    val amount = edAmount.text.toString()
                                    val admin_upi = mPref.getAdminUpi(Constants.ADMIN_UPI)
                                    val app_name = requireContext().getString(R.string.app_name)
                                    val tn = getRandomString(20)
                                    val upiURI =
                                        "upi://pay?pa=$admin_upi&pn=$app_name&mc=&tn=PaymentForApp&am=$amount&mc=5411&cu=INR&tn=$tn&tr=$tn"
                                    //log upiURL
                                    Log.d("GatewayTest", "upiURI: $upiURI")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiURI))
                                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                                        resultLauncher.launch(intent)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "No application available to handle this request!",
                                            Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                } else if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("auto")
                                ) {
                                    open_screen = true
                                    payUrlViewModel.getPayUrl(edAmount.text.toString().toInt())
                                    return@let
                                } else if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("pay_from_upi")
                                ) {

                                    open_screen = true
                                    payUrlViewModel.getPayFromUpiUrl(
                                        edAmount.text.toString().toInt()
                                    )
                                    return@let
                                } else if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("pay_from_upi_sdk")
                                ) {
                                    //sdk code implementation
                                    lifecycleScope.launch {

                                        paymentManager.createTransaction(
                                            bearerToken = Constants.PFU_SDK_API_KEY,
                                            userName = mPref.getName(Constants.NAME).toString(),
                                            email = "test@gmail.com",
                                            mobile = mPref.getPhone(Constants.PHONE).toString(),
                                            amount = edAmount.text.toString().toInt(),
                                            "",
                                            1
                                        )
                                    }
                                    return@let
                                } else if (mPref.getPaymentMethod(Constants.PAYMENT_METHOD)
                                        .equals("upi_money")
                                ) {
                                    upiMoneyPaymentUrl(edAmount.text.toString().toInt()) { resp ->
                                        val action =
                                            AddFundsFragmentDirections.actionAddFundsFragmentToAddFundWebViewFragment(
                                                edAmount.text.toString().toInt(),
                                                resp.response.paymentLink
                                            )
                                        findNavController().navigate(action)
                                    }
                                } else {
                                    pay_url =
                                        Constants.LIVE_SERVER + "payment/" + mPref.getID(Constants.ID) + "/" + edAmount.text.toString()
                                            .toInt()

                                    val action =
                                        AddFundsFragmentDirections.actionAddFundsFragmentToAddFundWebViewFragment(
                                            edAmount.text.toString().toInt(), pay_url
                                        )
                                    findNavController().navigate(action)
                                }
                            } else {
                                val dialog = InternetErrorDialogFragment()
                                dialog.show(childFragmentManager, "internet")
                            }
                        }
                    }

                    R.id.back -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun upiMoneyPaymentUrl(
        amount: Int,
        result: (UpiMoneyResponse) -> Unit
    ) {
        payUrlViewModel.viewModelScope.launch {
            context?.let { activity ->
                getActivity()?.showProgressDialog()
                val response = mApiInterface.upiMoneyPaymentUrl(amount)
                if (response.isSuccessful) {
                    getActivity()?.dismissDialog()
                    response.body()?.let {
                        if (!it.error) {
                            result.invoke(it)
                        } else {
                            getActivity()?.showToast(it.message)
                        }
                    } ?: run {
                        getActivity()?.showToast("We are unable to process your request try again.")
                    }
                } else {
                    getActivity()?.showToast("We are unable to process your request try again.")
                    getActivity()?.dismissDialog()
                }

            }
        }
    }

    fun payUpiPaymentUrl(
        amount: Int,
        result: (PayUrlIntentResponse) -> Unit
    ) {
        payUrlViewModel.viewModelScope.launch {
            context?.let { activity ->
                getActivity()?.showProgressDialog()
                val response = mApiInterface.getPayUrlIntent(amount)
                if (response.isSuccessful) {
                    getActivity()?.dismissDialog()
                    response.body()?.let {
                        if (!it.error) {
                            result.invoke(it)
                        } else {
                            getActivity()?.showToast(it.message)
                        }
                    } ?: run {
                        getActivity()?.showToast("We are unable to process your request try again.")
                    }
                } else {
                    getActivity()?.showToast("We are unable to process your request try again.")
                    getActivity()?.dismissDialog()
                }

            }
        }
    }

    fun openNewUpi(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            resultLauncher2.launch(intent)
        } else {
            Toast.makeText(
                requireContext(),
                "No application available to handle this request!",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    override fun onCreateFailed(error: String) {
        Log.d("GatewayTest", "onCreateFailed: $error")
        Toast.makeText(
            requireContext(),
            "Error: $error",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreateSuccess(response: CreatePaymentResponse) {
        Toast.makeText(
            requireContext(),
            "Payment Created:",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPaymentSubmitFailed(error: String) {
        Log.d("GatewayTest", "onPaymentSubmitFailed: $error")
        Toast.makeText(
            requireContext(),
            "Error: $error",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPaymentSubmitSuccess(response: SubmitPaymentResponse) {
        Toast.makeText(
            requireContext(),
            "Payment Success:",
            Toast.LENGTH_SHORT
        ).show()
        payUrlViewModel.getAppData()
    }

}