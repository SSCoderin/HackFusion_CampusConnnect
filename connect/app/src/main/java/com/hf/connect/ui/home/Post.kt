package com.hf.connect.ui.home
data class Post(
    val postId: String,
    val userId: String,
    val content: String,
    val username: String,
    val likeCount: Int,
    val commentCount: Int,
    val imageUrl: String = "",
    val description: String, // Add the description property
    val profileImageUrl: String
)
