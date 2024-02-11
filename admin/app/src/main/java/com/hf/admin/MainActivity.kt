package com.hf.admin

import VerificationAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), VerificationAdapter.OnItemClickListener {

    private lateinit var verificationRecyclerView: RecyclerView
    private lateinit var verificationAdapter: VerificationAdapter
    private val verificationList = mutableListOf<VerificationItem>()

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verificationRecyclerView = findViewById(R.id.verificationRecyclerView)
        verificationAdapter = VerificationAdapter(verificationList, this)
        verificationRecyclerView.adapter = verificationAdapter
        verificationRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch data from verification folder
        fetchDataFromVerificationFolder()
    }

    private fun fetchDataFromVerificationFolder() {
        val verificationRef = firestore.collection("verification")
        verificationRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val email = document.id // Email is document ID
                    val imageUrl = document.getString("imageUrl") ?: "" // Provide a default value if imageUrl is null
                    // Add verification item to list
                    verificationList.add(VerificationItem(email, imageUrl))
                }
                // Notify adapter about data changes
                verificationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.e(TAG, "Error fetching verification data: ${e.message}")
            }
    }

    override fun onItemClick(email: String) {
        // Move the item to the verified folder
        moveItemToVerifiedFolder(email)
    }

    private fun moveItemToVerifiedFolder(email: String) {
        // Remove item from the verification folder
        val verificationRef = firestore.collection("verification").document(email)
        verificationRef.delete()
            .addOnSuccessListener {
                // Add item to the verified folder
                val verifiedRef = firestore.collection("verified").document(email)
                verifiedRef.set(mapOf("email" to email))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Verification complete for $email", Toast.LENGTH_SHORT).show()
                        // Refresh the list
                        verificationList.clear()
                        fetchDataFromVerificationFolder()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error moving item to verified folder: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting item from verification folder: ${e.message}")
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
