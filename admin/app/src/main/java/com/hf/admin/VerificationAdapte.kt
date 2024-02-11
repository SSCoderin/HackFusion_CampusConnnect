package com.hf.admin
// VerificationAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class VerificationAdapter(
    private val verificationList: List<VerificationItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<VerificationAdapter.VerificationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(email: String)
    }

    inner class VerificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        private val verifyButton: Button = itemView.findViewById(R.id.verifyButton)

        init {
            verifyButton.setOnClickListener {
                val email = verificationList[adapterPosition].email
                listener.onItemClick(email)
            }
        }

        fun bind(item: VerificationItem) {
            emailTextView.text = item.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.verification_item, parent, false)
        return VerificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VerificationViewHolder, position: Int) {
        holder.bind(verificationList[position])
    }

    override fun getItemCount(): Int {
        return verificationList.size
    }
}
