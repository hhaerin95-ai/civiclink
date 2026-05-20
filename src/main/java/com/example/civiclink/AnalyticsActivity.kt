package com.example.civiclink

import android.content.Intent // Pastikan import Intent ada
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton // Pastikan import ImageButton ada
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*
import kotlin.math.roundToInt

class AnalyticsActivity : AppCompatActivity() {

    // Deklarasi pemboleh ubah
    private lateinit var progressBar: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var tvTotalComplaints: TextView
    private lateinit var tvCompletedPercentage: TextView
    private lateinit var pieChartStatus: PieChart
    private lateinit var dbRef: DatabaseReference
    private lateinit var btnHome: ImageButton // Deklarasi untuk butang Home

    // Kod yang menyebabkan ralat telah dipadam dari sini.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        // Pautkan view dari XML
        progressBar = findViewById(R.id.progressBar)
        contentLayout = findViewById(R.id.contentLayout)
        tvTotalComplaints = findViewById(R.id.tvTotalComplaints)
        tvCompletedPercentage = findViewById(R.id.tvCompletedPercentage)
        pieChartStatus = findViewById(R.id.pieChartStatus)
        btnHome = findViewById(R.id.btnHome) // Pautkan butang Home

        // --- PINDAHKAN LOGIK BUTANG HOME KE SINI ---
        btnHome.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        // --- TAMAT LOGIK BUTANG HOME ---

        // Mulakan proses muat turun data
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        contentLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("complaint")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(this@AnalyticsActivity, "No complaint data found.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    return
                }

                val complaintList = mutableListOf<ComplaintModel>()
                for (complaintSnap in snapshot.children) {
                    val complaint = complaintSnap.getValue(ComplaintModel::class.java)
                    complaint?.let { complaintList.add(it) }
                }

                // Proses data dan papar statistik
                processAndDisplayStats(complaintList)

                // Paparkan kandungan dan sembunyikan progress bar
                contentLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@AnalyticsActivity, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun processAndDisplayStats(complaints: List<ComplaintModel>) {
        val totalComplaints = complaints.size
        val completedComplaints = complaints.count { it.status == "Completed" }
        val completedPercentage = if (totalComplaints > 0) {
            (completedComplaints.toDouble() / totalComplaints * 100).roundToInt()
        } else {
            0
        }

        // 1. Kemas kini kad ringkasan
        tvTotalComplaints.text = totalComplaints.toString()
        tvCompletedPercentage.text = "$completedPercentage%"

        // 2. Sediakan data untuk carta pai
        val statusCounts = complaints.groupingBy { it.status ?: "Unknown" }.eachCount()
        val pieEntries = ArrayList<PieEntry>()
        for ((status, count) in statusCounts) {
            pieEntries.add(PieEntry(count.toFloat(), status))
        }

        setupPieChart(pieEntries)
    }

    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "")
        // Guna templat warna yang menarik
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)

        pieChartStatus.data = data
        pieChartStatus.description.isEnabled = false
        pieChartStatus.centerText = "Status Breakdown"
        pieChartStatus.setCenterTextSize(16f)
        pieChartStatus.animateY(1000) // Animasi
        pieChartStatus.invalidate() // Paparkan carta
    }
}
