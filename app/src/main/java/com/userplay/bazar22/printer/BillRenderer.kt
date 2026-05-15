package com.userplay.bazar22.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

/**
 * Builds the thermal-bill UI as a [LinearLayout] and also exports it as a [Bitmap].
 *
 * Key rule: text sizes are FIXED in sp — they never scale with item count.
 * Only the container grows vertically.
 */
object BillRenderer {

    fun buildView(
        context: Context,
        config: BillConfig,
        items: List<BillItem>,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            val h = dp(context, 8)
            val v = dp(context, 10)
            setPadding(h, v, h, v)
        }

        root.addView(textView(context, config.heading))
        if(config.label.text.isNotEmpty()){
            root.addView(textView(context, config.label))
        }
        root.addView(textView(context, config.srNo))
        root.addView(textView(context, config.subHeading))
        root.addView(textView(context, config.title))
        root.addView(textView(context, config.subTitle))
        root.addView(dividerView(context, config))
        root.addView(buildTable(context, config, items))
        root.addView(dividerView(context, config))
        root.addView(buildTotalRow(context, config, items))
        root.addView(dividerView(context, config))

        return root
    }

    /**
     * Renders [view] to a [Bitmap] for printing.
     * The view is measured at [widthPx] (thermal paper pixel width) then drawn.
     */
    fun toBitmap(view: View, widthPx: Int): Bitmap {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bmp = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888,
        )
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bmp
    }

    private fun textView(context: Context, cfg: TextConfig): TextView {
        return TextView(context).apply {
            text = cfg.text
            textSize = cfg.fontSize
            textAlignment = cfg.alignment.toTextAlignment()
            gravity = cfg.alignment.toLayoutGravity()
            setTextColor(Color.BLACK)
            typeface = if (cfg.isBold) Typeface.DEFAULT_BOLD else Typeface.MONOSPACE
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = dp(context, 2)
            }
        }
    }

    private fun dividerView(context: Context, config: BillConfig): View {
        val dashLen = config.thermalCharWidth.coerceIn(8, 48)
        return when (config.divider.style) {
            DividerStyle.DASHED -> {
                val line = config.divider.char.toString().repeat(dashLen)
                TextView(context).apply {
                    text = line
                    textSize = 10f
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    gravity = Gravity.CENTER_HORIZONTAL
                    setTextColor(Color.BLACK)
                    typeface = Typeface.MONOSPACE
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        topMargin = dp(context, 2)
                        bottomMargin = dp(context, 2)
                    }
                }
            }
            DividerStyle.SOLID -> {
                View(context).apply {
                    setBackgroundColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dp(context, 1)).apply {
                        topMargin = dp(context, 4)
                        bottomMargin = dp(context, 4)
                    }
                }
            }
        }
    }

    private fun buildTable(
        context: Context,
        config: BillConfig,
        items: List<BillItem>,
    ): TableLayout {
        val table = TableLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            isStretchAllColumns = true
        }

        table.addView(buildRow(context, config, isHeader = true, item = null))

        items.forEach { item ->
            table.addView(buildRow(context, config, isHeader = false, item = item))
        }

        return table
    }

    private fun buildRow(
        context: Context,
        config: BillConfig,
        isHeader: Boolean,
        item: BillItem?,
    ): TableRow {
        val row = TableRow(context).apply {
            layoutParams = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }

        val colCount = config.columns.size
        val dataCells: List<String>? = if (isHeader || item == null) {
            null
        } else {
            when {
                item.customRow != null -> {
                    require(item.customRow.size == colCount) {
                        "BillItem.customRow size (${item.customRow.size}) must match column count ($colCount)"
                    }
                    item.customRow
                }
                else -> {
                    val base = listOf(
                        item.name,
                        item.amount.toString(),
                    )
                    base.take(colCount)
                }
            }
        }

        config.columns.forEachIndexed { index, col ->
            val cellText = when {
                isHeader -> col.header
                dataCells != null -> dataCells[index]
                else -> ""
            }

            val cell = TextView(context).apply {
                text = cellText
                textSize = if (isHeader) config.itemHeadingSize else config.itemFontSize
                typeface = if (isHeader) Typeface.DEFAULT_BOLD else Typeface.MONOSPACE
                textAlignment = col.alignment.toTextAlignment()
                gravity = col.alignment.toLayoutGravity()
                setTextColor(Color.BLACK)
                setPadding(dp(context, 2), dp(context, 3), dp(context, 2), dp(context, 3))

                layoutParams = TableRow.LayoutParams(0, WRAP_CONTENT, col.widthWeight)
            }
            row.addView(cell)
        }

        return row
    }

    private fun buildTotalRow(
        context: Context,
        config: BillConfig,
        items: List<BillItem>,
    ): LinearLayout {
        val total = items.sumOf { it.amount }

        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setPadding(0, dp(context, 4), 0, dp(context, 4))

            addView(TextView(context).apply {
                text = config.totalLabel
                text = "Bids:  "+ items.size
                textSize = config.totalFontSize
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            })

            addView(TextView(context).apply {
                text = config.totalLabel+":  "+ total.toString()
                textSize = config.totalFontSize
                typeface = Typeface.DEFAULT_BOLD
                textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                gravity = Gravity.END
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            })
        }
    }

    private fun dp(context: Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()

    private fun formatAmount(amount: Double): String =
        String.format("%.2f", amount)
}
