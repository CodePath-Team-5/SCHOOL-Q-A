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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;


    public CommentAdapter(Context context, List<Comment> comments) {
        this.commentList = comments;
        this.context = context;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        commentList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> list) {
        commentList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvComment;
        private TextView tvTimestamp;
        private ImageView ivUserImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.commentItem_username);
            tvTimestamp = itemView.findViewById(R.id.comentItem_timestamp);
            ivImage = itemView.findViewById(R.id.commentItem_image);
            tvComment = itemView.findViewById(R.id.commentItem_comment);
            ivUserImage = itemView.findViewById(R.id.commentItem_userImage);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getUser().getUsername());
            String time = TimeFormatter.getTimeDifference(comment.getCreatedAt().toString());
            tvTimestamp.setText(time);
            tvComment.setText(comment.getContent());

            ParseFile user_image = comment.getUser().getParseFile("user_image");
            ParseFile cmt_image = comment.getImage();
            //add user image
            if (user_image != null) {
                Glide.with(context).load(user_image.getUrl()).into(ivUserImage);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_user)).into(ivUserImage);
            }
            //add comment image
            if (cmt_image != null) {
                // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
                Glide.with(context).load(cmt_image.getUrl()).into(ivImage);
            }
        }
    }
}
