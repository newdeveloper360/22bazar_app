package com.userplay.bazar22.ui.fragments.home.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentHomeBinding
import com.userplay.bazar22.models.get_markets.Market
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.LoginActivity
import com.userplay.bazar22.ui.activities.MainActivity
import com.userplay.bazar22.ui.callbacks.OnGameListener
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.MarketAdapter
import com.userplay.bazar22.ui.fragments.open_game.ui.activity.DeshawarGamesActivity
import com.userplay.bazar22.ui.fragments.open_game.ui.activity.OpenGameActivity
import com.userplay.bazar22.ui.fragments.open_game.ui.fragment.DeshawarGamesClosedFragment
import com.userplay.bazar22.ui.viewmodels.HomeViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.ENABLE_DESAWAR
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.SLIDER
import com.userplay.bazar22.utils.Constants.WHATSAPP_ENABLE
import com.userplay.bazar22.utils.Constants.WHATSAPP_NUMBER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), OnGameListener, View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by viewModels()
    private var mMarketsList: ArrayList<Market> = ArrayList()
    lateinit var mMarketAdapter: MarketAdapter
    var handler:Handler?=null
    private var isStateAlreadyInLoading:Boolean = false
    private var isAppDataStateAlreadyInLoading:Boolean = false
    val mSharedViewModels: SharedViewModels by activityViewModels()
    var isViewVisible=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentHomeBinding.inflate(inflater, container, false)
            initView()

        }
       handler= Handler(Looper.getMainLooper())
        return mBinding.root
    }

    private fun initView() {
        activity?.let {

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (childFragmentManager.backStackEntryCount == 0) {
                        // This is the HomeFragment
                        AlertDialog.Builder(requireContext())
                            .setTitle("Exit App")
                            .setMessage("Are you sure you want to exit?")
                            .setPositiveButton("Yes") { _, _ ->
                                requireActivity().finish()
                            }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        // There are fragments on the back stack, so just pop them
                        childFragmentManager.popBackStack()
                    }
                }
            }
            it.onBackPressedDispatcher.addCallback(it, callback)

            if (mPref.getHomePageImageUrl(Constants.IMAGE_URL) != null && mPref.getHomePageImageUrl(
                    Constants.IMAGE_URL
                ) != ""
            ) {
                Glide.with(this)
                    .asBitmap()
                    .load(mPref.getHomePageImageUrl(Constants.IMAGE_URL))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            if (resource != null) {
                                mBinding.homeImageView.setImageBitmap(resource)
                            } else {
                                mBinding.homeImageView.visibility = View.GONE
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            mBinding.homeImageView.visibility = View.GONE
                        }
                    })
            } else {
                mBinding.homeImageView.visibility = View.GONE
            }

        }
        val isResultOnly: Int
        if (mPref.getShowResultsOnly() == 1 || mPref.getMatkaEnable())
            isResultOnly = 1
        else isResultOnly = 0
        if (mPref.getEnableDesawarOnly() == 0) {
            mMarketAdapter = MarketAdapter(mMarketsList, this, isResultOnly, "market")
        } else {
            mMarketAdapter = MarketAdapter(mMarketsList, this, isResultOnly, "desawar")
        }

        mBinding.apply {
            openGame.setOnClickListener(this@HomeFragment)
            homeImageView.setOnClickListener(this@HomeFragment)
            addFund.setOnClickListener(this@HomeFragment)
            kingStarline.setOnClickListener(this@HomeFragment)
            llWithdarwal.setOnClickListener(this@HomeFragment)
            llGameRates.setOnClickListener(this@HomeFragment)
            tvPhone.setOnClickListener(this@HomeFragment)
            tvPhone1.setOnClickListener(this@HomeFragment)
            inviteEarnll.setOnClickListener(this@HomeFragment)

            headerText.text = mPref.getHomeMessage(Constants.HOME_MESSAGE)

            if (Constants.SHOW_STARLINE) {
                kingStarline.visibility = View.VISIBLE
                llGameRates.visibility = View.GONE
            } else {
                kingStarline.visibility = View.GONE
                llGameRates.visibility = View.VISIBLE
            }

            if (mPref.getEnableDesawar(ENABLE_DESAWAR) == 0) {
                openGame.visibility = View.GONE
                inviteEarnll.visibility = View.VISIBLE
                space17.visibility = View.VISIBLE
                space16.visibility = View.GONE
            } else {
                space16.visibility = View.GONE
                space17.visibility = View.VISIBLE
                openGame.visibility = View.VISIBLE
                inviteEarnll.visibility = View.GONE
            }

        }

        mBinding.rvGames.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mMarketAdapter
        }

        if (CheckNetwork.isNetworkConnected) {
            updateData()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
        }

        mBinding.apply {
            activity?.let {

                if (mPref.getTelegramEnable(Constants.TELEGRAM_ENABLE) == 1 && mPref.getWhatsAppEnable(
                        WHATSAPP_ENABLE
                    ) == 1
                ) {
                    (activity as MainActivity).setBottomNavIcon(R.drawable.ic_whatsapp_black)
                    imgIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.telegram))
                    imgIcon1.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.whatsapp))
                    tvPhone.text = mPref.getTelegramLink(Constants.TELEGRAM_LINK).toString()
                    tvPhone1.text = mPref.getWhatsAppNumber(Constants.WHATSAPP_NUMBER).toString()
                } else if (mPref.getWhatsAppEnable(WHATSAPP_ENABLE) == 1) {
                    tvPhone.text = mPref.getWhatsAppNumber(WHATSAPP_NUMBER).toString()
                    tvPhone1.text = mPref.getWhatsAppNumber(WHATSAPP_NUMBER).toString()
                    (activity as MainActivity).setBottomNavIcon(R.drawable.ic_whatsapp_black)
                    imgIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.whatsapp))
                    imgIcon1.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.whatsapp))
                } else if (mPref.getTelegramEnable(Constants.TELEGRAM_ENABLE) == 1) {
                    tvPhone.text = mPref.getTelegramLink(Constants.TELEGRAM_LINK).toString()
                    tvPhone1.text = mPref.getTelegramLink(Constants.TELEGRAM_LINK).toString()
                    (activity as MainActivity).setBottomNavIcon(R.drawable.ic_telegram_black)
                    imgIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.telegram))
                    imgIcon1.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.telegram))
                }else{
                    llSupports.visibility = View.GONE
                }
            }

            if (mPref.getShowResultsOnly() == 1 || mPref.getMatkaEnable()) {
                llBottomBtns.visibility = View.GONE
                llbtnList1.visibility = View.GONE
                llSupports.visibility = View.GONE
            }
        }

        if (mPref.getBlocked(Constants.BLOCKED) == 1) {
            logOutUser()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
    }
    private fun logOutUser() {
        mPref.setIsUserLogin(false)
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun updateData() {
        mHomeViewModel.mGetAppDataResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiState.Success -> {
                    isAppDataStateAlreadyInLoading=false
                    if (response.data?.error != null) {
                        if (response.data.error.not()) {
                            if (response.data.response?.appData?.version != null) {
                                response.data.response.appData.let { data ->
                                    data.let {
                                        mPref.setBalance(response.data.response.user?.balance)
                                        mPref.setBlocked(response.data.response.user?.blocked)
                                        mSharedViewModels.setBalance(response.data.response.user?.balance.toString())

                                        if (mPref.getBlocked(Constants.BLOCKED) == 1) {
                                            logOutUser()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ApiState.Error -> {
                    isAppDataStateAlreadyInLoading=false
                }
                is ApiState.Loading -> {
                    isAppDataStateAlreadyInLoading=true
                }
            }
        }

        //update data for 1st time
            updateMarketData()
        val updateIntervalMillis = 15_000L
        if (handler==null){
            handler= Handler(Looper.getMainLooper())
        }
        val periodicTask = object : Runnable {
            override fun run() {
                if (CheckNetwork.isNetworkConnected && isAdded && isVisible && isViewVisible) {
                    updateMarketData()
                    if (!isAppDataStateAlreadyInLoading){
                        mHomeViewModel.getAppData()
                    }
                }
                handler?.postDelayed(this, updateIntervalMillis)
            }
        }

        handler?.postDelayed(periodicTask, updateIntervalMillis)
    }



    private fun updateMarketData() {
        if (!isStateAlreadyInLoading){
            if (mPref.getEnableDesawarOnly() == 0) {
                mHomeViewModel.getGeneralMarket()
            } else {
                mHomeViewModel.getDesawarMarket()
            }
        }
    }

    override fun onPause() {
        isViewVisible=false
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewVisible=false
    }
    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler=null
    }

    override fun onResume() {
        isViewVisible=true
        super.onResume()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {
            mHomeViewModel.mMarketResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ApiState.Success -> {
                        isStateAlreadyInLoading=false
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", data.message)
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                } else {
                                    if (data.response?.markets != null) {
                                        mMarketsList.clear()
                                        mMarketsList.addAll(data.response.markets)
                                        mMarketAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        isStateAlreadyInLoading=false
                        Log.e("market_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        isStateAlreadyInLoading=true
                        //it.showProgressDialog()
                        Log.e("market_loading", "loading---->>>>")
                    }
                }
            }

    }


    override fun onGameClosedClick(
        position: Int?,
        openTime: String?,
        closeTime: String?,
        openResultTime: String?,
        closeResultTime: String?,
        bidName: String?
    ) {

        if (mPref.getEnableDesawarOnly() == 0) {
            val action = HomeFragmentDirections.actionHomeFragmentToBidClosedDialogFragment(
                openTime.toString(),
                closeTime.toString(),
                openResultTime.toString(),
                closeResultTime.toString(),
                bidName,
                GENERAL_MARKET
            )
            findNavController().navigate(action)
        } else {
            Log.e(
                "close time",
                "" + openTime + " " + closeTime + " " + openResultTime + " " + closeResultTime
            )
            val dialog = DeshawarGamesClosedFragment()
            val bundle = Bundle()
            bundle.putString("openTime", openTime)
            bundle.putString("closeTime", closeTime)
            bundle.putString("openResultTime", openResultTime)
            bundle.putString("closeResultTime", closeResultTime)
            bundle.putString("bidName", bidName)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "OpenGame")
        }

    }

    override fun onGameStartClick(
        position: Int,
        marketID: Int,
        mGameName: String,
        openStatus: Boolean
    ) {
        if (mPref.getShowResultsOnly() == 0 && !mPref.getMatkaEnable()) {
            if (mPref.getEnableDesawarOnly() == 0) {
                val action = HomeFragmentDirections.actionHomeFragmentToSelectGameFragment(
                    marketID, mGameName, GENERAL_MARKET, openStatus
                )
                findNavController().navigate(action)
            } else {
                startActivity(
                    Intent(requireContext(), OpenGameActivity::class.java).putExtra(
                        "marketID",
                        marketID
                    )
                        .putExtra("gameName", mGameName)
                )
            }
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.open_game -> {
                        startActivity(Intent(it, DeshawarGamesActivity::class.java))
                    }

                    R.id.addFund -> {
                        findNavController().navigate(R.id.fundsFragment)
                    }

                    R.id.inviteEarnll -> {
                        findNavController().navigate(R.id.inviteAndEarnFragment)
                    }

                    R.id.king_starline -> {
                        findNavController().navigate(R.id.action_homeFragment_to_starLineFragment)
                    }

                    R.id.llWithdarwal -> {
                        findNavController().navigate(R.id.action_homeFragment_to_withdrawFundsFragment)
                    }

                    R.id.llGameRates -> {
                        findNavController().navigate(R.id.action_homeFragment_to_gamesRatesFragment)
                    }

                    R.id.tvPhone -> {
                        if (mPref.getTelegramEnable(Constants.TELEGRAM_ENABLE) == 1) {

                            val telegramIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(mPref.getTelegramLink(Constants.TELEGRAM_LINK))
                                )
                            startActivity(telegramIntent)
                        } else if (mPref.getWhatsAppEnable(Constants.WHATSAPP_ENABLE) == 1) {
                            val url =
                                "https://api.whatsapp.com/send?phone=${
                                    mPref.getWhatsAppNumber(
                                        WHATSAPP_NUMBER
                                    )
                                }"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        }
                    }

                    R.id.tvPhone1 -> {
                        if (mPref.getWhatsAppEnable(Constants.WHATSAPP_ENABLE) == 1) {
                            val url =
                                "https://api.whatsapp.com/send?phone=${
                                    mPref.getWhatsAppNumber(
                                        WHATSAPP_NUMBER
                                    )
                                }"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } else if (mPref.getTelegramEnable(Constants.TELEGRAM_ENABLE) == 1) {
                            val telegramIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(mPref.getTelegramLink(Constants.TELEGRAM_LINK))
                                )
                            startActivity(telegramIntent)
                        }
                    }

                    R.id.homeImageView -> {
                        val url = mPref.getSliderUrl(SLIDER)
                        if (!url.isNullOrEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(browserIntent)
                        }
                    }
                }
            }
        }
    }
}