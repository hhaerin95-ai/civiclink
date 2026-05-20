package com.example.civiclink

import android.content.Context
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

class ViewComplaints : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvHeaderTitle: TextView
    private lateinit var complaintList: ArrayList<ComplaintModel>
    private lateinit var adapter: ComplaintAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_complaints)

        // Pautkan semua elemen dari XML (ID ini semua sudah betul)
        recyclerView = findViewById(R.id.complaintsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)

        // Konfigurasi RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Sediakan data dan adapter
        complaintList = arrayListOf()
        // Guna ComplaintAdapter untuk paparan pengguna/admin
        adapter = ComplaintAdapter(complaintList)
        recyclerView.adapter = adapter

        // Inisialisasi Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("complaint")

        // Fungsi butang Home (hanya tutup halaman semasa)
        btnHome.setOnClickListener {
            finish()
        }

        // Muat turun data
        loadComplaintData()
    }

    private fun loadComplaintData() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
        val userType = sharedPref.getString("userType", "user")

        tvHeaderTitle.text = if (userType == "admin") "All Complaints" else "My Complaints"

        // --- INI PEMBETULAN PALING KRITIKAL ---
        val query: Query

        if (userType == "admin") {
            // UNTUK ADMIN: Dapatkan SEMUA data tanpa menyusunnya di Firebase.
            // Kita akan susun kemudian di dalam aplikasi. Ini lebih selamat.
            query = dbRef
        } else {
            // UNTUK PENGGUNA BIASA: Dapatkan hanya aduan miliknya.
            val currentUserId = firebaseAuth.currentUser?.uid
            if (currentUserId == null) {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return
            }
            // Query ini selamat kerana ia tidak bergantung pada 'timestamp'.
            query = dbRef.orderByChild("userId").equalTo(currentUserId)
        }
        // --- TAMAT PEMBETULAN ---

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                if (snapshot.exists()) {
                    for (complaintSnap in snapshot.children) {
                        val complaintData = complaintSnap.getValue(ComplaintModel::class.java)
                        // Pastikan complaintId diambil dari 'key' snapshot untuk keselamatan
                        complaintData?.complaintId = complaintSnap.key
                        complaintData?.let { complaintList.add(it) }
                    }

                    // Susun senarai di sini (dalam aplikasi), bukan di Firebase.
                    // Ini akan menyusun aduan dengan 'timestamp' paling baru di atas.
                    // Aduan tanpa 'timestamp' akan diletak di bawah.
                    complaintList.sortByDescending { it.timestamp }

                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ViewComplaints, "No complaints found.", Toast.LENGTH_SHORT).show()
                }

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ViewComplaints, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
