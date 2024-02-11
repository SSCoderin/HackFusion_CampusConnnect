import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hf.connect.R
import com.hf.connect.ui.home.Post

class PostAdapter(private val context: Context, private val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView) // Add description TextView
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        val commentButton: ImageButton = itemView.findViewById(R.id.commentButton)
        val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
        val commentCountTextView: TextView = itemView.findViewById(R.id.commentCountTextView)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Load post image using Glide
        Glide.with(context)
            .load(post.imageUrl)
            .into(holder.postImage)

        // Bind other post data to views
        holder.usernameTextView.text = post.username
        holder.contentTextView.text = post.content
        holder.descriptionTextView.text = post.description // Bind description

        holder.likeCountTextView.text = post.likeCount.toString()
        holder.commentCountTextView.text = post.commentCount.toString()

        // Implement click listeners for like and comment buttons
        // You can add click listeners for likeButton and commentButton here
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
