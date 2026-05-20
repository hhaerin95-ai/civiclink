package com.example.civiclink

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLogin: TextView

    // Isytiharkan Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        // Inisialisasi Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            registerUser() // Panggil fungsi pendaftaran
        }

        tvLogin.setOnClickListener {
            // Kembali ke LoginActivity jika pengguna sudah ada akaun
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validasi input (kod anda sedia ada sudah bagus)
        if (name.isEmpty()) {
            etName.error = "Please enter your name"
            etName.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etEmail.error = "Please enter your email"
            etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            etEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Please enter password"
            etPassword.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        // --- INILAH BAHAGIAN YANG DIUBAH ---
        // Gantikan SharedPreferences dengan pendaftaran Firebase

        // Tunjukkan mesej "Registering..."
        Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Pendaftaran berjaya
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()

                    // Bawa pengguna ke halaman log masuk
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish() // Tutup halaman pendaftaran
                } else {
                    // Pendaftaran gagal, tunjukkan mesej ralat dari Firebase
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
