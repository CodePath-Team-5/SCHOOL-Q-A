package com.example.schoolqa;

import androidx.annotation.NonNull;
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

public class ShowUserPostListActivity extends AppCompatActivity implements PostAdaptor.OnQuestionItemListener {

    public static final int POST_CODE = 98;
    private static final String tag = "ShowPostListActivity";
    TextView tv_count;
    PostAdaptor adapter;
    RecyclerView recyclerView;
    List<Post> list;
    ParseUser user;
    Post deletedPost = null;
    Boolean isPostDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_post_list);
        recyclerView = findViewById(R.id.rv_viewMore_posts);
        tv_count = findViewById(R.id.tv_view_more_Post_count);


        //init
        list = new ArrayList<>();
        isPostDelete = false;

        //get intent
        Bundle extras = getIntent().getExtras();
        int count =  extras.getInt("postCount");
        //set count value to layout
        tv_count.setText(" "+count);

        //get current user
        user = ParseUser.getCurrentUser();

        adapter= new PostAdaptor(this, list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //set up swipe to delete feature for Post list
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(Post_simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        queryList();


    }


    private void queryList() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!= null){
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                else
                {
                    adapter.clear();
                    adapter.addAll(posts);
                    //allPost.addAll(posts);
                    //adaptor.notifyDataSetChanged();
                    //swipeRefreshLayout.setRefreshing(false);
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
                deletedPost = new Post();

                //save backup of deleted Post in case user want to undo their delete
                try {
                    deletedPost = (Post) list.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                list.remove(position);
                adapter.notifyItemRemoved(position);

                //reset post count:
                tv_count.setText("" + list.size());

                //making snackbar for undo delete
                Snackbar.make(recyclerView, "Post: "+deletedPost.getQuestion()+" is deleted!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //user click undo
                                Log.i(tag, "User click undo");
                                list.add(position, deletedPost);
                                //update adapter post_list
                                adapter.notifyItemInserted(position);
                                //reset post count:
                                tv_count.setText("" + list.size());
                                //reset flag
                                isPostDelete = false;

                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    // Snackbar closed on its own

                                    //delete comments that relate with post
                                    query_and_deleteComments(deletedPost.getObjectId());

//                                    //unsubscribe post channel before delete post
//                                    String postChannel = "POST_"+deletedPost.getObjectId();
//                                    ParsePush.unsubscribeInBackground(postChannel);

                                    // Delete post officially if user does not intent to undo their action

                                    deletedPost.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null)
                                            {
                                                //sucessfully delete on back4app
                                                Log.i(tag, "Sucessfully delete post on Back4App. Post: " + deletedPost.getQuestion());

                                                //set isPostDelte to true -> trigger flag to refresh post list when go back to Search Activity
                                                isPostDelete = true;
                                            }
                                            else
                                            {
                                                //fail to delete post
                                                Log.i(tag, "Fail to delete post: "+e.getMessage());
                                            }
                                        }
                                    });

                                }
                            }
                        })
                        .show();

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ShowUserPostListActivity.this,R.color.colorAccent))
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

    public void query_and_deleteComments(String postId)
    {
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Define our query conditions
        query.whereEqualTo("postId", postId);
        query.addDescendingOrder(Post.KEY_CREATED);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> itemList, ParseException e) {
                if (e != null) {
                    Log.d("item", "Error: " + e.getMessage());

                }
                // Access the array of results here
                for (Comment comment:itemList) {
                    Log.i(tag, "Delete Comment: " + comment.getContent());
                    //removeComment_onList(comment);
                    comment.deleteInBackground();

                }
            }
        });
    }

    @Override
    public void onItemClick(View v, int position) {
        Post post = list.get(position);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );
        goToPostActivity(post);
    }
    private void goToPostActivity(Post post) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivityForResult(intent,POST_CODE);
    }

    public void handle_back_button(View view) {
        Log.d(tag,"Back button clicked");

        if (isPostDelete==  true)
        {
            //if user delete post -> need update/query list again when go back Profile Screen
            Intent returnIntent= new Intent();
            setResult(RESULT_OK,returnIntent);
        }

        finish(); //go back to previous screen - Profile screen

    }
}