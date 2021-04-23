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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.List;

public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private OnUserCommentItemListener itemListener;


    public UserCommentAdapter(Context context, List<Comment> comments, OnUserCommentItemListener listener) {
        this.commentList = comments;
        this.context = context;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public UserCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_comment_item, parent, false);
        return new ViewHolder(view,itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCommentAdapter.ViewHolder holder, int position) {
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
        private TextView tvfromPost;
        private TextView tvComment;
        private TextView tvTimestamp;
        private ImageView ivUserImage;
        OnUserCommentItemListener userCommentItemListener;


        public ViewHolder(@NonNull View itemView , OnUserCommentItemListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.cmt_tvUsername);
            tvTimestamp = itemView.findViewById(R.id.cmt_timeCreated);
            tvComment = itemView.findViewById(R.id.cmt_tvComment);
            tvfromPost = itemView.findViewById(R.id.cmt_tvFromPost);
            ivUserImage = itemView.findViewById(R.id.cmt_ivImage);

            this.userCommentItemListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Comment comment) {
            //set username
            tvUsername.setText("Commented By: "+comment.getUser().getUsername());

            //set post title

            final String id = comment.getPostId();
            Log.i("UserCommentAdapter","Post ID: "+id);
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            // Define our query conditions
            query.whereEqualTo("objectId", id);
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {

                    if (e!=null)
                    {
                        tvfromPost.setText("From post: "+objects.get(0).getQuestion());
                    }
                    else
                    {
                        tvfromPost.setText("Post ID: "+id);
                    }

                }
            });

            //set timestamp
            String time = TimeFormatter.getTimeDifference(comment.getCreatedAt().toString());
            tvTimestamp.setText(time);

            //set comment
            String cmt = comment.getContent();
            cmt = cmt.substring(0, Math.min(cmt.length(), 50)); //trim down comment content to only 100 character
            if (cmt.length()>20)
            {tvComment.setText(cmt+ "...");}
            else
            {
                tvComment.setText(cmt);
            }


            ParseFile user_image = comment.getUser().getParseFile("user_image");
            ParseFile cmt_image = comment.getImage();
            //add user image
            if (user_image != null) {
                Glide.with(context).load(user_image.getUrl()).into(ivUserImage);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_user)).into(ivUserImage);
            }

        }

        @Override
        public void onClick(View v) {
            userCommentItemListener.onCommentClick(v,getAdapterPosition());
        }
    }

    public interface OnUserCommentItemListener
    {
        void onCommentClick(View v, int position);
    }
}


