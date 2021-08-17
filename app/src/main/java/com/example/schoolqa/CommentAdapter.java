package com.example.schoolqa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.overflowarchives.linkpreview.TelegramPreview;
import com.overflowarchives.linkpreview.ViewListener;
import com.overflowarchives.linkpreview.WhatsappPreview;
import com.parse.ParseFile;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private static final String tag ="CommentAdapter";
    private Context context;
    private List<Comment> commentList;
    private OnCommentItemListener itemListener;

    public CommentAdapter(Context context, List<Comment> comments, OnCommentItemListener listener ) {
        this.commentList = comments;
        this.context = context;
        this.itemListener = listener;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view, itemListener);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvComment;
        private TextView tvTimestamp;
        private ImageView ivUserImage;
        private TelegramPreview urlPreview;
        OnCommentItemListener commentItemListener;

        public ViewHolder(@NonNull View itemView, OnCommentItemListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.commentItem_username);
            tvTimestamp = itemView.findViewById(R.id.comentItem_timestamp);
            ivImage = itemView.findViewById(R.id.commentItem_image);
            tvComment = itemView.findViewById(R.id.commentItem_comment);
            ivUserImage = itemView.findViewById(R.id.commentItem_userImage);
            urlPreview = itemView.findViewById(R.id.link_preview_comment);

            this.commentItemListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getUser().getUsername());
            String time = TimeFormatter.getTimeDifference(comment.getCreatedAt().toString());
            tvTimestamp.setText(time);
            String content = comment.getContent();
            tvComment.setText(content);

            if (comment.isURL())
            {
                String url = "";
                if (content.contains("https://"))
                {
                    url = content;
                }
                else
                {
                    url = "https://" + content;
                }
                urlPreview.loadUrl(url, new ViewListener() {
                    @Override
                    public void onPreviewSuccess(boolean b) {

                    }

                    @Override
                    public void onFailedToLoad(Exception e) {

                    }
                });
            }

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
        @Override
        public void onClick(View v) {
                commentItemListener.onItemClick(v,getAdapterPosition());
            }
        }
    public interface OnCommentItemListener
    {
        void onItemClick(View v, int position);
    }
}

