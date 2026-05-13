package com.userplay.bazar22.printer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// ---------------------------------------------------------------------------
// Make BillItem Parcelable so it can be passed via Intent extras
// ---------------------------------------------------------------------------

@Parcelize
data class BillItem(
    val name: String,
    val openClose: String = "",
    val amount: Int,
    /**
     * When set, row cells are taken from this list (length must match [BillConfig.columns]).
     * Totals still use [amount] per line.
     */
    val customRow: List<String>? = null,
) : Parcelable
