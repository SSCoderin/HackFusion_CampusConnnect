package com.hf.connect.verification

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hf.connect.MainActivity
import com.hf.connect.R
import com.hf.connect.SignUpActivity

class VerificationActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var idPhotoImageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var submitButton: Button

    private var selectedImageUri: Uri? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        emailEditText = findViewById(R.id.emailEditText)
        idPhotoImageView = findViewById(R.id.idPhotoImageView)
        uploadButton = findViewById(R.id.uploadButton)
        submitButton = findViewById(R.id.submitButton)

        uploadButton.setOnClickListener {
            openGallery()
        }

        submitButton.setOnClickListener {
            submitVerification()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    private fun submitVerification() {
        val email = emailEditText.text.toString().trim()
        if (!email.endsWith("@sggs.ac.in")) {
            Toast.makeText(this, "Please enter a valid email ending with @sggs.ac.in", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an ID photo", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if email is already verified
        checkEmailVerification(email)
    }

    private fun checkEmailVerification(email: String) {
        val verifiedRef = firestore.collection("verified").document(email)
        verifiedRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Email already verified, navigate to MainActivity or HomeFragment
                    navigateToMainOrHome()
                } else {
                    // Email not verified, submit verification
                    uploadVerificationData(email)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to check email verification: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadVerificationData(email: String) {
        val verificationRef = firestore.collection("verification").document(email)

        // Upload the email, ID photo, and toast message to Firestore
        val verificationData = hashMapOf(
            "email" to email,
            // Upload the ID photo (selectedImageUri) here
            "toastMessage" to "Your email ID and ID card are sent for verification. You will receive an email shortly. Once verified, you will be able to access the app."
        )

        verificationRef.set(verificationData)
            .addOnSuccessListener {
                // Display confirmation dialog
                showConfirmationDialog()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload verification data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Verification Submitted")
            setMessage("Your email ID and ID card are sent for verification. You will receive an email shortly. Once verified, you will be able to access the app.")
            setPositiveButton("OK") { _, _ ->
                // Navigate to MainActivity or HomeFragment
                navigateToMainOrHome()
            }
            setCancelable(false) // Prevent closing the dialog by tapping outside or pressing back button
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun navigateToMainOrHome() {
        // Check if the user has already completed the sign-up process once
        if (isUserSignedUp()) {
            // If already signed up once, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // If signing up for the first time, navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun isUserSignedUp(): Boolean {
        // Retrieve the flag indicating whether the user has signed up from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isSignedUp", false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            idPhotoImageView.setImageURI(selectedImageUri)
            idPhotoImageView.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
    }
}
