import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hf.admin.R
import com.hf.admin.VerificationItem

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
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)

        init {
            verifyButton.setOnClickListener {
                val email = verificationList[adapterPosition].email
                listener.onItemClick(email)
            }
        }

        fun bind(item: VerificationItem) {
            emailTextView.text = item.email
            // Load image from girebade folder
            Glide.with(itemView.context)
                .load("file:///android_asset/girebade/${item.imageName}")
                .into(profileImageView)
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
