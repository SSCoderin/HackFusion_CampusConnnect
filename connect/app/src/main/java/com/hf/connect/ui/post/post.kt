package com.hf.connect.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hf.connect.R

class post : Fragment() {

    private val PICK_IMAGE_REQUEST = 71

    private lateinit var imageViewSelectedImage: ImageView
    private lateinit var editTextPostDescription: EditText
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonCreatePost: Button

    private var selectedImageUri: Uri? = null

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_post, container, false)

        imageViewSelectedImage = rootView.findViewById(R.id.imageViewSelectedImage)
        editTextPostDescription = rootView.findViewById(R.id.editTextPostDescription)
        buttonSelectImage = rootView.findViewById(R.id.buttonSelectImage)
        buttonCreatePost = rootView.findViewById(R.id.buttonCreatePost)

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        buttonSelectImage.setOnClickListener {
            openGallery()
        }

        buttonCreatePost.setOnClickListener {
            createPost()
        }

        return rootView
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            imageViewSelectedImage.setImageURI(selectedImageUri)
            imageViewSelectedImage.visibility = View.VISIBLE
        }
    }

    private fun createPost() {
        val description = editTextPostDescription.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(context, "Enter post description", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri != null) {
            uploadImage(selectedImageUri!!)
        } else {
            uploadPostData(description, "")
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val filename = "${System.currentTimeMillis()}.jpg"
        val ref = storageReference.child("images/$filename")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    uploadPostData(editTextPostDescription.text.toString(), imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPostData(description: String, imageUrl: String) {
        // Create a map to represent the post data
        val postData = hashMapOf(
            "description" to description,
            "imageUrl" to imageUrl,
            "timestamp" to FieldValue.serverTimestamp() // Add a timestamp for sorting posts chronologically
            // Add more fields if needed
        )

        // Reference to the "posts" collection in Firestore
        val postsCollection = firestore.collection("posts")

        // Add the post data to Firestore
        postsCollection.add(postData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Post created successfully", Toast.LENGTH_SHORT).show()
                // Clear input fields and reset UI after successful post creation
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to create post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        // Clear the input fields (description and image)
        editTextPostDescription.setText("")
        selectedImageUri = null
        imageViewSelectedImage.setImageDrawable(null)
        imageViewSelectedImage.visibility = View.GONE
    }
}
