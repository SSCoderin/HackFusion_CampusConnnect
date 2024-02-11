package com.hf.connect.ui.dashboard
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.hf.connect.R


class DashboardFragment : Fragment(), UserAdapter.OnConnectClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fetchUsersFromFirestore()
    }

    private fun fetchUsersFromFirestore() {
        // Query Firestore to fetch users excluding the current user
        firestore.collection("users")
            .whereNotEqualTo("uid", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<com.hf.connect.ui.home.User>()
                for (document in result) {
                    val userId = document.getString("uid") ?: ""
                    val username = document.getString("username") ?: ""
                    userList.add(com.hf.connect.ui.home.User(userId, username))
                }
                userAdapter = UserAdapter(requireContext(), userList, this)
                recyclerView.adapter = userAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching users: $exception")
                Toast.makeText(requireContext(), "Error fetching users", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onConnectClick(position: Int) {
        // Handle connect button click here
        // You can implement the logic to connect with the user at the given position
        // For example, you can show a confirmation dialog, send a friend request, etc.
    }

    companion object {
        private const val TAG = "FriendFragment"
    }
}
