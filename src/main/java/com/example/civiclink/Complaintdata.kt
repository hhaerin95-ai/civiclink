package com.example.civiclink

import android.net.Uri

data class Complaintdata(
    val id: Int,
    val title: String,
    val description: String,
    val imageUri: Uri? = null,
    val status: String = "Pending"
)