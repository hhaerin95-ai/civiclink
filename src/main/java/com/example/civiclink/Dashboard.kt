package com.example.civiclink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Pautkan semua butang dari layout dengan ID yang betul
        // --- INI PEMBETULANNYA ---
        val btnViewComplaint = findViewById<Button>(R.id.btnViewComplaint) // Tukar dari R.id.btnView
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnAnalytics = findViewById<Button>(R.id.btnAnalytics)

        // Tetapkan fungsi untuk butang "View Complaint"
        // Kod ini kini akan berfungsi kerana btnViewComplaint merujuk kepada butang sebenar
        btnViewComplaint.setOnClickListener {
            val intent = Intent(this, ViewComplaints::class.java)
            startActivity(intent)
        }

        // Tetapkan fungsi untuk butang "Update Status"
        btnUpdate.setOnClickListener {
            val intent = Intent(this, UpdateStatus::class.java)
            startActivity(intent)
        }

        // Tetapkan fungsi untuk butang "Feedback"
        btnAnalytics.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }
        // Tetapkan fungsi untuk butang "Logout"
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            finish()
        }





    }
}
