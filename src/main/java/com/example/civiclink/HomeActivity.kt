package com.example.civiclink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var btnSubmitComplaint: Button
    private lateinit var btnTrackStatus: Button
    private lateinit var btnMyComplaints: Button
    private lateinit var btnProfile: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Anda boleh komen baris ini jika ia menyebabkan isu
        setContentView(R.layout.activity_home)

        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint)
        btnTrackStatus = findViewById(R.id.btnTrackStatus)
        btnMyComplaints = findViewById(R.id.btnMyComplaints)
        btnProfile = findViewById(R.id.btnProfile)


        btnSubmitComplaint.setOnClickListener {
            // Sila pastikan nama kelas adalah betul (e.g., SubmitComplaintActivity)
            val intent = Intent(this, SubmitComplaintActivity::class.java)
            startActivity(intent)
        }

        btnTrackStatus.setOnClickListener {
            // Sila pastikan nama kelas adalah betul
            val intent = Intent(this, TrackStatusActivity::class.java)
            startActivity(intent)
        }

        btnMyComplaints.setOnClickListener {
            val intent = Intent(this, ViewComplaints::class.java)
            startActivity(intent)
        }


        val btnProfile = findViewById<Button>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
