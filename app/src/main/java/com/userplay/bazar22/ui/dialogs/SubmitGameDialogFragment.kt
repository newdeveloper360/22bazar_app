package com.userplay.bazar22.ui.dialogs

import android.R.attr.text
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.device.PrinterManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
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
import com.userplay.bazar22.printer.BillItem
import com.userplay.bazar22.printer.BillPrintActivity.Companion.newIntent
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.toString

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
    private var isAlreadyPrinting: Boolean = false
    private var printerManager: PrinterManager? = null
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
            tvSerialNumber.text = "S.No. ${mPref.getSerialNumber()}"
            when (mArgs.from) {
                DESAWAR_MARKET, STARLINE_MARKET -> {
                    tvType.visibility = View.GONE
                }

                GENERAL_MARKET -> {
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

                                if (Constants.showPrintViewDebug) {
                                    //printDialogView()
                                    startPrintActivity()
                                } else {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", response.data.message.toString())
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                }

                            } else {
                                mSharedViewModels.setBalance(response.data.response?.balanceLeft.toString())
                                mPref.setBalance(response.data.response?.balanceLeft)
                                when (mArgs.from) {

                                    GENERAL_MARKET -> {
                                      /*  val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", GENERAL_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")*/
                                       // printDialogView()
                                        startPrintActivity()
                                    }

                                    STARLINE_MARKET -> {

                                      /*  val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", STARLINE_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")*/
                                        //printDialogView()
                                        startPrintActivity()
                                    }

                                    DESAWAR_MARKET -> {

                                       /* val dialog = BidSuccessDialogFragment()
                                        val bundle = Bundle()
                                        bundle.putString("from", DESAWAR_MARKET)
                                        dialog.arguments = bundle
                                        dialog.show(childFragmentManager, "OpenGame")*/
                                        //printDialogView()
                                        startPrintActivity()
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
    fun startPrintActivity() {
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var formattedDate=fmt.format(Date())
        val items=  mArgs.sendBody.games.map {
            val formattedNumber = it.number?.let { number ->
                when {
                    it.pattiType.contains("HSB") == true ->
                        if (number.length > 3) number.substring(0, 3) + "x" + number.substring(3) else number
                    it.pattiType.contains("HSA") == true ->
                        if (number.length > 1) number.substring(0, 1) + "x" + number.substring(1) else number
                    else ->
                        if (number.length > 3) number.substring(0, 3) + "x" + number.substring(3) else number
                }
            }
            BillItem(formattedNumber + it.pattiType, openClose = it.session.toString(), amount = it.amount?:0)
        }

        startActivity(
            newIntent(
                context = requireContext(),
                heading = getString(com.userplay.bazar22.R.string.app_name),
                headingSize = 26f,
                subHeading =  mPref.getGameSubName(),
                subHeadingSize = 20f,
                title = mPref.getName(Constants.NAME).toString(),
                subTitle = formattedDate?:currentDate(),
                srNumber = "S.No. ${mPref.getSerialNumber()}",
                titleSize = 20f,
                itemFontSize = 20f,
                totalLabel = "TOTAL",
                items = ArrayList(items),
            )
        )
        val todayDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
        val lastSerialDate = mPref.getSerialDate()
        val newSerial: Int = if (lastSerialDate == todayDate) {
            mPref.getSerialNumber() + 1
        } else {
            1
        }

        mPref.setSerialNumber(newSerial)
        mPref.setSerialDate(todayDate)
        dismiss()
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
        dialog?.window?.setLayout(width, WRAP_CONTENT)
    }


    override fun onResume() {
        super.onResume()
        mBinding.tvBalance.text = mPref.getBalance(BALANCE).toString()
        val afterBalance = mPref.getBalance(BALANCE) - mArgs.totalPoints
        mBinding.afterBalance.text = afterBalance.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            printerManager?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            printerManager = null
            _binding = null
        }

    }

    private fun getPrinterManager(): PrinterManager {
        if (printerManager == null) {

            val hasPrinter = try {
                Class.forName("android.device.PrinterManager")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            if (hasPrinter) {
                printerManager = PrinterManager()
                printerManager?.open()
            } else {
                Log.w("Printer", "Printer hardware not available on this device")
            }
        }
        return printerManager!!
    }

    private fun printBitmap(bitmap: Bitmap) {
        try {
            val printer = getPrinterManager()

            // Get Bitmap from drawable

            // Check printer status
            val status = printer.status
            if (status == 0) { // PRNSTS_OK
                printer.setupPage(384, -1) // Paper width 384px
                printer.drawBitmap(bitmap, 30, 0)
                printer.printPage(0)
                printer.paperFeed(16) // Feed paper
                Toast.makeText(requireContext(), "Printed Successfully", Toast.LENGTH_SHORT).show()
                isAlreadyPrinting = false
                shareToPrintingApps(bitmap)
            } else {
                Toast.makeText(requireContext(), "Printer Error: $status", Toast.LENGTH_SHORT)
                    .show()
                isAlreadyPrinting = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isAlreadyPrinting = false
            Toast.makeText(requireContext(), "Print failed: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun printDialogView() {
        if (isAlreadyPrinting) return
        isAlreadyPrinting = true

        val inflater = LayoutInflater.from(requireContext())


        // Container for items (acts as a full list)
        val itemsContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
        }

        // Inflate and bind every item in adapter
        val adapter = mGameSubmitDialogAdapter
        for (i in 0 until adapter.itemCount) {
            val holder = adapter.onCreateViewHolder(itemsContainer, adapter.getItemViewType(i))
            adapter.onBindViewHolder(holder, i)

            val itemView = holder.itemView
            // Measure item properly
            itemView.measure(
                View.MeasureSpec.makeMeasureSpec(
                    Resources.getSystem().displayMetrics.widthPixels,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            itemView.layout(0, 0, itemView.measuredWidth, itemView.measuredHeight)
            itemsContainer.addView(itemView)
        }

        // Add this new list to a clean scroll container to avoid overlap
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
            addView(itemsContainer)
        }

        // Main container (full print layout)
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(20, 20, 20, 20)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
        }

        // Header
        val titleView = TextView(requireContext()).apply {
            text = getString(R.string.app_name)
            textSize = 26f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }

        // game name
        val gameView = TextView(requireContext()).apply {
            text = mPref.getGameSubName()
            textSize = 22f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }

        val userView = TextView(requireContext()).apply {
            text = mPref.getName(Constants.NAME).toString()
            textSize = 26f
            gravity = Gravity.CENTER
        }

        val timeView = TextView(requireContext()).apply {
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            text = fmt.format(Date())
            textSize = 26f
            gravity = Gravity.CENTER
        }


        // 1️⃣ Date TextView
        val tvDate = TextView(context).apply {
            text = mBinding.tvDate.text
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 10)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
            textSize = 26f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
        }

        // 2️⃣ Serial Number
        val tvSerialNumber = TextView(context).apply {
            text = "S.No. ${mPref.getSerialNumber()}"
            gravity = Gravity.END
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            setPadding(5, 5, 5, 5)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
        }

        // 3️⃣ Headings Row
        val llHeadings = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            ).apply {
                setMargins(16, 6, 16, 0)
            }
        }

        val headings = listOf("Digit", "Points")
        headings.forEach { title ->
            llHeadings.addView(TextView(context).apply {
                text = title
                textSize = 26f
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    WRAP_CONTENT,
                    1f
                )
            })
        }

        // 4️⃣ Totals Layout
        val llTotals = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CLIP_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 5)
            }
        }

        // Left column: Total Bids
        val llLeft = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CLIP_VERTICAL
            setBackgroundColor("#F2F2F2".toColorInt())
            layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setPadding(5, 8, 5, 5)
                }
        }

        val tvLabelBids = TextView(context).apply {
            text = "Total Bids"
            setTextColor(Color.BLACK)
            textSize = 22f
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
        }

        val tvTotalBids = TextView(context).apply {
            text = mArgs.totalBids.toString()
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            textSize = 26f
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
        }

        llLeft.addView(tvLabelBids)
        llLeft.addView(tvTotalBids)

        // Right column: Total Points
        val llRight = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor("#F2F2F2".toColorInt())
            layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setPadding(5, 8, 5, 5)
                }
        }

        val tvLabelPoints = TextView(context).apply {
            text = "Total Amount"
            setTextColor(Color.BLACK)
            textSize = 22f
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
        }

        val tvTotalPoints = TextView(context).apply {
            text = mArgs.totalPoints.toString()
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            textSize = 26f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
        }



        val emptyView = TextView(context).apply {
            text = ""
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize=30f
            setPadding(10, 5, 5, 5)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            ).apply {
                setPadding(5, 40, 5, 5)
            }
        }
        val emptyView2 = TextView(context).apply {
            text = "------------------------"
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize=30f
            setPadding(10, 20, 5, 5)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
            )
        }

        llRight.addView(tvLabelPoints)
        llRight.addView(tvTotalPoints)

        llTotals.addView(llLeft)
        llTotals.addView(llRight)


        container.addView(tvDate)
        container.addView(tvSerialNumber)
        container.addView(titleView)
        container.addView(gameView)
        container.addView(userView)
        container.addView(timeView)
        container.addView(llHeadings)
        container.addView(scrollView)
        container.addView(llTotals)
        container.addView(emptyView)
        container.addView(emptyView2)


        // Measure the entire layout
        val displayWidth = Resources.getSystem().displayMetrics.widthPixels
        container.measure(
            View.MeasureSpec.makeMeasureSpec(displayWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        container.layout(0, 0, container.measuredWidth, container.measuredHeight)

        // Convert to bitmap
        val bitmap = Bitmap.createBitmap(
            container.measuredWidth,
            container.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        container.draw(canvas)

        // Show the bitmap in a preview dialog (for testing without printer)
        if (Constants.showPrintViewDebug) {
            val uri = saveBitmapToCache(requireContext(), bitmap)
            if (uri != null) {
                shareBitmap(requireContext(), uri)
                isAlreadyPrinting = false
            } else {
                Log.e("getUrl", "null")
            }
        } else {
            printBitmap(bitmap)
        }


        val todayDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
        val lastSerialDate = mPref.getSerialDate()
        val newSerial: Int = if (lastSerialDate == todayDate) {
            mPref.getSerialNumber() + 1
        } else {
            1
        }

        mPref.setSerialNumber(newSerial)
        mPref.setSerialDate(todayDate)
        mBinding.tvSerialNumber.text = "S.No. $newSerial"

    }






    private fun increaseTextSize(view: View, scaleFactor: Float = 1.2f) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                increaseTextSize(view.getChildAt(i), scaleFactor)
            }
        } else if (view is TextView) {
            view.textSize =
                view.textSize / view.resources.displayMetrics.scaledDensity * scaleFactor
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
            val appName =
                resolveInfo.loadLabel(requireContext().packageManager).toString().lowercase()

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
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                targetedIntents.drop(1).toTypedArray()
            )
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


fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val cacheDir = context.cacheDir
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, "receipt_${System.currentTimeMillis()}.png").apply {
            outputStream().use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Failed to compress bitmap")
                }
            }
        }
        if (!file.exists()) {
            throw IOException("File creation failed")
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Ensure this matches your manifest
            file
        )
        Log.d("FilePath", "File created at: ${file.absolutePath}")
        Log.d("FileUri", "Uri: $uri")
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun shareBitmap(context: Context, uri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(shareIntent, "Share Receipt").apply {
        // Prevent multiple chooser activities
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to share receipt: ${e.message}", Toast.LENGTH_LONG).show()
    }
}