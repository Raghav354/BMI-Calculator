package com.example.jipvi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jipvi.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var tvBmi: TextView
    private lateinit var tvBmiCategory: TextView
    private lateinit var btnEdit: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


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

        btnEdit.setOnClickListener {
            startActivity(Intent(this, EditDetailActivity::class.java))
            finish()
        }

        fetchUserData()

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