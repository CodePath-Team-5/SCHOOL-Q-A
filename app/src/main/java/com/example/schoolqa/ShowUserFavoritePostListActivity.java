package com.example.schoolqa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShowUserFavoritePostListActivity extends AppCompatActivity implements PostAdaptor.OnQuestionItemListener {

    public static final int POST_CODE = 98;
    private static final String tag = "ShowFavPostActivity";
    TextView tv_count;
    PostAdaptor adapter;
    RecyclerView recyclerView;
    List<FavoritePost> list;
    List<Post> favoritePostList;
    ParseUser user;
    FavoritePost deletedPost = null;
    Boolean isPostDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_favorite_post_list);
        recyclerView = findViewById(R.id.rv_view_more_favorite_posts);
        tv_count = findViewById(R.id.tv_view_more_favPost_count);


        //init
        favoritePostList = new ArrayList<>();
        list = new ArrayList<>();
        isPostDelete = false;

        //get intent
        Bundle extras = getIntent().getExtras();
        int count =  extras.getInt("favPostCount");
        //set count value to layout
        tv_count.setText(" "+count);

        //get current user
        user = ParseUser.getCurrentUser();

        adapter= new PostAdaptor(this, favoritePostList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //set up swipe to delete feature for Post list
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(Post_simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        queryList();


    }

    private void queryList() {

        ParseQuery<FavoritePost> query = ParseQuery.getQuery(FavoritePost.class);
        query.include(FavoritePost.KEY_USER);
        query.whereEqualTo(FavoritePost.KEY_USER, user);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<FavoritePost>() {
            @Override
            public void done(List<FavoritePost> objects, ParseException e) {
                if (e==null)
                {
                    list.addAll(objects);
                    adapter.clear();
                    adapter.addAllFavoritePost(objects);
                }
                else
                {
                    Log.i(tag,"Fail to query favorite post");
                }
            }
        });

    }

    ItemTouchHelper.SimpleCallback Post_simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                //swipe left to delete
                deletedPost = new FavoritePost();

                //save backup of deleted Post in case user want to undo their delete
                try {
                    deletedPost = (FavoritePost) list.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                list.remove(position);
                favoritePostList.remove((position));
                adapter.notifyItemRemoved(position);

                //reset post count:
                tv_count.setText("" + list.size());

                //making snackbar for undo delete
                Snackbar.make(recyclerView, "Post: "+deletedPost.getPost().getQuestion()+" is removed from My Favorite Posts!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //user click undo
                                Log.i(tag, "User click undo");
                                list.add(position, deletedPost);
                                favoritePostList.add(position,deletedPost.getPost());
                                //update adapter post_list
                                adapter.notifyItemInserted(position);
                                //reset post count:
                                tv_count.setText("" + list.size());

                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    // Snackbar closed on its own
                                    remove_FavPost(deletedPost);
                                }
                            }
                        })
                        .show();

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ShowUserFavoritePostListActivity.this,R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setActionIconTint(Color.BLACK)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP,18)
                    .setSwipeLeftLabelColor(Color.BLACK)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void remove_FavPost(FavoritePost deletedPost) {
        deletedPost.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null)
                {
                    //delete sucess
                    Toast.makeText(getApplicationContext(),"Post has been removed from My Favorite List",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void handle_back_button(View view) {
        finish();
    }


    @Override
    public void onItemClick(View v, int position) {
        Post post = list.get(position).getPost();
        goToPostActivity(post);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POST_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                queryList();
            }
        }
    }

    private void goToPostActivity(Post post) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivityForResult(intent,POST_CODE); //go to post activity
    }
}