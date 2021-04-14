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

    public void addSearch(List<Post> list, String key){
        for(int i =0; i<list.size();i++){
            if(list.get(i).getQuestion().toLowerCase().contains(key.toLowerCase())||list.get(i).getContent().toLowerCase().contains(key.toLowerCase())){
                posts.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }


    class ViewHholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvQuestion;
        OnQuestionItemListener onQuestionItemListener;
        public ViewHholder(@NonNull View itemView, OnQuestionItemListener onQuestionItemListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);

            this.onQuestionItemListener = onQuestionItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvQuestion.setText(post.getQuestion());

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
            onQuestionItemListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnQuestionItemListener
    {
        void onItemClick (int position);
    }
}
