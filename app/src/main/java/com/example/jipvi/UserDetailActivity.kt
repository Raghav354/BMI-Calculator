package com.example.jipvi

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jipvi.databinding.ActivityUserDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDetailActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etDob: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        etName = binding.etName
        etDob = binding.etDob
        etHeight = binding.etHeight
        etWeight = binding.etWeight

        binding.btnSaveDetails.setOnClickListener {
            saveUserDetails()
        }

    }

    private fun saveUserDetails() {
        val name = etName.text.toString().trim()
        val dob = etDob.text.toString().trim()
        val height = etHeight.text.toString().trim()
        val weight = etWeight.text.toString().trim()

        if (validateInput(name, dob, height, weight)) {
            val heightValue = height.toFloat()
            val weightValue = weight.toFloat()
            val userDetails = hashMapOf(
                "name" to name,
                "dob" to dob,
                "height" to heightValue,
                "weight" to weightValue
            )

            auth.currentUser?.let { user ->
                firestore.collection("users").document(user.uid)
                    .set(userDetails)
                    .addOnSuccessListener {
                        saveWeightData(user.uid, weightValue)
                        Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun saveWeightData(userId: String, weight: Float) {
        val weightData = hashMapOf(
            "weight" to weight,
            "timestamp" to Date()
        )

        firestore.collection("users").document(userId)
            .collection("weights")
            .add(weightData)
            .addOnSuccessListener {
                // Weight data added successfully
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save weight data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(name: String, dob: String, height: String, weight: String): Boolean {
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return false
        }

        if (dob.isEmpty()) {
            etDob.error = "Date of Birth is required"
            etDob.requestFocus()
            return false
        } else if (!isValidDate(dob)) {
            etDob.error = "Invalid Date of Birth format (DD/MM/YYYY)"
            etDob.requestFocus()
            return false
        }

        if (height.isEmpty()) {
            etHeight.error = "Height is required"
            etHeight.requestFocus()
            return false
        } else if (!height.toFloatOrNull()?.isFinite()!!) {
            etHeight.error = "Height must be a valid number"
            etHeight.requestFocus()
            return false
        }

        if (weight.isEmpty()) {
            etWeight.error = "Weight is required"
            etWeight.requestFocus()
            return false
        } else if (!weight.toFloatOrNull()?.isFinite()!!) {
            etWeight.error = "Weight must be a valid number"
            etWeight.requestFocus()
            return false
        }

        return true
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

}