package com.userplay.bazar22.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.ActivityMainBinding
import com.userplay.bazar22.models.navigation.NawDrawerPanelItem
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.QuitDialogFragment
import com.userplay.bazar22.ui.dialogs.QuitPaymentDialog
import com.userplay.bazar22.ui.fragments.navigation.adapters.NewCustomDrawerAdapter
import com.userplay.bazar22.ui.fragments.navigation.callback.OnMenuItemClickListener
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.NAME
import com.userplay.bazar22.utils.Constants.PHONE
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.ui.dialogs.GiftDialogFragment
import com.userplay.bazar22.ui.dialogs.SessionOutDialogFragment
import com.userplay.bazar22.ui.viewmodels.PayUrlViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMenuItemClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private val payUrlViewModel: PayUrlViewModel by viewModels()
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mNavController: NavController
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var drawerMenuLayoutManager: LinearLayoutManager
    private var drawerItemList = ArrayList<NawDrawerPanelItem>()
    private lateinit var mHomeButton: ImageButton
    private lateinit var mQuitDialogFragment: QuitDialogFragment
    private lateinit var quitPaymentDialog: QuitPaymentDialog
    private val mSharedViewModels: SharedViewModels by viewModels()
    private val mNewCustomDrawerAdapter by lazy {
        NewCustomDrawerAdapter(
            drawerItemList, this
        )
    }
    private var sessionJob: Job? = null
    var isSessionTimeOut = false
    var lastActiveTime = 0L

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        if (Constants.sessionExpiredDialog) {
            startSessionTimeout()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_container) as NavHostFragment
        mNavController = navHostFragment.findNavController()
        mAppBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.myBidsFragment,
                R.id.passBookFragment,
                R.id.fundsFragment,
                R.id.supportFragment
            ), mBinding.drawerLayout
        )


        setSupportActionBar(mBinding.lytToolbar.toolBar)

        setupActionBarWithNavController(mNavController, mAppBarConfiguration)

        mBinding.navView.setupWithNavController(mNavController)
        mBinding.bottomNav.setupWithNavController(mNavController)


        val bottomMenuView = mBinding.bottomNav.getChildAt(0) as BottomNavigationMenuView

        val view = bottomMenuView.getChildAt(2)

        val itemView = view as BottomNavigationItemView
        val viewCustom = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.button_custom, bottomMenuView, false)
        itemView.addView(viewCustom)
        if (mPref.getShowResultsOnly() == 1 || mPref.getMatkaEnable()) {
            val view0 = bottomMenuView.getChildAt(0)
            val itemView0 = view0 as BottomNavigationItemView
            val view1 = bottomMenuView.getChildAt(1)
            val itemView1 = view1 as BottomNavigationItemView
            val view3 = bottomMenuView.getChildAt(3)
            val itemView3 = view3 as BottomNavigationItemView
            val view4 = bottomMenuView.getChildAt(4)
            val itemView4 = view4 as BottomNavigationItemView
            itemView0.visibility = View.GONE
            itemView1.visibility = View.GONE
            itemView3.visibility = View.GONE
            itemView4.visibility = View.GONE
        } else {
            if (Constants.disablePassBook) {
                val view1 = bottomMenuView.getChildAt(1)
                val itemView1 = view1 as BottomNavigationItemView
                itemView1.setOnClickListener {
                    val telegramIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(mPref.getTelegramLink(Constants.TELEGRAM_LINK))
                        )
                    startActivity(telegramIntent)
                }
            }
        }
        mHomeButton = viewCustom.findViewById(R.id.homeBtn)

        mBinding.mainNav.apply {
            drawerMenuLayoutManager =
                LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            menuItemsRV.layoutManager = drawerMenuLayoutManager
            menuItemsRV.adapter = mNewCustomDrawerAdapter

            drawerItemList.addAll(
                getDrawerPanelData(
                    mPref.getEarningSystem(),
                    mPref.getShowResultsOnly(),
                    mPref.getMatkaEnable()
                )
            )
            mNewCustomDrawerAdapter.notifyDataSetChanged()
        }

        mNavController.addOnDestinationChangedListener(this)
        mHomeButton.setOnClickListener(this)
        if (!Constants.ENABLE_LIVE_CHAT) mBinding.chatButton.visibility = View.GONE
        mBinding.chatButton.setOnClickListener {
            val intent = Intent(it.context, WebChatActivity::class.java)
            startActivity(intent)
        }

        mBinding.apply {
            mainNav.tvUserName.text = mPref.getName(NAME).toString().capitalize()
            mainNav.tvPhone.text = mPref.getPhone(PHONE)
            lytToolbar.imgVallet.setOnClickListener {
                mNavController.navigate(R.id.addFundsFragment)
            }
            lytToolbar.imgRefresh.setOnClickListener {
                Toast.makeText(
                    this@MainActivity, "Refreshing... Please wait...",
                    Toast.LENGTH_SHORT
                ).show()
                payUrlViewModel.getAppData()
            }
            if (mPref.getShowResultsOnly() == 1 || mPref.getMatkaEnable()) {
                lytToolbar.imgVallet.visibility = View.GONE
//                lytToolbar.imgNotification.visibility = View.GONE
                lytToolbar.tvBalance.visibility = View.GONE
            }
        }

        appUpdateDataObserver()
        observer()
        // Android code to subscribe a user to a topic
        if (!mPref.getIsSubscribedToTopic()) {
            FirebaseMessaging.getInstance().subscribeToTopic("daily_messaging_all_users")
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        mPref.setIsSubscribedToTopic(true)
                    }
                }
        }
    }

    private fun observer() {
        mSharedViewModels.mBalance?.observe(this) {
            mBinding.lytToolbar.tvBalance.text = it
        }
    }

    private fun appUpdateDataObserver() {
        payUrlViewModel.mGetAppDataResponse.observe(this) { response ->
            when (response) {
                is ApiState.Success -> {
                    this.dismissDialog()
                    if (response.data?.error != null) {
                        if (response.data.error.not()) {
                            if (response.data.response?.appData?.version != null) {
                                response.data.response.appData.let { data ->
                                    data.let {
                                        mPref.setBalance(response.data.response.user?.balance)
                                        mSharedViewModels.setBalance(response.data.response.user?.balance.toString())
//                                        Toast.makeText(this, response.data.response.user?.balance.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    this.dismissDialog()
                }

                is ApiState.Loading -> {
                    this.showProgressDialog()
                }
            }
        }
    }

    @SuppressLint("AppCompatMethod")
    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        mBinding.lytToolbar.tvBalance.text = mPref.getBalance(BALANCE).toString()

        if (destination.id == R.id.homeFragment || destination.id == R.id.logoutFragment || destination.id == R.id.bidClosedDialogFragment) {
            toggleOrientation()
            mBinding.lytToolbar.toolBar.visibility = View.VISIBLE
            mBinding.bottomNav.visibility = View.VISIBLE
        } else {
            mBinding.lytToolbar.toolBar.visibility = View.GONE
            mBinding.bottomNav.visibility = View.GONE
        }

        if (destination.id == R.id.passBookFragment) {
            toggleOrientation()
        }

        if (destination.id == R.id.starLineFragment) {
            mBinding.lytToolbar.toolBar.visibility = View.GONE
        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleOrientation() {
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mBinding.apply {

            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            when (item.itemId) {

                R.id.supportFragment -> {
                    if (mPref.getTelegramEnable(Constants.TELEGRAM_ENABLE) == 1 && mPref.getWhatsAppEnable(
                            Constants.WHATSAPP_ENABLE
                        ) == 1
                    ) {
                        val url =
                            "https://api.whatsapp.com/send?phone=${mPref.getSupportNumber(Constants.SUPPORT_NUMBER)}"
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
                    } else {
                        val url =
                            "https://api.whatsapp.com/send?phone=${mPref.getSupportNumber(Constants.SUPPORT_NUMBER)}"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    }
                }

//                R.id.settingsFragment -> mNavController.navigate(R.id.settingsFragment)
//                R.id.aboutFragment -> mNavController.navigate(R.id.aboutFragment)
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp(mAppBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.homeBtn -> {
                if (mNavController.currentDestination?.id != R.id.homeFragment) {
                    mNavController.navigate(R.id.homeFragment)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.lytToolbar.tvBalance.text = mPref.getBalance(BALANCE).toString()
        if (Constants.sessionExpiredDialog && lastActiveTime > 0L) {
            if (System.currentTimeMillis() - lastActiveTime > 5 * 60 * 1000L) {
                isSessionTimeOut = true
                lastActiveTime = 0L
                mPref.setSessionOutStatus(isSessionOut = true)
                if (!isFinishing && !isDestroyed) {
                    SessionOutDialogFragment.newInstance { isSessionTimeOut = false }
                        .showAllowingStateLoss(supportFragmentManager, "sessionOut")
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        if (mPref.getShowResultsOnly() == 1 || mPref.getMatkaEnable()) {
            when (position) {
                0 -> {
                    if (mNavController.currentDestination?.id != R.id.homeFragment) {
                        mNavController.navigate(R.id.homeFragment)
                    }
                }

                1 -> {
                    mNavController.navigate(R.id.logoutFragment)
                }
            }
            mBinding.drawerLayout.closeDrawer()
        } else {
            when (position) {

                0 -> {
                    if (mNavController.currentDestination?.id != R.id.homeFragment) {
                        mNavController.navigate(R.id.homeFragment)
                    }
                }

                1 -> {
                    mNavController.navigate(R.id.myBidsFragment)
                }

                2 -> {
                    if (Constants.disablePassBook) {
                        val telegramIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(mPref.getTelegramLink(Constants.TELEGRAM_LINK))
                            )
                        startActivity(telegramIntent)
                    } else {
                        mNavController.navigate(R.id.passBookFragment)
                    }
                }

                3 -> {
                    GiftDialogFragment.newInstance {}.show(supportFragmentManager, "gift")
                }

                4 -> {
                    val url =
                        "https://api.whatsapp.com/send?phone=${mPref.getSupportNumber(Constants.SUPPORT_NUMBER)}"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                    // mNavController.navigate(R.id.supportFragment)
                }

                5 -> {
                    mNavController.navigate(R.id.fundsFragment)
                }

                6 -> {
                    mNavController.navigate(R.id.withDrawInformationFragment)
                }

                7 -> {
                    mNavController.navigate(R.id.gamesRatesFragment)
                }

                8 -> {

                    mNavController.navigate(R.id.chartsFragment)
                }

                9 -> {
                    mNavController.navigate(R.id.settingsFragment)
                }

                10 -> {
                    if (mPref.getEarningSystem()) {
                        mNavController.navigate(R.id.inviteAndEarnFragment)
                    } else {
                        shareData(mPref.getOwnCode(Constants.OWN_CODE).toString())
                    }

                }

                11 -> {
                    if (mPref.getEarningSystem()) {
                        shareData(mPref.getOwnCode(Constants.OWN_CODE).toString())
                    } else {
                        mNavController.navigate(R.id.logoutFragment)
                    }
                }

                12 -> {
                    mNavController.navigate(R.id.logoutFragment)
//                mPref.setIsUserLogin(false)
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
                }
            }
            mBinding.drawerLayout.closeDrawer()
        }

    }

    override fun onBackPressed() {
        when (mNavController.currentDestination?.id) {
            R.id.homeFragment -> {
                if (!this::mQuitDialogFragment.isInitialized) {
                    mQuitDialogFragment = QuitDialogFragment()
                }
                if (mQuitDialogFragment.isVisible) {
                    mQuitDialogFragment.dismiss()
                }
                mQuitDialogFragment.show(supportFragmentManager, "logout")
            }

            R.id.addFundWebViewFragment -> {
                if (!this::quitPaymentDialog.isInitialized) {
                    quitPaymentDialog = QuitPaymentDialog()
                }
                if (quitPaymentDialog.isVisible) {
                    quitPaymentDialog.dismiss()
                }
                quitPaymentDialog.show(supportFragmentManager, "logout")
            }

            else -> {
                mNavController.popBackStack()
            }
        }
    }

    fun setBottomNavIcon(icon: Int) {
        if (mPref.getShowResultsOnly() == 0 && !mPref.getMatkaEnable()) {
            val menu: Menu = mBinding.bottomNav.menu
            menu.findItem(R.id.supportFragment).setIcon(icon)
        }
    }

    private fun startSessionTimeout() {
        sessionJob?.cancel()
        lastActiveTime = System.currentTimeMillis()
        sessionJob = lifecycleScope.launch {
            delay(5 * 60 * 1000L)
            isSessionTimeOut = true
            lastActiveTime = 0L
            mPref.setSessionOutStatus(isSessionOut = true)
            if (!isFinishing && !isDestroyed) {
                SessionOutDialogFragment.newInstance { isSessionTimeOut = false }
                    .showAllowingStateLoss(supportFragmentManager, "sessionOut")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionJob?.cancel()
    }
}