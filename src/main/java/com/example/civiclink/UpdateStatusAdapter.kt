package com.example.civiclink

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.ContextCompat

class UpdateStatusAdapter(private val context: Context, private val complaintList: ArrayList<ComplaintModel>) :
    RecyclerView.Adapter<UpdateStatusAdapter.ViewHolder>() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("complaint")

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvComplaintTitle)
        val description: TextView = view.findViewById(R.id.tvComplaintDescription)
        // This might also cause an error if the ID is different. Let's fix the layout first.
        val currentStatus: TextView = view.findViewById(R.id.tvStatus) // The correct ID in item_update_complaint.xml
        val statusSpinner: Spinner = view.findViewById(R.id.spinnerStatus)
        val updateButton: Button = view.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // --- FIX IS HERE ---
        // Change R.layout.itemcomplaint to the correct layout for updating status.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemcomplaint, parent, false) // Use the layout with the Spinner
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val complaint = complaintList[position]

        holder.title.text = complaint.title
        holder.description.text = complaint.description
        holder.currentStatus.text = "Status: ${complaint.status}"

        val statusAdapter = ArrayAdapter.createFromResource(
            context,
            R.array.complaint_statuses,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.statusSpinner.adapter = adapter
        }

        val currentStatusPosition = statusAdapter.getPosition(complaint.status)
        if (currentStatusPosition >= 0) {
            holder.statusSpinner.setSelection(currentStatusPosition)
        }

        holder.updateButton.setOnClickListener {
            val newStatus = holder.statusSpinner.selectedItem.toString()
            val complaintId = complaint.complaintId

            if (complaintId != null) {
                updateComplaintStatus(complaintId, newStatus)
            } else {
                Toast.makeText(context, "Error: Complaint ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    private fun updateComplaintStatus(complaintId: String, newStatus: String) {
        databaseReference.child(complaintId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Status updated successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, Dashboard::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show()
            }
    }
}
