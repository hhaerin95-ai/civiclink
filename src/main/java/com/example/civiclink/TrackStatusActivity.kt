package com.example.civiclink

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TrackStatusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoComplaints: TextView
    private lateinit var complaintList: ArrayList<ComplaintModel>
    private lateinit var adapter: TrackStatusAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_status)

        recyclerView = findViewById(R.id.trackStatusRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvNoComplaints = findViewById(R.id.tvNoComplaints)

        recyclerView.layoutManager = LinearLayoutManager(this)
        complaintList = arrayListOf()
        adapter = TrackStatusAdapter(complaintList)
        recyclerView.adapter = adapter

        firebaseAuth = FirebaseAuth.getInstance()

        // Logik Butang Home
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

        loadUserComplaints()
    }

    private fun loadUserComplaints() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvNoComplaints.visibility = View.GONE

        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        dbRef = FirebaseDatabase.getInstance().getReference("complaint")
        val query = dbRef.orderByChild("userId").equalTo(currentUserId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                if (snapshot.exists()) {
                    for (complaintSnap in snapshot.children) {
                        complaintSnap.getValue(ComplaintModel::class.java)?.let {
                            complaintList.add(it)
                        }
                    }
                    complaintList.reverse() // Tunjuk yang terbaru dahulu
                    adapter.notifyDataSetChanged()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvNoComplaints.visibility = View.VISIBLE
                }
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@TrackStatusActivity, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
