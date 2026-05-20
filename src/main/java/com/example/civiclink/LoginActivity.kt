package com.example.civiclink

// --- INI BAHAGIAN 'IMPORT' YANG HILANG ---
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
// --- TAMAT BAHAGIAN 'IMPORT' ---

class LoginActivity : AppCompatActivity() {

    // Kredensial Admin yang ditetapkan
    private val ADMIN_EMAIL = "admin@civiclink.com"
    private val ADMIN_PASSWORD = "admin123"

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        // --- LOGIK "STAY LOGGED IN" PALING KUKUH ---
        // Semak SharedPreferences DAHULU untuk menentukan aliran
        val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userType = sharedPref.getString("userType", null)

        if (isLoggedIn) {
            // Jika ditanda sebagai admin, terus ke Dashboard tanpa periksa Firebase
            if (userType == "admin") {
                navigateTo(Dashboard::class.java)
                return // Hentikan pelaksanaan onCreate
            }
            // Jika ditanda sebagai pengguna biasa, SAHKAN dengan Firebase
            else if (userType == "user" && firebaseAuth.currentUser != null) {
                navigateTo(HomeActivity::class.java)
                return // Hentikan pelaksanaan onCreate
            }
        }
        // Jika tiada sesi sah, tunjukkan skrin log masuk
        setupLoginScreen()
    }

    // Fungsi untuk memaparkan skrin log masuk
    private fun setupLoginScreen() {
        setContentView(R.layout.activity_login) // Baris ini kini akan berfungsi
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, password)
        }

        btnRegister.setOnClickListener {
            // Pastikan anda ada RegisterActivity.kt dan activity_register.xml
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        // Log keluar mana-mana pengguna sedia ada untuk sesi yang bersih
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }

        if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            handleAdminLogin()
        } else {
            handleUserLogin(email, password)
        }
    }

    private fun handleAdminLogin() {
        // LOGIK ADMIN YANG DIASINGKAN: HANYA GUNA SharedPreferences
        val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userType", "admin")
            apply()
        }

        Toast.makeText(this, "Admin Login successful!", Toast.LENGTH_SHORT).show()
        navigateTo(Dashboard::class.java)
    }

    private fun handleUserLogin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Log masuk berjaya, simpan status ke SharedPreferences
                    val sharedPref = getSharedPreferences("CivicLinkPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("userType", "user")
                        apply()
                    }

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateTo(HomeActivity::class.java)
                } else {
                    // Log masuk gagal, beri mesej yang jelas
                    Toast.makeText(this, "Invalid email or password. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Fungsi bantuan untuk navigasi yang bersih
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
