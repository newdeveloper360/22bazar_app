package com.userplay.bazar22.printer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.view.View
import java.io.FileOutputStream
import kotlin.math.ceil
import kotlin.math.max

/**
 * Renders the bill across one or more PDF pages by vertical tiling (no shrinking).
 * Content height is measured once; each page shows the next [pageHeightPx] strip.
 */
class BillPrintDocumentAdapter(
    private val context: Context,
    private val config: BillConfig,
    private val items: List<BillItem>,
) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null

    /** Page size in px derived from [PrintAttributes] (matches PDF canvas). */
    private var layoutPageWidthPx: Int = 0
    private var layoutPageHeightPx: Int = 0
    private var layoutPageCount: Int = 1

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?,
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        pdfDocument?.close()
        pdfDocument = null

        val (pw, ph) = measurePageSizeWithTempDocument(context, newAttributes)
        layoutPageWidthPx = pw
        layoutPageHeightPx = ph

        val billView = BillRenderer.buildView(context, config, items)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(pw, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        billView.measure(widthSpec, heightSpec)
        val contentH = billView.measuredHeight.coerceAtLeast(1)

        layoutPageCount = max(1, ceil(contentH.toDouble() / ph).toInt())

        pdfDocument = PrintedPdfDocument(context, newAttributes)

        val info = PrintDocumentInfo.Builder("bill.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(layoutPageCount)
            .build()

        callback.onLayoutFinished(info, oldAttributes != newAttributes)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback,
    ) {
        val document = pdfDocument ?: run {
            callback.onWriteFailed("PDF document not initialised")
            return
        }

        if (cancellationSignal?.isCanceled == true) {
            callback.onWriteCancelled()
            return
        }

        val billView = BillRenderer.buildView(context, config, items)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(layoutPageWidthPx, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        billView.measure(widthSpec, heightSpec)
        val totalH = billView.measuredHeight.coerceAtLeast(1)
        billView.layout(0, 0, layoutPageWidthPx, totalH)

        try {
            for (pageIndex in 0 until layoutPageCount) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    return
                }

                val page = document.startPage(pageIndex)
                val canvas = page.canvas
                val pageW = canvas.width.coerceAtLeast(1)
                val pageH = canvas.height.coerceAtLeast(1)

                canvas.drawColor(Color.WHITE)
                canvas.save()
                canvas.clipRect(0, 0, pageW, pageH)
                canvas.translate(0f, -pageIndex * pageH.toFloat())
                billView.draw(canvas)
                canvas.restore()

                document.finishPage(page)
            }

            document.writeTo(FileOutputStream(destination.fileDescriptor))
            callback.onWriteFinished(arrayOf(PageRange(0, layoutPageCount - 1)))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message ?: "Write failed")
        } finally {
            document.close()
            pdfDocument = null
        }
    }

    override fun onFinish() {
        pdfDocument?.close()
        pdfDocument = null
    }

    /**
     * Uses a throwaway [PrintedPdfDocument] so width/height match the real PDF canvas
     * (avoids mils/dpi rounding mismatch with [pageSizePx]).
     */
    private fun measurePageSizeWithTempDocument(c: Context, a: PrintAttributes): Pair<Int, Int> {
        return try {
            val tmp = PrintedPdfDocument(c, a)
            val page = tmp.startPage(0)
            val w = page.canvas.width.coerceAtLeast(1)
            val h = page.canvas.height.coerceAtLeast(1)
            tmp.finishPage(page)
            tmp.close()
            w to h
        } catch (_: Exception) {
            pageSizePx(a)
        }
    }

    /**
     * Fallback when temp document probe fails.
     */
    private fun pageSizePx(attrs: PrintAttributes): Pair<Int, Int> {
        val media = attrs.mediaSize
        val dpiX = attrs.resolution?.horizontalDpi?.takeIf { it > 0 } ?: 203
        val dpiY = attrs.resolution?.verticalDpi?.takeIf { it > 0 } ?: dpiX

        if (media == null) {
            return 464 to 2200
        }

        val wPx = ((media.widthMils / 1000f) * dpiX).toInt().coerceAtLeast(1)
        val hPx = ((media.heightMils / 1000f) * dpiY).toInt().coerceAtLeast(1)
        return wPx to hPx
    }
}
