package com.userplay.bazar22.printer

/**
 * Holds a one-shot [BillConfig] + items for [BillPrintActivity] when the config
 * cannot be represented by intent extras alone (e.g. custom column sets).
 */
object BillPrintSession {

    data class Payload(
        val config: BillConfig,
        val items: List<BillItem>,
    )

    @Volatile
    var pending: Payload? = null
}
