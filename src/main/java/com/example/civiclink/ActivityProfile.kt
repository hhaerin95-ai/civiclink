package com.example.civiclink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button // <<< ADD THIS IMPORT
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ActivityProfile : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Find views by their IDs
        val tvUserEmail: TextView = findViewById(R.id.tvUserEmail)
        val btnClose: ImageButton = findViewById(R.id.btnClose)
        // --- THIS IS THE FIX ---
        val btnLogout: Button = findViewById(R.id.btnlogout) // Find the logout button

        // Set the close button functionality
        btnClose.setOnClickListener {
            finish() // This will close the current activity and go back
        }

        // --- MOVE THE LOGOUT LOGIC HERE ---
        // Set the click listener for the "Logout" button
        btnLogout.setOnClickListener {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Clear SharedPreferences
            val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // Go back to the Login page
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish() // Close the ProfileActivity so the user can't go back
        }

        // Load and display user data
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // If user is logged in, display their email
            val email = currentUser.email
            val tvUserEmail: TextView = findViewById(R.id.tvUserEmail)
            tvUserEmail.text = email
        } else {
            // Handle case where user is not logged in
            // Redirecting to LoginActivity is handled by the logout button now
        }

        // The logout logic has been moved to onCreate, so we remove it from here.
    }
}
