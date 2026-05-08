package com.userplay.bazar22.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.userplay.bazar22.BuildConfig
import com.userplay.bazar22.R
import com.userplay.bazar22.models.jantri_request.JantriDetailExpo
import com.userplay.bazar22.models.navigation.NawDrawerPanelItem
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.fragments.home.models.NumberDataModel
import com.userplay.bazar22.ui.fragments.home.models.NumberParentDataModel
import com.userplay.bazar22.ui.fragments.home.models.PairsModel
import com.userplay.bazar22.ui.fragments.home.models.PairsModelDigits
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private lateinit var mNavItemsTitles: Array<String>
private lateinit var dialog: ProgressDialog
private lateinit var mPref: MatkaPref


val mNavItemsIcons = intArrayOf(
    R.drawable.ic_outline_home_24,
    R.drawable.ic_bid_nav,
//    R.drawable.mpin_icon,
    R.drawable.ic_passbook,
    R.drawable.ic_gift,
    R.drawable.ic_outline_support_agent_24,
    R.drawable.ic_outline_account_balance_24,
//    R.drawable.ic_outline_notifications_active_24,
    R.drawable.ic_round_warning_amber_24,
    R.drawable.ic_baseline_attach_money_24,
    R.drawable.ic_outline_stacked_bar_chart_24,
    R.drawable.ic_outline_handyman_24,
    R.drawable.ic_baseline_attach_money_24,
    R.drawable.ic_outline_mobile_screen_share_24,
    R.drawable.ic_baseline_power_settings_new_24
)


fun Context.getDrawerPanelData(visibleInvite: Boolean, isShowResultOnly:Int,matkaEnable:Boolean): List<NawDrawerPanelItem> {

    mNavItemsTitles =
        if (isShowResultOnly==1 || matkaEnable){
            resources.getStringArray(R.array.drawerItems2)
        }else{
            resources.getStringArray(R.array.drawerItems)
        }
    val data: MutableList<NawDrawerPanelItem> = ArrayList()
    for (i in mNavItemsTitles.indices) {
        val navItem = NawDrawerPanelItem()
        navItem.mTitle = mNavItemsTitles[i]
        navItem.mImage = mNavItemsIcons[i]
        if (navItem.mTitle == "Invite And Earn") {
            if (visibleInvite) {
                data.add(navItem)
            }
        } else {
            data.add(navItem)
        }

    }
    return data
}


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity?.hideKeyboard() {
    if (this == null) {
        return
    }
    val view = this.currentFocus
    if (view != null) {
        val inm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context.showProgressDialog() {
    dialog = ProgressDialog(this)
    try {
        dialog.show()
        Log.e("dialog", "show")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("dialog", "exception" + e.localizedMessage)
    }
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    if (dialog.window != null) dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.progress_item_center)
    dialog.setCancelable(false)
}


fun Context.dismissDialog() {
    if (dialog != null) {
        if (dialog.isShowing) {
            dialog.dismiss()
            Log.e("dialog", "dismiss")
        }
    } else {
        Log.e("dialog", "is nulled")
    }
}


fun getCurrentDateAndTime(): String {
    val sdf: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val currentDateandTime: String = sdf.format(Date())

    return currentDateandTime
}

@SuppressLint("HardwareIds")
fun Context.getDeviceID(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}


//private fun getTime(): String? {
//    val delegate = "hh:mm::ss aaa"
//    return DateFormat.format(delegate, Calendar.getInstance().time)
//}


@SuppressLint("SimpleDateFormat")
fun String.changeToTime(): String {
//    val time = "11:11:21"
    val sdf = SimpleDateFormat("H:mm")
    return SimpleDateFormat("h:mm a").format(sdf.parse(this) as Date)
}


fun Int.SinglePanaCondition(): Boolean {
    val firstDigit = this / 100
    val secondDigit = this / 10 % 10
    val thirdDigit = this % 10
    return (secondDigit > firstDigit && thirdDigit > secondDigit) || (secondDigit > firstDigit && thirdDigit == 0)

}

//fun Int.doublePana(): Boolean {
//    val firstDigit = this / 100
//    val secondDigit = this / 10 % 10
//    val thirdDigit = this % 10
//    return ((firstDigit == secondDigit) || (secondDigit < thirdDigit || thirdDigit == 0))
//}

fun isDoublePana(str: String): Boolean {
    val len = str.length
    if (len != 3) {
        return false
    } else {
        val num = str.toInt()
        val a = num / 100
        val b = (num / 10) % 10
        val c = num % 10

        if (a == b && b == c) {
            return false
        }
        if (b == c && a == 0) {
            return false
        }

        if (a>c && c != 0) {
            return false
        }
        if (a == b && b != c) {
            return true
        }

        if (b == c && a != b && a != 0) {
            return true
        }
        return false
    }
}


fun Int.triplePana(): Boolean {

    val firstDigit: Int = this / 100
    val secondDigit: Int = this / 10 % 10
    val thirdDigit: Int = this % 10

    return firstDigit == secondDigit && secondDigit == thirdDigit
}


fun Int.singlePanaBulk(): ArrayList<Int> {
    val number = this
    val lst = ArrayList<Int>()

    for (i in 100..999) {
        val digits = IntArray(3)
        digits[0] = i / 100 // get the first digit
        digits[1] = i % 100 / 10 // get the second digit
        digits[2] = i % 10 // get the third digit
        if ((digits[0] < digits[1] && digits[1] < digits[2] || digits[2] == 0 && digits[1] != 0 && digits[0] < digits[1]) && (digits[0] + digits[1] + digits[2] == number || (digits[0] + digits[1] + digits[2]) % 10 == number % 10)) {
            lst.add(i)
        }
    }

    return lst
}

fun Int.singlePanaPairs(): ArrayList<PairsModel> {
    val number = this
    val lst = ArrayList<PairsModel>()

    for (i in 100..999) {
        val digits = IntArray(3)
        digits[0] = i / 100 // get the first digit
        digits[1] = i % 100 / 10 // get the second digit
        digits[2] = i % 10 // get the third digit
        if ((digits[0] < digits[1] && digits[1] < digits[2] || digits[2] == 0 && digits[1] != 0 && digits[0] < digits[1]) && (digits[0] + digits[1] + digits[2] == number || (digits[0] + digits[1] + digits[2]) % 10 == number % 10)) {
            lst.add(PairsModel(i, ""))
        }
    }

    return lst
}
fun Int.jodiDigitsPairs(): ArrayList<PairsModelDigits> {
    val number = this
    val lst = ArrayList<PairsModelDigits>()
    for (i in 0..9) {
        lst.add(PairsModelDigits("${number}${i}",""))
    }

    return lst
}

fun singleDigitsPairs(): ArrayList<PairsModelDigits> {
   return arrayListOf(
        PairsModelDigits("0",""),
        PairsModelDigits("1",""),
        PairsModelDigits("2",""),
        PairsModelDigits("3",""),
        PairsModelDigits("4",""),
        PairsModelDigits("5",""),
        PairsModelDigits("6",""),
        PairsModelDigits("7",""),
        PairsModelDigits("8",""),
        PairsModelDigits("9","")
    )
}


fun Int.allPana(): Boolean {
    val numStr = this.toString()
    val isSinglePana = this.SinglePanaCondition()
    val isDoublePana = isDoublePana(numStr)
    val isTriplePana = this.triplePana()
    return when {
        isTriplePana -> true
        isDoublePana -> true
        isSinglePana -> true
        else -> false
    }
}


fun Int.AllPanaCheck(): Boolean {
    val firstDigit = this / 100
    val secondDigit = this / 10 % 10
    val thirdDigit = this % 10


    return if (secondDigit in (firstDigit + 1) until thirdDigit || thirdDigit == 0) {
        true
    }
//    else if (firstDigit == secondDigit && secondDigit < thirdDigit || thirdDigit == 0) {
//        true
//    }
    else firstDigit == secondDigit && secondDigit == thirdDigit


}


fun Int.doublePanaBulk(): ArrayList<Int> {
    var number = 0
    val lst = ArrayList<Int>()

    for (i in 100 until 1000) {
        val digits = IntArray(3)
        digits[0] = i / 100 // get the first digit
        digits[1] = (i % 100) / 10 // get the second digit
        digits[2] = i % 10 // get the third digit

        if ((digits[0] == digits[1] || digits[1] == digits[2]) && (digits[2] >= digits[0]) || (digits[0] == digits[1] || digits[1] == digits[2]) && digits[2] == 0) {
            if (digits[0] != digits[1] || digits[1] != digits[2]) {
                val sum = digits[0] + digits[1] + digits[2]
                if (sum == this || sum % 10 == this % 10) {
                    if(isDoublePana(i.toString())) {
                        lst.add(i)
                    }
                }
            }
        }
    }

    println("List of three-digit numbers where the first two digits are the same or the last two digits are the same, and whose sum of digits equals the input number or whose last digit of the sum equals the last digit of the input number:")
    println(lst)

    return lst
}

fun Int.doublePanaBulkPairs(): ArrayList<PairsModel> {
    var number = 0
    val lst = ArrayList<PairsModel>()
    for (i in 100 until 1000) {
        val digits = IntArray(3)
        digits[0] = i / 100 // get the first digit
        digits[1] = (i % 100) / 10 // get the second digit
        digits[2] = i % 10 // get the third digit

        if ((digits[0] == digits[1] || digits[1] == digits[2]) && (digits[2] >= digits[0]) || (digits[0] == digits[1] || digits[1] == digits[2]) && digits[2] == 0) {
            if (digits[0] != digits[1] || digits[1] != digits[2]) {
                val sum = digits[0] + digits[1] + digits[2]
                if (sum == this || sum % 10 == this % 10) {
                    lst.add(PairsModel(i, ""))
                }
            }
        }
    }

    println("List of three-digit numbers where the first two digits are the same or the last two digits are the same, and whose sum of digits equals the input number or whose last digit of the sum equals the last digit of the input number:")
    println(lst)

    return lst
}

fun Int.triplePanaBulk(): ArrayList<Int> {
    val lst = ArrayList<Int>()
    var newNumber = ""
    when (this) {
        0 -> {
            lst.add(0)
        }
        1 -> {
            lst.add(777)
        }
        2 -> {
            lst.add(444)
        }
        3 -> {
            lst.add(111)
        }
        4 -> {
            lst.add(888)
        }
        5 -> {
            lst.add(555)
        }
        6 -> {
            lst.add(222)
        }
        7 -> {
            lst.add(999)
        }
        8 -> {
            lst.add(666)
        }
        9 -> {
            lst.add(333)
        }
    }
    return lst
}


fun Int.triplePanaBulkPairs(): ArrayList<PairsModel> {
    val lst = ArrayList<PairsModel>()
    when (this) {
        0 -> {
            lst.add(PairsModel(0, ""))
        }
        1 -> {
            lst.add(PairsModel(777, ""))
        }
        2 -> {
            lst.add(PairsModel(444, ""))
        }
        3 -> {
            lst.add(PairsModel(111, ""))
        }
        4 -> {
            lst.add(PairsModel(888, ""))
        }
        5 -> {
            lst.add(PairsModel(555, ""))
        }
        6 -> {
            lst.add(PairsModel(222, ""))
        }
        7 -> {
            lst.add(PairsModel(999, ""))
        }
        8 -> {
            lst.add(PairsModel(666, ""))
        }
        9 -> {
            lst.add(PairsModel(333, ""))
        }
    }
    println("List of three-digit numbers where the first two digits are the same or the last two digits are the same, and whose sum of digits equals the input number or whose last digit of the sum equals the last digit of the input number:")
    println(lst)

    return lst
}

//fun singlePana(number: Int): Boolean {
//    val firstDigit = number / 100
//    val secondDigit = number / 10 % 10
//    val thirdDigit = number % 10
//    return if (firstDigit < secondDigit && secondDigit < thirdDigit || thirdDigit == 0) {
//        true
//    } else {
//        false
//    }
//}

//
//fun doublePana(number: Int): Boolean {
//
//    val firstDigit = number / 100
//    val secondDigit = (number / 10) % 10
//    val thirdDigit = number % 10
//
//    return firstDigit == secondDigit && secondDigit < thirdDigit || thirdDigit == 0
//}


fun getJantri(): MutableList<JantriDetailExpo> {
    val numberList: MutableList<JantriDetailExpo> = ArrayList()
    for (i in 1..100) {
        // val number = if (i == 100) "00" else String.format("%02d", i)
        numberList.add(JantriDetailExpo(i.toString()))
    }
    return numberList
}

//fun getJantri(): MutableList<String> {
//    val numberList: MutableList<String> = ArrayList()
//    for(i in 1..100) {
//        if (i == 100) {
//            numberList.add("00")
//        } else {
//            numberList.add(String.format("%02d", i))
//        }
//    }
//
//    return numberList
//}


fun getAnderHaruf(): MutableList<String> {

    val numbers: MutableList<String> = ArrayList()
    for (i in 1..9) {
        val num = i * 111

        if (num in 100..1000) {
            numbers.add(num.toString())
        }

    }
    numbers.add("000")

    return numbers
}

fun getBharHaruf(): MutableList<String> {

    val numbers: MutableList<String> = ArrayList()
    for (i in 1..9) {
        val num = i * 1111

        if (num in 1000..10000) {
            numbers.add(num.toString())
        }

    }
    numbers.add("0000")

    return numbers
}


fun pairsTwoGivenValuesNumbers(num1: String, num2: String): MutableList<Int> {
    val pairs: MutableList<Int> = ArrayList()

    // Find the number of digits in each number
    val num1Digits = num1.length
    val num2Digits = num2.length

    // Iterate through digits of the first number
    for (i in 0 until num1Digits) {
        val digit1 = Character.getNumericValue(num1[i])

        // Iterate through digits of the second number
        for (j in 0 until num2Digits) {
            val digit2 = Character.getNumericValue(num2[j])

            // Combine the two digits and add to list
            val pair = digit1 * 10 + digit2
            if (!pairs.contains(pair)) {
                pairs.add(pair)
            }
        }
    }

    // Optional: Sort the pairs if needed
    pairs.sort()

    // Print pairs for debugging
    for (item in pairs) {
        Log.e("data duplicate", "" + item)
    }

    return pairs
}

fun areDigitsSame(number: Int): Boolean {
    // convert the integer to a string to access its digits
    val numberStr = number.toString()

    // iterate through the digits and check if they are all the same
    for (i in 1 until numberStr.length) {
        if (numberStr[i] != numberStr[0]) {
            // digits are not all the same, return false
            return false
        }
    }
    // all digits are the same, return true
    return true
}

fun allNoToNo(startValue: Int, endValue: Int): MutableList<Int> {
    val myList: MutableList<Int> = ArrayList()
    for (i in startValue..endValue) {
        myList.add(i)
    }

    return myList
}

fun DrawerLayout.closeDrawer() {
    try {
        this.closeDrawers()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Context.shareData(inviteCode: String) {
    mPref = MatkaPref(this)
    Log.e("invite_code", "" + inviteCode)
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND

    var link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
    if (mPref.getPlayStoreEnable(Constants.PLAY_STORE_ENABLE) == 0) {
        link = Constants.LIVE_SERVER
    }
    if (mPref.getEarningSystem()) {
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Play Now India's Best Online Matka on ${getString(R.string.app_name)} : $link Use My Invite code to Get Additional Bonus: $inviteCode"
        )
    } else {
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Play Now India's Best Online Matka on ${getString(R.string.app_name)} : $link"
        )
    }
    sendIntent.type = "text/plain"
    startActivity(sendIntent)
}

fun getSingleDigitsBulkList(): ArrayList<Int> {
//    val list: kotlin.collections.ArrayList<Int> = ArrayList()
//    for (i in 0..9) {
//        list.add(i)
//    }
//    return list

    val myList = kotlin.collections.ArrayList<Int>()
    for (i in 1..9) {
        myList.add(i)
    }
    myList.add(0)

    return myList
}


fun currentDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.openWebPage(url: String?) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}



fun getJodiFamilyData(): ArrayList<NumberDataModel> {
    return ArrayList<NumberDataModel>().apply {
        for (tens in 0..9) {
            for (units in 0..9) {
                val jodiNumber = "" + tens + units
                add(NumberDataModel("${jodiNumber}"))
            }
        }
    }
}

fun String.generateJodiFamilyNumber2(): List<String> {
    if (Constants.isEnableNewJodiFamilyNumber){
        return when(this){
            "11","16","61","66" -> listOf<String>("11","16","61","66")
            "22","27","72","77" -> listOf<String>("22","27","72","77")
            "33","38","83","88" -> listOf<String>("33","38","83","88")
            "44","49","94","99" -> listOf<String>("44","49","94","99")
            "00","05","50","55" -> listOf<String>("00","05","50","55")
            else->{
                listOf<String>()
            }
        }
    }else{
       return when(this){
            "12","17","21","26","62","67","71","76" ->  listOf<String>("12","17","21","26","62","67","71","76")
            "13","18","31","36","63","68","81","86" ->  listOf<String>("13","18","31","36","63","68","81","86")
            "14","19","41","46","64","69","91","96" ->  listOf<String>("14","19","41","46","64","69","91","96")
            "01","06","10","15","51","56","60","65" ->  listOf<String>("01","06","10","15","51","56","60","65")
            "23","28","32","37","73","78","82","87" ->  listOf<String>("23","28","32","37","73","78","82","87")
            "24","29","42","47","74","79","92","97" ->  listOf<String>("24","29","42","47","74","79","92","97")
            "02","07","20","25","52","57","70","75" ->  listOf<String>("02","07","20","25","52","57","70","75")
            "34","39","43","48","84","89","93","98" ->  listOf<String>("34","39","43","48","84","89","93","98")
            "03","08","30","35","53","58","80","85" ->  listOf<String>("03","08","30","35","53","58","80","85")
            "04","09","40","45","54","59","90","95" ->  listOf<String>("04","09","40","45","54","59","90","95")
            "05","16","27","38","49","50","61","72","83","94" ->  listOf<String>("05","16","27","38","49","50","61","72","83","94")
            "00","11","22","33","44","55","66","77","88","99" ->  listOf<String>("00","11","22","33","44","55","66","77","88","99")
            else->{
                 listOf<String>()
            }
        }
    }

}



fun getSinglePanaData(): ArrayList<NumberParentDataModel> {
    return ArrayList<NumberParentDataModel>().apply {
        add(
            NumberParentDataModel(
                "0", arrayListOf(
                    NumberDataModel("127"),
                    NumberDataModel("136"),
                    NumberDataModel("145"),
                    NumberDataModel("190"),
                    NumberDataModel("235"),
                    NumberDataModel("280"),
                    NumberDataModel("370"),
                    NumberDataModel("389"),
                    NumberDataModel("460"),
                    NumberDataModel("479"),
                    NumberDataModel("569"),
                    NumberDataModel("578"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "1", arrayListOf(
                    NumberDataModel("128"),
                    NumberDataModel("137"),
                    NumberDataModel("146"),
                    NumberDataModel("236"),
                    NumberDataModel("245"),
                    NumberDataModel("290"),
                    NumberDataModel("380"),
                    NumberDataModel("470"),
                    NumberDataModel("489"),
                    NumberDataModel("560"),
                    NumberDataModel("579"),
                    NumberDataModel("678"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "2", arrayListOf(
                    NumberDataModel("129"),
                    NumberDataModel("138"),
                    NumberDataModel("147"),
                    NumberDataModel("156"),
                    NumberDataModel("237"),
                    NumberDataModel("246"),
                    NumberDataModel("345"),
                    NumberDataModel("390"),
                    NumberDataModel("480"),
                    NumberDataModel("570"),
                    NumberDataModel("589"),
                    NumberDataModel("679"),
                )
            )
        )
        add(
            NumberParentDataModel(
                "3", arrayListOf(
                    NumberDataModel("120"),
                    NumberDataModel("139"),
                    NumberDataModel("148"),
                    NumberDataModel("157"),
                    NumberDataModel("238"),
                    NumberDataModel("247"),
                    NumberDataModel("256"),
                    NumberDataModel("346"),
                    NumberDataModel("490"),
                    NumberDataModel("580"),
                    NumberDataModel("670"),
                    NumberDataModel("689"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "4", arrayListOf(
                    NumberDataModel("130"),
                    NumberDataModel("149"),
                    NumberDataModel("158"),
                    NumberDataModel("167"),
                    NumberDataModel("239"),
                    NumberDataModel("248"),
                    NumberDataModel("257"),
                    NumberDataModel("347"),
                    NumberDataModel("356"),
                    NumberDataModel("590"),
                    NumberDataModel("680"),
                    NumberDataModel("789"),
                )
            )
        )

        add(
            NumberParentDataModel(
                "5", arrayListOf(
                    NumberDataModel("140"),
                    NumberDataModel("159"),
                    NumberDataModel("168"),
                    NumberDataModel("230"),
                    NumberDataModel("249"),
                    NumberDataModel("258"),
                    NumberDataModel("267"),
                    NumberDataModel("348"),
                    NumberDataModel("357"),
                    NumberDataModel("456"),
                    NumberDataModel("690"),
                    NumberDataModel("780"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "6", arrayListOf(
                    NumberDataModel("123"),
                    NumberDataModel("150"),
                    NumberDataModel("169"),
                    NumberDataModel("178"),
                    NumberDataModel("240"),
                    NumberDataModel("259"),
                    NumberDataModel("268"),
                    NumberDataModel("349"),
                    NumberDataModel("358"),
                    NumberDataModel("367"),
                    NumberDataModel("457"),
                    NumberDataModel("790"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "7", arrayListOf(
                    NumberDataModel("124"),
                    NumberDataModel("160"),
                    NumberDataModel("278"),
                    NumberDataModel("179"),
                    NumberDataModel("250"),
                    NumberDataModel("269"),
                    NumberDataModel("340"),
                    NumberDataModel("359"),
                    NumberDataModel("368"),
                    NumberDataModel("458"),
                    NumberDataModel("467"),
                    NumberDataModel("890"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "8", arrayListOf(
                    NumberDataModel("125"),
                    NumberDataModel("134"),
                    NumberDataModel("170"),
                    NumberDataModel("189"),
                    NumberDataModel("260"),
                    NumberDataModel("279"),
                    NumberDataModel("350"),
                    NumberDataModel("369"),
                    NumberDataModel("468"),
                    NumberDataModel("378"),
                    NumberDataModel("459"),
                    NumberDataModel("567"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "9", arrayListOf(
                    NumberDataModel("126"),
                    NumberDataModel("135"),
                    NumberDataModel("180"),
                    NumberDataModel("234"),
                    NumberDataModel("270"),
                    NumberDataModel("289"),
                    NumberDataModel("360"),
                    NumberDataModel("379"),
                    NumberDataModel("450"),
                    NumberDataModel("469"),
                    NumberDataModel("478"),
                    NumberDataModel("568"),
                )
            )
        )

    }
}

fun getDoublePanaData(): ArrayList<NumberParentDataModel> {
    return ArrayList<NumberParentDataModel>().apply {
        add(
            NumberParentDataModel(
                "0", arrayListOf(
                    NumberDataModel("118"),
                    NumberDataModel("226"),
                    NumberDataModel("244"),
                    NumberDataModel("299"),
                    NumberDataModel("334"),
                    NumberDataModel("488"),
                    NumberDataModel("550"),
                    NumberDataModel("668"),
                    NumberDataModel("677"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "1", arrayListOf(
                    NumberDataModel("100"),
                    NumberDataModel("119"),
                    NumberDataModel("155"),
                    NumberDataModel("227"),
                    NumberDataModel("335"),
                    NumberDataModel("344"),
                    NumberDataModel("399"),
                    NumberDataModel("588"),
                    NumberDataModel("669"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "2", arrayListOf(
                    NumberDataModel("110"),
                    NumberDataModel("200"),
                    NumberDataModel("228"),
                    NumberDataModel("255"),
                    NumberDataModel("336"),
                    NumberDataModel("499"),
                    NumberDataModel("660"),
                    NumberDataModel("688"),
                    NumberDataModel("778"),
                )
            )
        )
        add(
            NumberParentDataModel(
                "3", arrayListOf(
                    NumberDataModel("166"),
                    NumberDataModel("229"),
                    NumberDataModel("300"),
                    NumberDataModel("337"),
                    NumberDataModel("355"),
                    NumberDataModel("445"),
                    NumberDataModel("599"),
                    NumberDataModel("779"),
                    NumberDataModel("788"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "4", arrayListOf(
                    NumberDataModel("112"),
                    NumberDataModel("220"),
                    NumberDataModel("266"),
                    NumberDataModel("338"),
                    NumberDataModel("400"),
                    NumberDataModel("446"),
                    NumberDataModel("455"),
                    NumberDataModel("699"),
                    NumberDataModel("770"),
                )
            )
        )

        add(
            NumberParentDataModel(
                "5", arrayListOf(
                    NumberDataModel("113"),
                    NumberDataModel("122"),
                    NumberDataModel("177"),
                    NumberDataModel("339"),
                    NumberDataModel("366"),
                    NumberDataModel("447"),
                    NumberDataModel("500"),
                    NumberDataModel("799"),
                    NumberDataModel("889"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "6", arrayListOf(
                    NumberDataModel("600"),
                    NumberDataModel("114"),
                    NumberDataModel("277"),
                    NumberDataModel("330"),
                    NumberDataModel("448"),
                    NumberDataModel("466"),
                    NumberDataModel("556"),
                    NumberDataModel("880"),
                    NumberDataModel("899"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "7", arrayListOf(
                    NumberDataModel("115"),
                    NumberDataModel("133"),
                    NumberDataModel("188"),
                    NumberDataModel("223"),
                    NumberDataModel("377"),
                    NumberDataModel("449"),
                    NumberDataModel("557"),
                    NumberDataModel("566"),
                    NumberDataModel("700"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "8", arrayListOf(
                    NumberDataModel("116"),
                    NumberDataModel("224"),
                    NumberDataModel("233"),
                    NumberDataModel("288"),
                    NumberDataModel("440"),
                    NumberDataModel("477"),
                    NumberDataModel("558"),
                    NumberDataModel("800"),
                    NumberDataModel("990"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "9", arrayListOf(
                    NumberDataModel("117"),
                    NumberDataModel("144"),
                    NumberDataModel("199"),
                    NumberDataModel("225"),
                    NumberDataModel("388"),
                    NumberDataModel("559"),
                    NumberDataModel("577"),
                    NumberDataModel("667"),
                    NumberDataModel("900"),
                )
            )
        )

    }
}

fun getTripplePanaData(): ArrayList<NumberParentDataModel> {
    return ArrayList<NumberParentDataModel>().apply {
        add(
            NumberParentDataModel(
                "0", arrayListOf(
                    NumberDataModel("000")
                )
            )
        )


        add(
            NumberParentDataModel(
                "1", arrayListOf(
                    NumberDataModel("777")
                )
            )
        )


        add(
            NumberParentDataModel(
                "2", arrayListOf(
                    NumberDataModel("444")
                )
            )
        )
        add(
            NumberParentDataModel(
                "3", arrayListOf(
                    NumberDataModel("111"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "4", arrayListOf(
                    NumberDataModel("888")
                )
            )
        )

        add(
            NumberParentDataModel(
                "5", arrayListOf(
                    NumberDataModel("555")
                )
            )
        )


        add(
            NumberParentDataModel(
                "6", arrayListOf(
                    NumberDataModel("222")
                )
            )
        )


        add(
            NumberParentDataModel(
                "7", arrayListOf(
                    NumberDataModel("999"),
                )
            )
        )


        add(
            NumberParentDataModel(
                "8", arrayListOf(
                    NumberDataModel("666")
                )
            )
        )


        add(
            NumberParentDataModel(
                "9", arrayListOf(
                    NumberDataModel("333")
                )
            )
        )

    }
}
fun getListPanaFamily(): HashMap<String, List<String>> {
    return HashMap<String, List<String>>().apply {
        put("111", listOf<String>("111", "116", "166", "666"))

        put(
            "112", listOf<String>(
                "112", "117", "126", "167", "266", "667"))

        put(
            "113", listOf<String>("113", "118", "136", "168", "366", "668"))
        put("114", listOf<String>(
            "114", "119", "146", "169", "466", "669"))

        put(
            "115",listOf<String>(
                "110", "115", "156", "160", "566", "660"))

        put(
            "122",listOf<String>(
                "122", "127", "177", "226", "267", "677"))

        put(
            "123",listOf<String>(
                "123", "128", "137", "178", "236", "268", "367", "678"))

        put(
            "124",listOf<String>(
                "124", "129", "147", "179", "246", "269", "467", "679"))

        put(
            "125",listOf<String>(
                "120", "125", "157", "170", "256", "260", "567", "670"))

        put(
            "133",listOf<String>(
                "133", "138", "188", "336", "368", "688"))

        put(
            "134",listOf<String>(
                "134", "139", "148", "189", "346", "369", "468", "689"))

        put(
            "135",listOf<String>(
                "130", "135", "158", "180", "356", "360", "568", "680"))

        put(
            "144",listOf<String>(
                "144", "149", "199", "446", "469", "699"))

        put(
            "145",listOf<String>(
                "140", "145", "159", "190", "456", "460", "569", "690"))

        put(
            "155",listOf<String>(
                "100","150","155", "556", "560", "600"))

        put(
            "222",listOf<String>(
                "222", "227", "277", "777"))

        put(
            "223",listOf<String>(
                "223", "228", "237", "278", "377", "778"))

        put(
            "224",listOf<String>(
                "224", "229", "247", "279", "477", "779"))

        put(
            "225",listOf<String>(
                "220", "225", "257", "270", "577", "770"))

        put(
            "233",listOf<String>(
                "233", "238", "288", "337", "378", "788"))

        put(
            "234",listOf<String>(
                "234", "239", "248", "289", "347", "379", "478", "789"))

        put(
            "235",listOf<String>(
                "230", "235", "258", "280", "357", "370", "578", "780"))

        put(
            "244",listOf<String>(
                "244", "249", "299", "447", "479", "799"))

        put(
            "245",listOf<String>(
                "240", "245", "259", "290", "457", "470", "579", "790"))

        put(
            "255",listOf<String>(
                "200", "250", "255", "557", "570", "700"))

        put(
            "333",listOf<String>(
                "333", "338", "388", "888"))

        put(
            "334",listOf<String>(
                "334", "339", "348", "389", "488", "889"))

        put(
            "335",listOf<String>(
                "330", "335", "358", "380", "588", "880"))

        put(
            "344",listOf<String>(
                "344", "349", "399", "448", "489", "899"))

        put(
            "345",listOf<String>(
                "340", "345", "359", "390", "458", "480", "589", "890"))

        put(
            "355",listOf<String>(
                "300", "350", "355", "558", "580", "800"))

        put(
            "444",listOf<String>(
                "444", "449", "499", "999"))

        put(
            "445",listOf<String>(
                "440", "445", "459", "490", "599", "990"))

        put(
            "455",listOf<String>(
                "400", "450", "455", "559", "590", "900"))

        put(
            "555",listOf<String>(
                "000", "500", "550", "555"))






        /*   put(
               "11",
               listOf<String>("110", "111", "112", "113", "114", "115", "116", "117", "118", "119")
           )
           put(
               "12",
               listOf<String>("112", "120", "122", "123", "124", "125", "126", "127", "128", "129")
           )
           put(
               "13",
               listOf<String>("113", "123", "130", "133", "134", "135", "136", "137", "138", "139")
           )
           put(
               "14",
               listOf<String>("114", "124", "134", "140", "144", "145", "146", "147", "148", "149")
           )
           put(
               "15",
               listOf<String>("115", "125", "135", "145", "150", "155", "156", "157", "158", "159")
           )
           put(
               "16",
               listOf<String>("116", "126", "136", "146", "156", "160", "166", "167", "168", "169")
           )
           put(
               "17",
               listOf<String>("117", "127", "137", "147", "157", "167", "170", "177", "178", "179")
           )
           put(
               "18",
               listOf<String>("118", "128", "138", "148", "158", "168", "178", "180", "188", "189")
           )
           put(
               "19",
               listOf<String>("119", "129", "139", "149", "159", "169", "179", "189", "190", "199")
           )
           put(
               "20",
               listOf<String>("120", "200", "220", "230", "240", "250", "260", "270", "280", "290")
           )
           put(
               "22",
               listOf<String>("122", "220", "223", "224", "225", "226", "227", "228", "229", "222")
           )
           put(
               "23",
               listOf<String>("123", "230", "233", "234", "235", "236", "237", "238", "239", "223")
           )
           put(
               "24",
               listOf<String>("124", "240", "244", "245", "246", "247", "248", "249", "224", "234")
           )
           put(
               "25",
               listOf<String>("125", "250", "255", "256", "257", "258", "259", "225", "235", "245")
           )
           put(
               "26",
               listOf<String>("126", "260", "266", "267", "268", "269", "226", "236", "246", "256")
           )
           put(
               "27",
               listOf<String>("127", "270", "277", "278", "279", "227", "237", "247", "257", "267")
           )
           put(
               "28",
               listOf<String>("128", "280", "288", "289", "228", "238", "248", "258", "268", "278")
           )
           put(
               "29",
               listOf<String>("129", "290", "299", "229", "239", "249", "259", "269", "279", "289")
           )
           put(
               "30",
               listOf<String>("130", "230", "300", "330", "340", "350", "360", "370", "380", "390")
           )
           put(
               "34",
               listOf<String>("134", "234", "334", "340", "344", "345", "346", "347", "348", "349")
           )
           put(
               "35",
               listOf<String>("135", "350", "355", "335", "345", "235", "356", "357", "358", "359")
           )
           put(
               "36",
               listOf<String>("136", "360", "366", "336", "346", "356", "367", "368", "369", "236")
           )
           put(
               "37",
               listOf<String>("137", "370", "377", "337", "347", "357", "367", "378", "379", "237")
           )
           put(
               "38",
               listOf<String>("138", "380", "388", "238", "338", "348", "358", "368", "378", "389")
           )
           put(
               "39",
               listOf<String>("139", "390", "399", "349", "359", "369", "379", "389", "239", "339")
           )
           put(
               "40",
               listOf<String>("140", "240", "340", "400", "440", "450", "460", "470", "480", "490")
           )
           put(
               "44",
               listOf<String>("144", "244", "344", "440", "449", "445", "446", "447", "448", "444")
           )
           put(
               "45",
               listOf<String>("145", "245", "345", "450", "456", "457", "458", "459", "445", "455")
           )
           put(
               "46",
               listOf<String>("146", "460", "446", "467", "468", "469", "246", "346", "456", "466")
           )
           put(
               "47",
               listOf<String>("147", "470", "447", "478", "479", "247", "347", "457", "467", "477")
           )
           put(
               "48",
               listOf<String>("148", "480", "489", "248", "348", "448", "488", "458", "468", "478")
           )
           put(
               "49",
               listOf<String>("149", "490", "499", "449", "459", "469", "479", "489", "249", "349")
           )
           put(
               "50",
               listOf<String>("500", "550", "150", "250", "350", "450", "560", "570", "580", "590")
           )
           put(
               "55",
               listOf<String>("155", "556", "557", "558", "559", "255", "355", "455", "555", "550")
           )
           put(
               "56",
               listOf<String>("156", "556", "567", "568", "569", "356", "256", "456", "560", "566")
           )
           put(
               "57",
               listOf<String>("157", "257", "357", "457", "557", "578", "579", "570", "567", "577")
           )
           put(
               "58",
               listOf<String>("158", "558", "568", "578", "588", "589", "580", "258", "358", "458")
           )
           put(
               "59",
               listOf<String>("159", "259", "359", "459", "559", "569", "579", "589", "590", "599")
           )
           put(
               "60",
               listOf<String>("600", "160", "260", "360", "460", "560", "660", "670", "680", "690")
           )
           put(
               "66",
               listOf<String>("660", "667", "668", "669", "666", "166", "266", "366", "466", "566")
           )
           put(
               "67",
               listOf<String>("670", "167", "267", "367", "467", "567", "667", "678", "679", "677")
           )
           put(
               "68",
               listOf<String>("680", "688", "668", "678", "168", "268", "368", "468", "568", "689")
           )
           put(
               "69",
               listOf<String>("690", "169", "269", "369", "469", "569", "669", "679", "689", "699")
           )
           put(
               "70",
               listOf<String>("700", "170", "270", "370", "470", "570", "670", "770", "780", "790")
           )
           put(
               "77",
               listOf<String>("770", "177", "277", "377", "477", "577", "677", "778", "779", "777")
           )
           put(
               "78",
               listOf<String>("178", "278", "378", "478", "578", "678", "778", "788", "789", "780")
           )
           put(
               "79",
               listOf<String>("179", "279", "379", "479", "579", "679", "779", "789", "799", "790")
           )
           put(
               "80",
               listOf<String>("180", "280", "380", "480", "580", "680", "780", "880", "800", "890")
           )
           put(
               "88",
               listOf<String>("188", "288", "388", "488", "588", "688", "788", "889", "888", "880")
           )
           put(
               "89",
               listOf<String>("189", "289", "389", "489", "589", "689", "789", "889", "890", "899")
           )
           put(
               "90",
               listOf<String>("900", "190", "290", "390", "490", "590", "690", "790", "890", "900")
           )
           put(
               "99",
               listOf<String>("199", "299", "399", "499", "599", "699", "799", "899", "990", "999")
           )*/
    }
}


fun EditText.validatePanaFamily(): Boolean {
    return if (this.text.isNotBlank() && this.length() == 3) {
        this.error = null
        true

    } else {
        this.error = "Invalid Pana"
        false
    }
}

fun DialogFragment.showAllowingStateLoss(
    fragmentManager: FragmentManager,
    tag: String
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(this, tag)
    transaction.commitAllowingStateLoss()
}


