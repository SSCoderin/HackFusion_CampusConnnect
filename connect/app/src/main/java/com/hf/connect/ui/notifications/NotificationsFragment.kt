package com.hf.connect.ui.notifications

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hf.connect.databinding.FragmentNotificationsBinding
import java.util.*

class NotificationsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // Get the image URI from the result
            val data = result.data
            selectedImageUri = data?.data
            // Set the image to ImageView
            binding.profileImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Fetch user details if verified
        fetchUserDetails()

        // Setup click listener for upload photo button
        binding.uploadPhotoTextView.setOnClickListener {
            // Open image picker
            openImagePicker()
        }

        // Setup listener for anonymous account toggle switch
        binding.accountToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Activate anonymous account
                activateAnonymousAccount()
            } else {
                // Deactivate anonymous account
                deactivateAnonymousAccount()
            }
        }

        return root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Image"))
    }

    private fun fetchUserDetails() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            firestore.collection("verified").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // User is verified, fetch user details from "users" collection
                        fetchVerifiedUserData(uid)
                    } else {
                        // User is not verified, fetch user details from "app_generated_users" collection
                        fetchGeneratedUserData(uid)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to fetch user verification status: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchVerifiedUserData(uid: String) {
        firestore.collection("account").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Display user details here
                    val userName = document.getString("name") ?: ""
                    val userBranch = document.getString("branch") ?: ""
                    val userEmail = document.getString("email") ?: ""

                    binding.userNameTextView.text = "Name: $userName"
                    binding.userBranchTextView.text = "Branch: $userBranch"
                    binding.userEmailTextView.text = "Email: $userEmail"
                } else {
                    Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchGeneratedUserData(uid: String) {
        firestore.collection("app_generated_users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Display user details here
                    val userName = document.getString("name") ?: ""
                    // other fields can be retrieved in a similar manner
                    binding.userNameTextView1.text = "Name: $userName"
                    // update other UI elements as needed
                } else {
                    Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun activateAnonymousAccount() {
        // Generate a random name for the anonymous account
        val generatedName = generateRandomName()

        // Save the generated name and activate the anonymous account
        saveGeneratedNameAndActivateAccount(generatedName)
    }

    private fun generateRandomName(): String {
        // Generate a random name using some logic, e.g., combining random words or characters
        // For simplicity, let's say we're generating a random name like "User123"
        val randomSuffix = (100..999).random()
        return "User$randomSuffix"
    }

    private fun saveGeneratedNameAndActivateAccount(name: String) {
        // Save the generated name to the database and activate the account
        // For simplicity, let's assume we save the name to the "app_generated_users" collection
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val userMap = hashMapOf(
                "name" to name
                // Other user details can be added here if needed
            )

            firestore.collection("app_generated_users").document(uid)
                .set(userMap)
                .addOnSuccessListener {
                    // Account activated, fetch and display details
                    fetchUserDetails()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to activate account: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deactivateAnonymousAccount() {
        // Deactivate the anonymous account, if needed
        // You can add your logic here
        Toast.makeText(requireContext(), "Deactivate anonymous account", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up binding
        _binding = null
    }
}
