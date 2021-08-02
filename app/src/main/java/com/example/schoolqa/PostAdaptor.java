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
import com.parse.ParseUser;

import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHholder>{

    private Context context;
    private List<Post> posts;
    private  OnQuestionItemListener mOnQuestionItemListener;

    public PostAdaptor(Context context, List<Post> posts, OnQuestionItemListener onQuestionItemListener) {
        this.context = context;
        this.posts = posts;
        this.mOnQuestionItemListener = onQuestionItemListener;
    }

    @NonNull
    @Override
    public ViewHholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHholder(view, mOnQuestionItemListener);
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

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void addSearch(List<Post> list, String key){
        for(int i =0; i<list.size();i++){
            if(list.get(i).getQuestion().toLowerCase().contains(key.toLowerCase())||list.get(i).getContent().toLowerCase().contains(key.toLowerCase())){
                posts.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }
    public void addHashtagSearch(List<Post> list, String key){
        for(int i =0; i<list.size();i++){
            for (int tag=0; tag<3; tag++ )
            if(list.get(i).getHashtags().get(tag).toLowerCase().contains(key.toLowerCase())){
                posts.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }


    class ViewHholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private TextView tvVote;
        private TextView tvPostId;
        private TextView tvTimestamp;
        private ImageView ivImage;
        private TextView tvQuestion;
        private HashtagView tvHashtags;
        OnQuestionItemListener onQuestionItemListener;
        public ViewHholder(@NonNull View itemView, OnQuestionItemListener onQuestionItemListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimestamp = itemView.findViewById(R.id.timeCreated);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvPostId = itemView.findViewById(R.id.tvPostid);
            tvVote = itemView.findViewById(R.id.tv_Postvote);
            tvHashtags = itemView.findViewById(R.id.tvHashtagList);

            this.onQuestionItemListener = onQuestionItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvUsername.setText("Author: "+post.getUser().getUsername());
            tvQuestion.setText(post.getQuestion());
            tvPostId.setText("Post ID: "+post.getObjectId());
            String time = TimeFormatter.getTimeDifference(post.getCreatedAt().toString());
            tvTimestamp.setText(time);
            tvVote.setText(""+post.getVote());

            if (post.getHashtags().isEmpty() == false) {
                tvHashtags.setData(post.getHashtags());
            }

            ParseFile image = post.getUser().getParseFile("user_image");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_user)).into(ivImage);
            }
        }

        @Override
        public void onClick(View v) {
                onQuestionItemListener.onItemClick(v,getAdapterPosition());
        }
    }
    public interface OnQuestionItemListener
    {
        void onItemClick (View v, int position);
    }
}
