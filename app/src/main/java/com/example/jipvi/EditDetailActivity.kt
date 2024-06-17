package com.example.jipvi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jipvi.databinding.ActivityEditDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var btnSave: Button
    private lateinit var binding:ActivityEditDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        etHeight = binding.etHeight
        etWeight = binding.etWeight
        btnSave = binding.btnSave

        // Fetch and display the current height and weight
        fetchCurrentDetails()

        btnSave.setOnClickListener {
            val height = etHeight.text.toString().toDoubleOrNull()
            val weight = etWeight.text.toString().toDoubleOrNull()

            if (height != null && weight != null) {
                saveUserDetails(height, weight)
            } else {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCurrentDetails() {
        val user = auth.currentUser
        if (user != null) {
            val docRef = firestore.collection("users").document(user.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val height = document.getDouble("height") ?: 0.0
                        val weight = document.getDouble("weight") ?: 0.0

                        if (height != 0.0) etHeight.setText(height.toString())
                        if (weight != 0.0) etWeight.setText(weight.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch current details: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserDetails(height: Double, weight: Double) {
        val user = auth.currentUser
        if (user != null) {
            val userDetails = hashMapOf(
                "height" to height,
                "weight" to weight
            )
            firestore.collection("users").document(user.uid)
                .set(userDetails)
                .addOnSuccessListener {
                    Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditDetailActivity , MainActivity::class.java))
                    finish() // Close the activity and go back to MainActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}