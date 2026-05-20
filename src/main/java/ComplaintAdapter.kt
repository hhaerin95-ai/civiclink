package com.example.civiclink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Guna Glide untuk muat turun gambar dengan cekap
import androidx.core.content.ContextCompat // Tambah import ini

class ComplaintAdapter(private val complaintList: ArrayList<ComplaintModel>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    // ... (ComplaintViewHolder tidak perlu diubah) ...
    class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvComplaintTitle)
        val description: TextView = itemView.findViewById(R.id.tvComplaintDescription)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val image: ImageView = itemView.findViewById(R.id.ivComplaintImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_complaint_view, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaintList[position]
        val context = holder.itemView.context // dapatkan context

        holder.title.text = complaint.title
        holder.description.text = complaint.description
        holder.status.text = "Status: ${complaint.status}"

        // --- KEMAS KINI LOGIK STATUS ---
        // Tukar warna latar belakang status berdasarkan nilainya
        when (complaint.status) {
            "Pending" -> {
                holder.status.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_pending)
            }
            "In Progress" -> {
                // Pastikan anda ada drawable 'status_indicator_progress.xml'
                holder.status.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_progress)
            }
            "Completed" -> {
                // Pastikan anda ada drawable 'status_indicator_completed.xml'
                holder.status.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_completed)
            }
            else -> {
                holder.status.background = ContextCompat.getDrawable(context, R.drawable.status_indicator_pending)
            }
        }

        // Guna Glide untuk memaparkan gambar dari URL
        if (!complaint.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(complaint.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }
}
