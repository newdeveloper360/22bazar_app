package com.userplay.bazar22.ui.fragments.navigation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.BlueprintReferItemsBinding
import com.userplay.bazar22.models.user_level.UserLevelData
import java.util.*

class UserLevelAdapter(private val userList: ArrayList<UserLevelData>) :
    RecyclerView.Adapter<UserLevelAdapter.UserLevelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLevelViewHolder {
        val binding = BlueprintReferItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserLevelViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserLevelViewHolder, position: Int) {
        val item = userList[position]
        holder.bind(item)
    }

    class UserLevelViewHolder(private val binding: BlueprintReferItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserLevelData) {
            binding.name.text = item.name ?: "-"
            binding.id.text = item.id ?: "-"
            binding.phone.text = item.mobile ?: "-"
            binding.date.text = item.createdAt ?: "-"

            binding.tvTodayCommission.visibility = View.VISIBLE
            binding.tvTotalCommission.visibility = View.VISIBLE

            binding.tvTodayCommission.text = "₹${item.todayCommission ?: 0}"
            binding.tvTotalCommission.text = "₹${item.totalCommission ?: 0}"        }
    }
}
