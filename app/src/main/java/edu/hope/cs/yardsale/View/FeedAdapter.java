package edu.hope.cs.yardsale.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.people.v1.model.Url;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.hope.cs.yardsale.Control.HttpUtils;
import edu.hope.cs.yardsale.Model.Post;
import edu.hope.cs.yardsale.Model.User;
import edu.hope.cs.yardsale.R;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;

public class FeedAdapter extends BaseAdapter implements APIDelegate<ArrayList<Post>> {

    private ArrayList<Post> posts;
    private Context context;
    private LayoutInflater inflater;
    private DecimalFormat df = new DecimalFormat("0.00");

    private String type;

    public FeedAdapter(Context context, String type){
        this.context = context;

        // set the type
        this.type = type;

        // load the data initially
        reloadData();
        
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void reloadData() {
      switch (this.type) {
        case("all"):
            // all posts
            API.getAllPosts(this);
            break;

        case("saved"):
            // user saved posts
            API.getSavedPosts(this, User.getCurrentUser().getToken());
            break;

        case("userPosts"):
            // posts the user has posted
            API.getUserPosts(this, User.getCurrentUser().getToken());
            break;

        // board names
        case("Books"):
        case("Bikes"):
        case("Housing"):
            API.getPostsFromBoard(this.type, this);
            break;
      }
    }

    public void onAPIDataSuccess(ArrayList<Post> value) {
        // only update data if it needs to be changed
        if (posts != value) {
          posts = value;
          notifyDataSetChanged();
        }
        
        Log.d("Yardsale/FeedAdapter.onAPIDataSuccess", ""+value.size());
    }

    public void onAPIFailure(Error error) {
        Log.e("Yardsale/FeedAdapter.APIFailure", error.toString());
    }

    @Override
    public int getCount() {
        if(posts==null){return 0;}
        else {
            return posts.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(posts==null){return null;}
        else {
            return posts.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    static class ViewHolder {
        TextView titleView;
        TextView price;
        public TextView cell;
        public ImageView image;
        ConstraintLayout constraintLayout;
        int ref;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.feed_item, null);
            holder.titleView = convertView.findViewById(R.id.cellTitle);
            holder.price = convertView.findViewById(R.id.feed_price);
            holder.image = convertView.findViewById(R.id.cellImage);
            holder.cell = convertView.findViewById(R.id.cell);
            holder.constraintLayout = convertView.findViewById(R.id.feed_layout);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ref = position;
        final Post post = posts.get(position);
        holder.titleView.setText(post.getTitle());
        holder.price.setText("$"+df.format(post.getPrice()));

        if(post.getImages().length > 0) {
            final String photoUrl = HttpUtils.BASE_URL + "images/"+post.getImages()[0];
            Picasso.get().load(photoUrl).placeholder(R.drawable.loading).into(holder.image);
        } else {
            holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(context, PostDetails.class);
                postIntent.putExtra("Post", post);
                context.startActivity(postIntent);
            }
        });
        holder.cell.bringToFront();

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }
}
