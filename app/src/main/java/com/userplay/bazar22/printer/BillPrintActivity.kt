package com.userplay.bazar22.printer

import android.R.attr.label
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.number.Precision.currency
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.userplay.bazar22.databinding.ActivityBillPrintBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BillPrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillPrintBinding
    private lateinit var billView: LinearLayout
    private lateinit var billConfig: BillConfig
    private lateinit var billItems: List<BillItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (cfg, items) = resolvePayload()
        billConfig = cfg
        billItems = items

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        billView = BillRenderer.buildView(this, billConfig, billItems)
        binding.billPreviewContainer.addView(billView)

        binding.btnPrint.setOnClickListener { printBill() }
        binding.btnShare.setOnClickListener { shareBill() }
    }

    private fun resolvePayload(): Pair<BillConfig, List<BillItem>> {
        if (intent.getBooleanExtra(EXTRA_USE_SESSION, false)) {
            val payload = BillPrintSession.pending
            BillPrintSession.pending = null
            if (payload != null) {
                return payload.config to payload.items
            }
        }
        return buildConfigFromIntent() to readItemsFromIntent()
    }

    private fun buildConfigFromIntent(): BillConfig {
        return BillConfig(
            heading = TextConfig(
                text = intent.getStringExtra(EXTRA_HEADING) ?: "MY STORE",
                fontSize = intent.getFloatExtra(EXTRA_HEADING_SIZE, 18f),
                alignment = BillAlignment.CENTER,
                isBold = true,
            ),
            subHeading = TextConfig(
                text = intent.getStringExtra(EXTRA_SUB_HEADING) ?: "Inventory Receipt",
                fontSize = intent.getFloatExtra(EXTRA_SUB_HEADING_SIZE, 16f),
                alignment = BillAlignment.CENTER,
            ),
            title = TextConfig(
                text = intent.getStringExtra(EXTRA_TITLE) ?: "Bill No: #0001  |  Date: --",
                fontSize = intent.getFloatExtra(EXTRA_TITLE_SIZE, 16f),
                alignment = BillAlignment.CENTER,
            ),
            subTitle = TextConfig(
                text = intent.getStringExtra(EXTRA_SUB_TITLE) ?: "Date: --",
                fontSize = intent.getFloatExtra(EXTRA_SUB_TITLE_SIZE, 16f),
                alignment = BillAlignment.CENTER,
            ),
            label = TextConfig(
                text = intent.getStringExtra(EXTRA_LABEL) ?: "Date: --",
                fontSize = intent.getFloatExtra(EXTRA_LABEL_SIZE, 14f),
                alignment = BillAlignment.CENTER,
            ),
            divider = DividerConfig(
                style = DividerStyle.SOLID,
                char = '-',
                charCount = intent.getIntExtra(EXTRA_THERMAL_CHAR_WIDTH, 32),
            ),
            srNo = TextConfig(
                text =  intent.getStringExtra(EXTRA_SR_NO)?:"",
                fontSize = 14f,
                alignment = BillAlignment.RIGHT,
                isBold = true
            ),
            columns = listOf(
                ColumnDef("Digits", 0.20f, BillAlignment.LEFT),
                ColumnDef("Points", 0.48f, BillAlignment.RIGHT)
            ),

                /* if(intent.getBooleanExtra(EXTRA_IS_GAME_TYPE,false)){listOf(
                ColumnDef("Digits", 0.12f, BillAlignment.LEFT),
                ColumnDef("Points", 0.48f, BillAlignment.RIGHT)
            )}else{
                listOf(
                    ColumnDef("Digits", 0.12f, BillAlignment.LEFT),
                    ColumnDef("Type", 0.12f, BillAlignment.LEFT),
                    ColumnDef("Points", 0.48f, BillAlignment.RIGHT))
            },*/
            itemFontSize = intent.getFloatExtra(EXTRA_ITEM_FONT_SIZE, 10f),
            totalLabel = intent.getStringExtra(EXTRA_TOTAL_LABEL) ?: "TOTAL",
            totalFontSize = 18f,
            currency = intent.getStringExtra(EXTRA_CURRENCY) ?: "",
            thermalCharWidth = intent.getIntExtra(EXTRA_THERMAL_CHAR_WIDTH, 55),
        )
    }

    @Suppress("DEPRECATION")
    private fun readItemsFromIntent(): List<BillItem> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_ITEMS, BillItem::class.java)
        } else {
            intent.getParcelableArrayListExtra(EXTRA_ITEMS)
        } ?: sampleItems()
    }

    private fun printBill() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(
                PrintAttributes.MediaSize("THERMAL_58MM", "Thermal 58mm", 2283, 11000)
            )
            .setResolution(PrintAttributes.Resolution("res1", "203dpi", 203, 203))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(
            billConfig.heading.text,
            BillPrintDocumentAdapter(this, billConfig, billItems),
            printAttributes,
        )
    }

    private fun shareBill() {
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                BillRenderer.toBitmap(billView, widthPx = 464)
            }
            val file = withContext(Dispatchers.IO) {
                saveBitmapToFile(bitmap)
            }
            val uri = FileProvider.getUriForFile(
                this@BillPrintActivity,
                "$packageName.provider",
                file,
            )
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            withContext(Dispatchers.Main) {
                try {
                    startActivity(Intent.createChooser(sendIntent, "Share Bill"))
                } catch (e: Exception) {
                    Toast.makeText(
                        this@BillPrintActivity,
                        getString(com.userplay.bazar22.R.string.places_try_again),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val dir = File(cacheDir, "cache").apply { mkdirs() }
        val file = File(dir, "bill_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    companion object {

        private const val EXTRA_USE_SESSION = "extra_use_session"
        private const val EXTRA_HEADING = "extra_heading"
        private const val EXTRA_HEADING_SIZE = "extra_heading_size"
        private const val EXTRA_SUB_HEADING = "extra_sub_heading"
        private const val EXTRA_SUB_HEADING_SIZE = "extra_sub_heading_size"
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_SUB_TITLE = "extra_sub_title"
        private const val EXTRA_LABEL = "extra_label"
        private const val EXTRA_TITLE_SIZE = "extra_title_size"
        private const val EXTRA_SUB_TITLE_SIZE = "extra_sub_title_size"
        private const val EXTRA_LABEL_SIZE = "extra_label_size"
        private const val EXTRA_IS_GAME_TYPE = "is_Game_type"
        private const val EXTRA_ITEM_FONT_SIZE = "extra_item_font_size"
        private const val EXTRA_TOTAL_LABEL = "extra_total_label"
        private const val EXTRA_CURRENCY = "extra_currency"
        private const val EXTRA_THERMAL_CHAR_WIDTH = "extra_thermal_char_width"
        const val EXTRA_ITEMS = "extra_items"
        const val EXTRA_SR_NO = "sr_number"

        /**
         * Opens the print screen using a full [BillConfig] (e.g. matka slip columns).
         * Thread-safe for a single immediate [Context.startActivity] after assignment.
         */
        fun newIntentForConfig(
            context: Context,
            config: BillConfig,
            items: ArrayList<BillItem>,
        ): Intent {
            BillPrintSession.pending = BillPrintSession.Payload(config, items)
            return Intent(context, BillPrintActivity::class.java).apply {
                putExtra(EXTRA_USE_SESSION, true)
            }
        }

        fun Activity.startPrintActivity() {
            val items = arrayListOf(
                BillItem("01", amount = 1500),
                BillItem("02", amount = 450),
                BillItem("03", amount = 800),
                BillItem("04", amount = 1200),
                BillItem("01", amount = 1500),
                BillItem("02", amount = 450),
                BillItem("03", amount = 800),
                BillItem("04", amount = 1200),
                BillItem("01", amount = 1500),
                BillItem("02", amount = 450),
                BillItem("03", amount = 800),
                BillItem("04", amount = 1200),
                BillItem("01", amount = 1500),
                BillItem("02", amount = 450),
                BillItem("03", amount = 800),
                BillItem("04", amount = 1200),
                BillItem("01", amount = 1500),
                BillItem("02", amount = 450),
                BillItem("03", amount = 800),
                BillItem("04", amount = 1200),
            )
            startActivity(
                newIntent(
                    context = this,
                    heading = getString(com.userplay.bazar22.R.string.app_name),
                    headingSize = 30f,
                    subHeading = "Single Digit",
                    subHeadingSize = 26f,
                    title = "DHANU 2(MH)",
                    subTitle = "13-15-2025",
                    srNumber = "SNo. 105",
                    label = "GAME NAME",
                    titleSize = 20f,
                    itemFontSize = 28f,
                    totalLabel = "TOTAL",
                    currency = "",
                    items = items,
                )
            )
        }

        fun newIntent(
            context: Context,
            heading: String = "",
            headingSize: Float = 30f,
            subHeading: String = "Double Digit",
            subHeadingSize: Float = 26f,
            title: String = "DHANU 2(MH)",
            subTitle: String = "",
            label: String = "",
            srNumber: String = "",
            titleSize: Float = 20f,
            subTitleSize: Float = 20f,
            itemFontSize: Float = 28f,
            totalLabel: String = "TOTAL",
            currency: String = "",
            thermalCharWidth: Int = 55,
            isGameTypeShow: Boolean = false,
            items: ArrayList<BillItem> = arrayListOf(),
        ): Intent = Intent(context, BillPrintActivity::class.java).apply {
            putExtra(EXTRA_USE_SESSION, false)
            putExtra(EXTRA_HEADING, heading)
            putExtra(EXTRA_HEADING_SIZE, headingSize)
            putExtra(EXTRA_SUB_HEADING, subHeading)
            putExtra(EXTRA_SUB_HEADING_SIZE, subHeadingSize)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_SUB_TITLE, subTitle)
            putExtra(EXTRA_LABEL, label)
            putExtra(EXTRA_TITLE_SIZE, titleSize)
            putExtra(EXTRA_SUB_TITLE_SIZE, subTitleSize)
            putExtra(EXTRA_ITEM_FONT_SIZE, itemFontSize)
            putExtra(EXTRA_TOTAL_LABEL, totalLabel)
            putExtra(EXTRA_CURRENCY, currency)
            putExtra(EXTRA_SR_NO, srNumber)
            putExtra(EXTRA_THERMAL_CHAR_WIDTH, thermalCharWidth)
            putExtra(EXTRA_IS_GAME_TYPE, isGameTypeShow)
            putParcelableArrayListExtra(EXTRA_ITEMS, items)
        }

        private fun sampleItems() = listOf(
            BillItem("0", amount = 1500),
            BillItem("1", amount = 450),
            BillItem("2", amount = 800),
            BillItem("3", amount = 1200),
            BillItem("4", amount = 600),
        )
    }
}
