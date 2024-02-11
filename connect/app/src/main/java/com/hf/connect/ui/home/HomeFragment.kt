package com.hf.connect.ui.home

import PostAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hf.connect.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPostsFromFirestore()
    }

    private fun fetchPostsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val postsCollection = firestore.collection("posts")

        postsCollection.get()
            .addOnSuccessListener { result ->
                val postsList = mutableListOf<Post>()
                for (document in result) {
                    val postId = document.id
                    val userId = document.getString("userId") ?: ""
                    val content = document.getString("content") ?: ""
                    val username = document.getString("username") ?: ""
                    val likeCount = document.getLong("likeCount")?.toInt() ?: 0
                    val commentCount = document.getLong("commentCount")?.toInt() ?: 0
                    val imageUrl = document.getString("imageUrl")
                    val description = document.getString("description") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl") ?: ""

                    // Check if imageUrl is not null before creating the Post object
                    if (imageUrl != null) {
                        val post = Post(postId, userId, content, username, likeCount, commentCount, imageUrl, description, profileImageUrl)
                        postsList.add(post)
                    } else {
                        Log.e(TAG, "Skipping post with missing imageUrl: $postId")
                    }
                }
                postAdapter = PostAdapter(requireContext(), postsList)
                recyclerView.adapter = postAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching posts: $exception")
            }
    }

    private fun incrementLikeCount(postId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val postRef = firestore.collection("posts").document(postId)
        postRef.update("likeCount", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d(TAG, "Like count incremented successfully for post: $postId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to increment like count for post: $postId", e)
                Toast.makeText(requireContext(), "Failed to like post. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
