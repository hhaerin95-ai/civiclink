package com.example.civiclink

// ... (semua import anda sudah betul) ...
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.R as MaterialR

class TrackStatusAdapter(private val complaintList: List<ComplaintModel>) :
    RecyclerView.Adapter<TrackStatusAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvComplaintTitle)
        val date: TextView = itemView.findViewById(R.id.tvComplaintDate)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val indicator: View = itemView.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track_status, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val complaint = complaintList[position]
        val context = holder.itemView.context

        holder.title.text = complaint.title

        // Format tarikh untuk paparan dengan selamat
        // --- INI ADALAH PEMBETULANNYA ---
        complaint.timestamp?.let { timestamp ->
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val submittedDate = sdf.format(Date(timestamp))
            holder.date.text = "Submitted on: $submittedDate"
        } ?: run {
            // Ini akan dijalankan jika timestamp adalah null
            holder.date.text = "Submitted on: N/A"
        }

        holder.status.text = complaint.status

        // ... (kod 'when' anda tidak perlu diubah dan sudah betul) ...
        when (complaint.status) {
            "Pending" -> {
                holder.indicator.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_pending)
                holder.status.setTextColor(MaterialColors.getColor(context, MaterialR.attr.colorPrimaryVariant, Color.GRAY))
            }
            "In Progress" -> {
                holder.indicator.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_progress)
                holder.status.setTextColor(MaterialColors.getColor(context, MaterialR.attr.colorPrimary, Color.BLUE))
            }
            "Completed" -> {
                holder.indicator.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_completed)
                holder.status.setTextColor(MaterialColors.getColor(context, MaterialR.attr.colorSecondary, Color.GREEN))
            }
            else -> {
                holder.indicator.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_pending)
                holder.status.setTextColor(MaterialColors.getColor(context, MaterialR.attr.colorPrimaryVariant, Color.GRAY))
            }
        }
    }

    override fun getItemCount() = complaintList.size
}
