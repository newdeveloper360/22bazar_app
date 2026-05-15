package com.userplay.bazar22.printer

import android.view.Gravity
import android.view.View

// ---------------------------------------------------------------------------
// Alignment
// ---------------------------------------------------------------------------

enum class BillAlignment { LEFT, CENTER, RIGHT }

fun BillAlignment.toLayoutGravity(): Int = when (this) {
    BillAlignment.LEFT   -> Gravity.START
    BillAlignment.CENTER -> Gravity.CENTER_HORIZONTAL
    BillAlignment.RIGHT  -> Gravity.END
}

fun BillAlignment.toTextAlignment(): Int = when (this) {
    BillAlignment.LEFT   -> View.TEXT_ALIGNMENT_TEXT_START
    BillAlignment.CENTER -> View.TEXT_ALIGNMENT_CENTER
    BillAlignment.RIGHT  -> View.TEXT_ALIGNMENT_TEXT_END
}

// ---------------------------------------------------------------------------
// Text block config
// ---------------------------------------------------------------------------

data class TextConfig(
    val text: String,
    val fontSize: Float,          // sp
    val alignment: BillAlignment = BillAlignment.CENTER,
    val isBold: Boolean = false,
)

// ---------------------------------------------------------------------------
// Divider config
// ---------------------------------------------------------------------------

data class DividerConfig(
    /** "---" style dashed, or a solid line */
    val style: DividerStyle = DividerStyle.SOLID,
    val char: Char = '-',
    val charCount: Int = 32,      // how many chars wide the dashed line is
)

enum class DividerStyle { DASHED, SOLID }

// ---------------------------------------------------------------------------
// Column definitions for item table
// ---------------------------------------------------------------------------

data class ColumnDef(
    val header: String,
    val widthWeight: Float,       // sum of all weights = 1.0
    val alignment: BillAlignment = BillAlignment.LEFT,
)



// ---------------------------------------------------------------------------
// Master bill config
// ---------------------------------------------------------------------------

data class BillConfig(

    // ── Heading ─────────────────────────────────────────────────────────────
    val heading: TextConfig = TextConfig(
        text        = "INVENTORY BILL",
        fontSize    = 16f,
        alignment   = BillAlignment.CENTER,
        isBold      = true,
    ),

    // ── Sub-heading ──────────────────────────────────────────────────────────
    val subHeading: TextConfig = TextConfig(
        text        = "Store Receipt",
        fontSize    = 11f,
        alignment   = BillAlignment.CENTER,
    ),

    val srNo: TextConfig = TextConfig(
        text        = "S.No",
        fontSize    = 14f,
        alignment   = BillAlignment.RIGHT,
        isBold      = true,
    ),

    // ── Title / meta row (date, bill no, etc.) ───────────────────────────────
    val title: TextConfig = TextConfig(
        text        = "Bill No: #0001  |  Date: 09-May-2026",
        fontSize    = 14f,
        alignment   = BillAlignment.CENTER,
    ),
    // ── sub Title / meta row (date, bill no, etc.) ───────────────────────────────
    val subTitle: TextConfig = TextConfig(
        text        = "Bill No: #0001  |  Date: 09-May-2026",
        fontSize    = 14f,
        alignment   = BillAlignment.CENTER,
    ),
    // ── sub Title / meta row (date, bill no, etc.) ───────────────────────────────
    val label: TextConfig = TextConfig(
        text        = "",
        fontSize    = 14f,
        alignment   = BillAlignment.CENTER,
    ),

    // ── Divider between sections ─────────────────────────────────────────────
    val divider: DividerConfig = DividerConfig(),

    // ── Table columns ────────────────────────────────────────────────────────
    val columns: List<ColumnDef> = listOf(
        ColumnDef("Digits",   0.15f, BillAlignment.LEFT),
        ColumnDef("Points",  0.25f, BillAlignment.RIGHT),
    ),

    // ── Item text size ────────────────────────────────────────────────────────
    val itemFontSize: Float = 10f,
    val itemHeadingSize: Float = 16f,


    // ── Total row ─────────────────────────────────────────────────────────────
    val totalLabel: String = "TOTAL",
    val totalFontSize: Float = 12f,
    val currency: String = "",

    // ── Paper width (chars) for thermal — drives dashed divider width ─────────
    val thermalCharWidth: Int = 32,
)

