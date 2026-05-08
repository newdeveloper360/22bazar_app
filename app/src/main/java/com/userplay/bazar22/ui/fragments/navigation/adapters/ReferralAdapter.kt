package com.userplay.bazar22.ui.fragments.navigation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.BlueprintReferItemsBinding
import com.userplay.bazar22.models.refferal.ReferralUser
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReferralAdapter(private val mList: ArrayList<ReferralUser>) :
    RecyclerView.Adapter<ReferralAdapter.ReferralViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferralViewHolder {
        return ReferralViewHolder(
            BlueprintReferItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ReferralViewHolder, position: Int) {
        val item = mList[position]
        item.let {
            holder.binding(it)
        }
    }

    inner class ReferralViewHolder(private val mBinding: BlueprintReferItemsBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        @SuppressLint("SimpleDateFormat")
        fun binding(mReferralUser: ReferralUser) {
            mBinding.apply {

                name.text = mReferralUser.name
                id.text = mReferralUser.id.toString()
                phone.text = mReferralUser.phone


                val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
                val timestamp: Date
                try {
                    timestamp = mReferralUser.createdAt?.let { dateFormat.parse(it) } as Date
                    val dateOnlyFormat = SimpleDateFormat("MM/dd/yyyy")
                    val dateOnly = dateOnlyFormat.format(timestamp);
                    date.text = dateOnly
                } catch (e: ParseException) {
                    e.printStackTrace();
                }
            }

        }
    }
}