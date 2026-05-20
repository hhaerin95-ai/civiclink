package com.example.civiclink

// Jadikan kelas ini sebagai data class untuk kemudahan
data class ComplaintModel(
    // --- INI PEMBETULANNYA ---
    var complaintId: String? = null, // Tukar dari 'val' ke 'var'

    // Pastikan semua nama pembolehubah ini sepadan dengan nama medan di Firebase
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = "",
    val status: String? = null,
    val userId: String? = null,
    val timestamp: Long? = null
)
