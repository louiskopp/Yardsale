package edu.hope.cs.yardsale.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;
import edu.hope.cs.yardsale.Control.HttpUtils;
import edu.hope.cs.yardsale.Model.Board;
import edu.hope.cs.yardsale.Model.Post;
import edu.hope.cs.yardsale.Model.User;

import edu.hope.cs.yardsale.R;


public class PostDetails extends AppCompatActivity implements APIDelegate<Post> {
    Post post;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        prefs = getApplicationContext().getSharedPreferences(HttpUtils.getSharedPrefsKey(), 0);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent){
        String action = intent.getAction();
        Uri data = intent.getData();
        System.out.println(action);
        if(Intent.ACTION_VIEW.equals(action) && data != null){
            List<String> segments = data.getPathSegments();
            API.getBoardPostById(this, segments.get(1), segments.get(3), User.getCurrentUser().getToken());
            System.out.println(segments);
        } else {
            System.out.println("NOTHING!");
            onAPIDataSuccess((Post) getIntent().getSerializableExtra("Post"));
        }
    }


    @Override
    public void onAPIDataSuccess(Post value) {
        this.post = value;

        TextView title = findViewById(R.id.postTitle);
        title.setText(post.getTitle());
        final TextView description = findViewById(R.id.postText);
        description.setText(post.getDescription());

        final ImageView imageView = (ImageView) findViewById(R.id.postImage);
        final TextView photoIndex = (TextView) findViewById(R.id.postImageIndex);
        if (post.getImages().length > 0) {
            photoIndex.setText("1 / " + post.getImages().length);
            final String url = HttpUtils.BASE_URL + "images/" + post.getImages()[0];
            Picasso.get().load(url).placeholder(R.drawable.loading).into(imageView);
        }
        imageView.setOnTouchListener(new View.OnTouchListener() {
            int index = 0;
            float oldX = -1;

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (oldX == -1) {
                            return false;
                        }
                        int velocityX = (int) (event.getX() - oldX);
                        if (velocityX > 50) {
                            onSwipeRight();
                            oldX = -1;
                            return true;
                        } else if (velocityX < -50) {
                            onSwipeLeft();
                            oldX = -1;
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        oldX = -1;
                        break;
                }
                return false;
            }

            public void onSwipeLeft() {
                if (index < post.getImages().length - 1) {
                    index++;
                    reload();
                }

            }

            public void onSwipeRight() {
                if (index > 0) {
                    index--;
                    reload();
                }
            }

            private void reload() {
                final String url = HttpUtils.BASE_URL + "images/" + post.getImages()[index];
                Picasso.get().load(url).into(imageView);
                photoIndex.setText((index + 1) + " / " + post.getImages().length);
            }
        });

        Button inquire = findViewById(R.id.inquireButton);
        inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",post.getUser().getEmail(), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Yardsale Inquiry: " + post.getTitle());
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.getBoards(new APIDelegate<ArrayList<Board>>(){
                    @Override
                    public void onAPIDataSuccess(ArrayList<Board> boards) {
                        Board b = null;
                        for(Board board : boards){
                            if(board.getId() == post.getBoardId()){
                                b = board;
                                break;
                            }
                        }
                        if(b == null){
                            return;
                        }
                        StringBuilder sb = new StringBuilder("Check out this item on yardsale!\n");
                        sb.append(post.getDescription());
                        sb.append("\nhttp://yardsale.forsale:3000/boards/");
                        sb.append(b.getTitle());
                        sb.append("/posts/");
                        sb.append(post.getId());

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
                        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        startActivity(Intent.createChooser(intent, "Share Post"));
                    }

                    @Override
                    public void onAPIFailure(Error error) {
                        Log.e("Yardsale/PostDetails.Share.APIFailure", error.toString());
                    }
                });

            }
        });

        Button save = findViewById(R.id.details_savePost);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Post has been saved", Toast.LENGTH_SHORT);
                toast.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("boardId", Integer.toString(post.getBoardId()));
                requestParams.add("postId", Integer.toString(post.getId()));
                HttpUtils.post("/users/me/saved", requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    }
                }, prefs.getString("token", ""));
            }
        });


    }

    @Override
    public void onAPIFailure(Error error) {
        Log.e("Yardsale/PostDetails.APIFailure", error.toString());
    }
}
