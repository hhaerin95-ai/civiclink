package com.example.civiclink

import android.content.Intent // <-- Import Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UpdateStatus : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var complaintList: ArrayList<ComplaintModel>
    private lateinit var adapter: UpdateStatusAdapter
    private lateinit var dbRef: DatabaseReference

    // The code that was causing the error has been moved from here.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the layout you created
        setContentView(R.layout.activity_update_status)

        // --- FIX STARTS HERE ---

        // 1. Initialize the button after setContentView
        val btnHome = findViewById<ImageButton>(R.id.btnHome)

        // 2. Set the click listener for the button
        btnHome.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // --- FIX ENDS HERE ---


        // Link UI elements from the layout
        recyclerView = findViewById(R.id.rvComplaints)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Prepare an empty list to store complaint data
        complaintList = arrayListOf()

        // Prepare the adapter
        adapter = UpdateStatusAdapter(this, complaintList)
        recyclerView.adapter = adapter

        // Get complaint data from Firebase
        getComplaintData()
    }

    private fun getComplaintData() {
        // Show ProgressBar while loading data
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        // Reference to the 'complaint' node in Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("complaint")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the old list before adding new data
                complaintList.clear()
                if (snapshot.exists()) {
                    for (complaintSnap in snapshot.children) {
                        // Get complaint data and convert it to a ComplaintModel object
                        val complaintData = complaintSnap.getValue(ComplaintModel::class.java)
                        if (complaintData != null) {
                            complaintList.add(complaintData)
                        }
                    }
                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged()
                }

                // Hide ProgressBar and show RecyclerView
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide ProgressBar if an error occurs
                progressBar.visibility = View.GONE
                // Show an error message
                Toast.makeText(this@UpdateStatus, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
