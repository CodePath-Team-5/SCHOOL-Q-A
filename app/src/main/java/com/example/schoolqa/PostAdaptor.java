package com.example.schoolqa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHholder>{

    private Context context;
    private List<Post> posts;

    public PostAdaptor(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHholder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHholder extends RecyclerView.ViewHolder{

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvQuestion;
        public ViewHholder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvQuestion.setText(post.getQuestion());

            ParseFile image = post.getImage();
            if (image != null) {
                // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
                Glide.with(context).load(post.getUser().getParseFile("user_image").getUrl()).into(ivImage);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_user)).into(ivImage);
            }
        }
    }
}
