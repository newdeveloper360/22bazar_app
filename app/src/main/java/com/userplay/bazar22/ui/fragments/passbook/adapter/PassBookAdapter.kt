package com.userplay.bazar22.ui.fragments.passbook.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.BlueprintPassbookItemsBinding
import com.userplay.bazar22.models.getpassbook.Data

class PassBookAdapter(
    private val mList: ArrayList<Data>,
    private val mContext: Context
) : RecyclerView.Adapter<PassBookAdapter.PassBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassBookViewHolder {
        return PassBookViewHolder(
            BlueprintPassbookItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: PassBookViewHolder, position: Int) {
        val item = mList[position]

        item.let {
            holder.binding(it)
        }
    }


    inner class PassBookViewHolder(private val mBinding: BlueprintPassbookItemsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(mData: Data) {

            mBinding.apply {

                val dateString = mData.createdAt
                val date = dateString?.substring(
                    0,
                    10
                ) // extract the date (starting from index 0 and ending at index 9)

                val time = dateString?.substring(11) // extract the time (starting from index 11)

                val newString = """
                    $date
                    $time
                    """.trimIndent() // concatenate the date and time with a line break character

                transactionDate.text = newString
                tvDetails.text = mData.details
                tvPreviousAmount.text =
                    mContext.getString(R.string.ruppes_symbol) + mData.previousAmount.toString()
                //if previous amount is bigger than current amount then show - sign else show + sign and set text color red if previous amount is bigger than current amount else set text color green
                if (mData.previousAmount!! > mData.currentAmount!!) {
                    tvtransAmount.setTextColor(mContext.resources.getColor(R.color.red))
                    tvtransAmount.text =
                        "- " + mContext.getString(R.string.ruppes_symbol) + mData.amount.toString()
                } else {
                    tvtransAmount.setTextColor(mContext.resources.getColor(R.color.green))
                    tvtransAmount.text =
                        "+ " + mContext.getString(R.string.ruppes_symbol) + mData.amount.toString()
                }
//                tvtransAmount.text =
//                    "- " + mContext.getString(R.string.ruppes_symbol) + mData.amount.toString()
                tvCurrentAmount.text =
                    mContext.getString(R.string.ruppes_symbol) + mData.currentAmount.toString()

            }
        }
    }
}