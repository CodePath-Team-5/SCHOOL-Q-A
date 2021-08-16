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

import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShowUserCommentListActivity extends AppCompatActivity implements UserCommentAdapter.OnUserCommentItemListener {

    public static final int POST_CODE = 98;
    private static final String tag = "ShowPostListActivity";
    TextView tv_count;
    UserCommentAdapter adapter;
    RecyclerView recyclerView;
    List<Comment> list;
    ParseUser user;
    Post deletedPost = null;
    Comment deletedComment = null;
    Comment user_cmt;
    Boolean isPostDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_comment_list);
        recyclerView = findViewById(R.id.rv_viewMore_comments);
        tv_count = findViewById(R.id.tv_view_more_comments_count);


        //init
        list = new ArrayList<>();
        isPostDelete = false;

        //get intent
        Bundle extras = getIntent().getExtras();
        int count =  extras.getInt("commentCount");
        //set count value to layout
        tv_count.setText(" "+count);

        //get current user
        user = ParseUser.getCurrentUser();

        adapter= new UserCommentAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //set up swipe to delete feature for Comment list
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(Comment_simpleCallback);
        itemTouchHelper2.attachToRecyclerView(recyclerView);

        queryList();


    }


    private void queryList() {
        ParseQuery<Comment> query2 = ParseQuery.getQuery(Comment.class);
        query2.include(Comment.KEY_USER);
        query2.whereEqualTo(Comment.KEY_USER, user);
        query2.addDescendingOrder(Comment.KEY_CREATED);
        query2.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if  (e!=null)
                {
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                else
                {
                    list.addAll(comments);
                    adapter.clear();
                    adapter.addAll(comments);

                }

            }
        });

    }

    public void handle_back_button(View view) {
        finish();
    }

    ItemTouchHelper.SimpleCallback Comment_simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                //swipe left to delete
                deletedComment = new Comment();
                //save backup of deleted comment in case user want to undo their delete
                try {
                    deletedComment = (Comment) list.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                list.remove(position);
                adapter.notifyItemRemoved(position);

                //reset post count:
                tv_count.setText("" + list.size());

                //making snackbar for undo delete
                Snackbar.make(recyclerView, "Comment: " + deletedComment.getContent() + " is deleted!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //user click undo
                                Log.i(tag, "User click undo");
                                list.add(position, deletedComment);
                                //update adapter post_list
                                adapter.notifyItemInserted(position);
                                //reset comment count:
                                tv_count.setText("" + list.size());

                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    // Snackbar closed on its own

                                    // Delete comment officially if user does not intent to undo their action
                                    deletedComment.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                //sucessfully delete on back4app
                                                Log.i(tag, "Sucessfully delete comment on Back4App. Comment: " + deletedComment.getContent());
                                            } else {
                                                //fail to delete post
                                                Log.i(tag, "Fail to delete comment: " + e.getMessage());
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ShowUserCommentListActivity.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setActionIconTint(Color.BLACK)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18)
                    .setSwipeLeftLabelColor(Color.BLACK)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @Override
    public void onCommentClick(View v, int position) {
    // do nothing

    }

}