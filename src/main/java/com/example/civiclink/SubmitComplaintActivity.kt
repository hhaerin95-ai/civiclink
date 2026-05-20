package com.example.civiclink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import android.widget.ImageButton

class SubmitComplaintActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var ivComplaintImage: ImageView
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnSubmit: Button
    // Tambah deklarasi untuk butang home di sini
    private lateinit var btnHome: ImageButton

    private var imageUri: Uri? = null

    // Rujukan Firebase
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth

    // Pelancar untuk memilih gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            ivComplaintImage.setImageURI(imageUri) // Tunjukkan gambar yang dipilih
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_complaint)

        // Pautkan elemen UI
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        ivComplaintImage = findViewById(R.id.ivComplaintImage)
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto)
        btnSubmit = findViewById(R.id.btnSubmit)
        // Pautkan butang home di sini
        btnHome = findViewById(R.id.btnHome)

        // Inisialisasi Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("complaint")
        storageRef = FirebaseStorage.getInstance().getReference("complaint_images")

        // --- PINDAHKAN LOGIK BUTANG HOME KE SINI ---
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        // --- TAMAT LOGIK BUTANG HOME ---

        // Fungsi untuk butang pilih gambar
        btnChoosePhoto.setOnClickListener {
            openGallery()
        }

        // Fungsi untuk butang hantar aduan
        btnSubmit.setOnClickListener {
            submitComplaint()
        }
    }

    // ... (fungsi-fungsi lain seperti openGallery, submitComplaint, dll. tidak berubah)

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun submitComplaint() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val currentUser = firebaseAuth.currentUser

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to submit.", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            uploadImageAndSaveData(title, description, currentUser.uid)
        } else {
            saveComplaintToDatabase(
                title,
                description,
                "",
                currentUser.uid
            )
        }
    }

    private fun uploadImageAndSaveData(title: String, description: String, userId: String) {
        Toast.makeText(this, "Submitting complaint...", Toast.LENGTH_LONG).show()
        btnSubmit.isEnabled = false

        val fileName = UUID.randomUUID().toString()
        val imageRef = storageRef.child(fileName)

        imageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveComplaintToDatabase(title, description, downloadUrl.toString(), userId)
                    }.addOnFailureListener {
                        handleFailure("Failed to get image URL.")
                    }
                }
                .addOnFailureListener {
                    handleFailure("Image upload failed.")
                }
        }
    }

    private fun saveComplaintToDatabase(title: String, description: String, imageUrl: String, userId: String) {
        val complaintId = dbRef.push().key ?: return

        val complaint = ComplaintModel(
            complaintId = complaintId,
            title = title,
            description = description,
            imageUrl = imageUrl,
            status = "Pending",
            userId = userId,
            timestamp = System.currentTimeMillis()
        )

        dbRef.child(complaintId).setValue(complaint)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                handleFailure("Failed to save data.")
            }
    }

    private fun handleFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        btnSubmit.isEnabled = true
    }
}
