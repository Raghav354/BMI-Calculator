package com.example.jipvi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jipvi.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var tvBmi: TextView
    private lateinit var tvBmiCategory: TextView
    private lateinit var btnEdit: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var lineChart: LineChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvBmi = binding.tvBmi
        tvBmiCategory = binding.tvBmiCategory
        btnEdit = binding.btnEdit
        lineChart = binding.lineChart

        btnEdit.setOnClickListener {
            startActivity(Intent(this, EditDetailActivity::class.java))
            finish()
        }

        fetchUserData()
        fetchWeightHistory()

    }


    private fun fetchWeightHistory() {
        val user = auth.currentUser
        if (user != null) {
            val oneWeekAgo = Calendar.getInstance()
            oneWeekAgo.add(Calendar.DAY_OF_YEAR, -7)

            firestore.collection("users").document(user.uid).collection("weights")
                .whereGreaterThanOrEqualTo("timestamp", oneWeekAgo.time)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val entries = mutableListOf<Entry>()

                    for (document in documents) {
                        val weight = document.getDouble("weight") ?: 0.0
                        val timestamp = document.getDate("timestamp")
                        if (timestamp != null) {
                            entries.add(Entry(timestamp.time.toFloat(), weight.toFloat()))
                        }
                    }

                    if (entries.isNotEmpty()) {
                        val dataSet = LineDataSet(entries, "Weight").apply {
                            color = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
                            valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark)
                            lineWidth = 2f
                            setDrawCircles(true)
                            setCircleColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
                            setCircleRadius(5f)
                            setDrawValues(false)
                            setDrawHighlightIndicators(true)
                            highLightColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                            setDrawFilled(true)
                            fillDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.chart_fill)
                        }

                        val lineData = LineData(dataSet)
                        lineChart.data = lineData

                        val xAxis = lineChart.xAxis
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.valueFormatter = DateValueFormatter()
                        xAxis.setDrawGridLines(false)
                        xAxis.granularity = 1f

                        val yAxisLeft = lineChart.axisLeft
                        yAxisLeft.setDrawGridLines(true)
                        yAxisLeft.granularity = 1f

                        val yAxisRight = lineChart.axisRight
                        yAxisRight.isEnabled = false

                        lineChart.description.isEnabled = false
                        lineChart.legend.isEnabled = true

                        lineChart.invalidate() // Refresh the chart
                    } else {
                        Toast.makeText(this, "No weight data found for the past week", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch weight data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        if (user != null) {
            val docRef = firestore.collection("users").document(user.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val height = document.getDouble("height") ?: 0.0
                        val weight = document.getDouble("weight") ?: 0.0

                        if (height != 0.0 && weight != 0.0) {
                            val bmi = calculateBMI(height, weight)
                            val bmiCategory = getBMICategory(bmi)

                            tvBmi.text = "BMI: %.2f".format(bmi)
                            tvBmiCategory.text = "Category: $bmiCategory"
                        } else {
                            Toast.makeText(this, "Height or Weight is missing", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun calculateBMI(height: Double, weight: Double): Double {
        val heightInMeters = height / 100.0 // Assuming height is in cm
        return weight / (heightInMeters * heightInMeters)
    }

    private fun getBMICategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
    }

}