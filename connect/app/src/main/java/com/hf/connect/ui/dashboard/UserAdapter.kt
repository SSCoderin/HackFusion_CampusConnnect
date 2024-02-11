package com.hf.connect.ui.dashboard
// UserAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.hf.connect.R

class UserAdapter(
    private val context: Context,
    private val userList: List<com.hf.connect.ui.home.User>,
    private val listener: OnConnectClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val connectButton: Button = itemView.findViewById(R.id.connectButton)
    }

    interface OnConnectClickListener {
        fun onConnectClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.usernameTextView.text = user.username
        holder.connectButton.setOnClickListener {
            listener.onConnectClick(position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
