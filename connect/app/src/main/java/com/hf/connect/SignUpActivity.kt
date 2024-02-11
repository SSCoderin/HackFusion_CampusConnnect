package com.hf.connect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val branchEditText = findViewById<EditText>(R.id.editTextBranch)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)
        val signInButton = findViewById<Button>(R.id.buttonSignIn)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val branch = branchEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || branch.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                signUp(name, branch, email, password)
            }
        }

        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun signUp(name: String, branch: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-up user's information
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        // Save user details in Firestore
                        val userMap = hashMapOf(
                            "name" to name,
                            "branch" to branch,
                            "email" to email
                        )
                        firestore.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Sign up successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Save user details in Realtime Database "account file"
                                val accountRef =
                                    FirebaseDatabase.getInstance().getReference("account")
                                val userDetails = hashMapOf(
                                    "name" to name,
                                    "branch" to branch,
                                    "email" to email
                                )
                                accountRef.child(uid).setValue(userDetails)
                                    .addOnSuccessListener {
                                        // Redirect to MainActivity
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Failed to save user details in account file: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to save user details in Firestore: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Sign up failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
