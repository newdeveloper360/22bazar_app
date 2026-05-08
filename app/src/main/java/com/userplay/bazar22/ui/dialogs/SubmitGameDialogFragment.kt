package com.userplay.bazar22.ui.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSubmitGameDialogBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork.Companion.isNetworkConnected
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.fragments.home.adapters.GameSubmitDialogAdapter
import com.userplay.bazar22.ui.viewmodels.GameTypeViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.*
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.DESAWAR_MARKET
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SubmitGameDialogFragment : DialogFragment(R.layout.fragment_submit_game_dialog),
    View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSubmitGameDialogBinding? = null
    private val mBinding get() = _binding!!
    private val mGameTypeViewModel: GameTypeViewModel by viewModels()
    private val mArgs: SubmitGameDialogFragmentArgs by navArgs()
    private val mSharedViewModels: SharedViewModels by activityViewModels()

    private val mGameSubmitDialogAdapter: GameSubmitDialogAdapter by lazy {
        GameSubmitDialogAdapter(
            mArgs.sendBody.games,
            mArgs.from,
            mArgs.gameType
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmitGameDialogBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBinding.apply {
            tvTotalBids.text = mArgs.totalBids.toString()
            tvTotalPoints.text = mArgs.totalPoints.toString()
            submit.setOnClickListener(this@SubmitGameDialogFragment)
            cancel.setOnClickListener(this@SubmitGameDialogFragment)
            tvDate.text = mArgs.gameName + " - " + currentDate()

            when (mArgs.from) {
                DESAWAR_MARKET, STARLINE_MARKET -> {
                    tvType.visibility = View.GONE
                }
                GENERAL_MARKET ->{
                    when (mArgs.gameType) {
                        true -> {
                            tvType.visibility = View.VISIBLE
                        }
                        false -> {
                            tvType.visibility = View.GONE
                        }
                    }
                }
            }
        }

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = mGameSubmitDialogAdapter
        }
    }

    private fun observer() {
        activity?.let {
            mGameTypeViewModel.mGameSubmitResponse.observe(it) { response ->

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
                                mSharedViewModels.setBalance(response.data.response?.balanceLeft.toString())
                                mPref.setBalance(response.data.response?.balanceLeft)
                                when (mArgs.from) {

                                    GENERAL_MARKET -> {
                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", GENERAL_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")
                                    }

                                    STARLINE_MARKET -> {

                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", STARLINE_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")
                                    }

                                    DESAWAR_MARKET -> {

                                        val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", DESAWAR_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")

                                    }

                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                    }
                }
            }

            mSharedViewModels.mBalance?.observe(viewLifecycleOwner) {
                mBinding.tvBalance.text = it
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.submit -> {
                        if (isNetworkConnected) {
                            it.hideKeyboard()
                            mGameTypeViewModel.gameSubmit(mArgs.sendBody)
                        } else {
                            val dialog = InternetErrorDialogFragment()
                            dialog.show(childFragmentManager, "internet")
                            // it.showToast(resources.getString(R.string.check_your_internet))
                        }
                    }
                    R.id.cancel -> {
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(BALANCE).toString()
        val afterBalance = mPref.getBalance(BALANCE) - mArgs.totalPoints
        mBinding.afterBalance.text = afterBalance.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun printDialogView() {
        // 1) inflate a fresh copy of the dialog layout
        val printBinding = FragmentSubmitGameDialogBinding.inflate(layoutInflater).apply {
            // copy over the data you want printed
            tvTotalBids.text = mArgs.totalBids.toString()
            tvTotalPoints.text = mArgs.totalPoints.toString()
            tvDate.text = mBinding.tvDate.text
            tvBalance.text = mBinding.tvBalance.text
            afterBalance.text = mBinding.afterBalance.text

            // set adapter so RecyclerView will measure its children
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = mGameSubmitDialogAdapter

            // hide the on‑screen action buttons
            submit.visibility = View.GONE
            cancel.visibility = View.GONE
            lyWalletDetails.visibility = View.GONE
        }

        // 2) wrap it in a container that adds margins + header + timestamp
//        val marginPx = resources.getDimensionPixelSize(R.dimen.print_margin)
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
//            setPadding(marginPx, marginPx, marginPx, marginPx)
        }

        // app name at top
        val titleView = TextView(requireContext()).apply {
            text = getString(R.string.app_name)
            textSize = 20f
            gravity = Gravity.CENTER
        }

        // current timestamp below
        val timeView = TextView(requireContext()).apply {
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            text = fmt.format(Date())
            textSize = 14f
            gravity = Gravity.CENTER
        }

        // current timestamp below
        val userView = TextView(requireContext()).apply {
            text = mPref.getName(Constants.NAME).toString()
            textSize = 18f
            gravity = Gravity.CENTER
        }

        // assemble
        container.addView(titleView)
        container.addView(userView)
        container.addView(timeView)
        container.addView(printBinding.root)

        // Add this:
        increaseTextSize(container)

        val bitmap = getBitmapFromView(container)
        shareToPrintingApps(bitmap)
    }

    private fun increaseTextSize(view: View, scaleFactor: Float = 1.2f) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                increaseTextSize(view.getChildAt(i), scaleFactor)
            }
        } else if (view is TextView) {
            view.textSize = view.textSize / view.resources.displayMetrics.scaledDensity * scaleFactor
        }
    }


    private fun shareToPrintingApps(bitmap: Bitmap) {
        val cachePath = File(requireContext().cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "submission_slip.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Get all apps that can handle this share intent
        val resInfoList = requireContext().packageManager.queryIntentActivities(intent, 0)

        // Filter to only apps likely to be printing apps
        val targetedIntents = resInfoList.mapNotNull { resolveInfo ->
            val pkgName = resolveInfo.activityInfo.packageName
            val appName = resolveInfo.loadLabel(requireContext().packageManager).toString().lowercase()

            if ("print" in pkgName.lowercase() || "print" in appName) {
                Intent(intent).apply {
                    setPackage(pkgName)
                    setClassName(pkgName, resolveInfo.activityInfo.name)
                }
            } else null
        }

        // If no printing apps found, show full chooser as fallback
        if (targetedIntents.isEmpty()) {
            startActivity(Intent.createChooser(intent, "Share to Printer"))
        } else {
            val chooserIntent = Intent.createChooser(targetedIntents[0], "Print Submission Slip")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.drop(1).toTypedArray())
            startActivity(chooserIntent)
        }
    }


    private fun getBitmapFromView(view: View): Bitmap {
        // compute printable width (screen width minus left+right margins)
        val displayMetrics = Resources.getSystem().displayMetrics
        val printableWidth = displayMetrics.widthPixels

        // measure with EXACTLY printable width, wrap‐content height
        val widthSpec = View.MeasureSpec.makeMeasureSpec(printableWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)

        // layout at (0,0) with measured dimensions
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // create bitmap and draw
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    //    private fun shareBitmap(bitmap: Bitmap) {
//        try {
//            // Save bitmap to cache
//            val file = File(requireContext().cacheDir, "print_slip.png")
//            FileOutputStream(file).use { out ->
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//            }
//
//            // Create share intent
//            val uri = FileProvider.getUriForFile(
//                requireContext(),
//                "${requireContext().packageName}.provider",
//                file
//            )
//
//            val shareIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_STREAM, uri)
//                type = "image/png"
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            startActivity(Intent.createChooser(shareIntent, "Share Slip to Print"))
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Error sharing slip: ${e.message}", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

//    private fun printDialogView() {
//        // 1) inflate a fresh copy of the dialog layout
//        val printBinding = FragmentSubmitGameDialogBinding.inflate(layoutInflater).apply {
//            // copy over the data you want printed
//            tvTotalBids.text = mArgs.totalBids.toString()
//            tvTotalPoints.text = mArgs.totalPoints.toString()
//            tvDate.text = mBinding.tvDate.text
//            tvBalance.text = mBinding.tvBalance.text
//            afterBalance.text = mBinding.afterBalance.text
//
//            // set adapter so RecyclerView will measure its children
//            recyclerview.layoutManager = LinearLayoutManager(requireContext())
//            recyclerview.adapter = mGameSubmitDialogAdapter
//
//            // hide the on‑screen action buttons
//            submit.visibility = View.GONE
//            cancel.visibility = View.GONE
//            lyWalletDetails.visibility = View.GONE
//        }
//
//        // 2) wrap it in a container that adds margins + header + timestamp
//        val marginPx = resources.getDimensionPixelSize(R.dimen.print_margin)
//        val container = LinearLayout(requireContext()).apply {
//            orientation = LinearLayout.VERTICAL
//            setBackgroundColor(Color.WHITE)
//            setPadding(marginPx, marginPx, marginPx, marginPx)
//        }
//
//        // app name at top
//        val titleView = TextView(requireContext()).apply {
//            text = getString(R.string.app_name)
//            textSize = 20f
//            gravity = Gravity.CENTER
//        }
//
//        // current timestamp below
//        val timeView = TextView(requireContext()).apply {
//            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//            text = fmt.format(Date())
//            textSize = 14f
//            gravity = Gravity.CENTER
//        }
//
//        // current timestamp below
//        val userView = TextView(requireContext()).apply {
//            text = mPref.getName(Constants.NAME).toString()
//            textSize = 18f
//            gravity = Gravity.CENTER
//        }
//
//        // assemble
//        container.addView(titleView)
//        container.addView(userView)
//        container.addView(timeView)
//        container.addView(printBinding.root)
//
//        // 3) capture to bitmap and send to PrintHelper
//        val bitmap = getBitmapFromView(container, marginPx)
//        PrintHelper(requireContext()).apply {
//            scaleMode = PrintHelper.SCALE_MODE_FIT
//        }.printBitmap("Game Submission Slip", bitmap)
//    }


}