package com.example.guardianai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmergencyHistoryAdapter(

    private val historyList:
    ArrayList<HistoryItem>

) : RecyclerView.Adapter<
        EmergencyHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(
        itemView
    ) {

        val tvHistoryIcon =
            itemView.findViewById<TextView>(
                R.id.tvHistoryIcon
            )

        val tvHistoryTitle =
            itemView.findViewById<TextView>(
                R.id.tvHistoryTitle
            )

        val tvHistoryDescription =
            itemView.findViewById<TextView>(
                R.id.tvHistoryDescription
            )

        val tvHistoryTime =
            itemView.findViewById<TextView>(
                R.id.tvHistoryTime
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_emergency_history,
                    parent,
                    false
                )

        return HistoryViewHolder(
            view
        )
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {

        val item =
            historyList[position]

        holder.tvHistoryIcon.text =
            item.icon

        holder.tvHistoryTitle.text =
            item.title

        holder.tvHistoryDescription.text =
            item.description

        holder.tvHistoryTime.text =
            item.time
    }

    override fun getItemCount(): Int {

        return historyList.size

    }
}