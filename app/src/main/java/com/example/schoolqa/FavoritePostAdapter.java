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
import com.greenfrvr.hashtagview.HashtagView;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class FavoritePostAdapter extends RecyclerView.Adapter<FavoritePostAdapter.ViewHholder>{

    private static final String tag  = "FavoritePostAdapter";
    private Context context;
    private List<Post> posts;
    private OnFavoriteQuestionItemListener mOnFavoriteQuestionItemListener;

    public FavoritePostAdapter(Context context, List<Post> posts, OnFavoriteQuestionItemListener onFavoriteQuestionItemListener) {
        this.context = context;
        this.posts = posts;
        this.mOnFavoriteQuestionItemListener = onFavoriteQuestionItemListener;
    }

    @NonNull
    @Override
    public FavoritePostAdapter.ViewHholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_favorite_post_item, parent, false);
        return new FavoritePostAdapter.ViewHholder(view, mOnFavoriteQuestionItemListener);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHholder holder) {
        super.onViewAttachedToWindow(holder);
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
    public void addAll(List<FavoritePost> list) {
        List<Post> p = new ArrayList<>();
        for(int i=0; i<list.size(); i++)
        {
            p.add(list.get(i).getPost());
        }
        posts.addAll(p);
        notifyDataSetChanged();
    }


    class ViewHholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private TextView tvVote;
        private TextView tvPostId;
        private TextView tvTimestamp;
        private ImageView ivImage;
        private TextView tvQuestion;

        OnFavoriteQuestionItemListener onFavoriteQuestionItemListener;
        public ViewHholder(@NonNull View itemView, OnFavoriteQuestionItemListener onFavoriteQuestionItemListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername98);
            ivImage = itemView.findViewById(R.id.ivImage98);
            tvTimestamp = itemView.findViewById(R.id.timeCreated98);
            tvQuestion = itemView.findViewById(R.id.tvQuestion98);
            tvPostId = itemView.findViewById(R.id.tvPostid98);
            tvVote = itemView.findViewById(R.id.tv_Postvote98);


            this.onFavoriteQuestionItemListener = onFavoriteQuestionItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvQuestion.setText(post.getQuestion());
            tvPostId.setText("Post ID: "+post.getObjectId());
            String time = TimeFormatter.getTimeDifference(post.getCreatedAt().toString());
            tvTimestamp.setText(time);
            tvVote.setText(""+post.getVote());
            //ParseFile image = post.getImage();
            ParseFile image = post.getUser().getParseFile("user_image");
            if (image != null) {
                // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
                Glide.with(context).load(image.getUrl()).into(ivImage);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_user)).into(ivImage);
            }
        }

        @Override
        public void onClick(View v) {
            onFavoriteQuestionItemListener.onFavoritePostlick(v,getAdapterPosition());
        }
    }
    public interface OnFavoriteQuestionItemListener
    {
        void onFavoritePostlick (View v, int position);
    }
}