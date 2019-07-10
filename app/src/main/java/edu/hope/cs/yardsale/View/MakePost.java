package edu.hope.cs.yardsale.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;
import edu.hope.cs.yardsale.Model.Course;
import edu.hope.cs.yardsale.Model.Post;
import edu.hope.cs.yardsale.Model.User;
import edu.hope.cs.yardsale.R;
import android.widget.ProgressBar;

import com.google.gson.Gson;

//Copyright (c) 2016 Esa Firman

public class MakePost extends AppCompatActivity {

    EditText title;
    EditText description;
    EditText price;
    EditText condition;
    EditText courseNumber;
    EditText courseName;
    private ArrayList<String> imagePaths = new ArrayList<>();
    Spinner category;
    Post newPost;
    String type = "housing";
    DecimalFormat df = new DecimalFormat("0.00");
    Course course;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);
        title = findViewById(R.id.makePostTitle);
        description = findViewById(R.id.makePostDesc);
        price = findViewById(R.id.makePostPrice);
        category = findViewById(R.id.makePostType);
        condition = findViewById(R.id.makePostCondition);
        courseNumber = findViewById(R.id.course_number);
        courseName = findViewById(R.id.course_name);

        Button addImage = findViewById(R.id.addImageButton);


        courseName.setVisibility(View.INVISIBLE);
        courseNumber.setVisibility(View.INVISIBLE);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    courseName.setVisibility(View.VISIBLE);
                    courseNumber.setVisibility(View.VISIBLE);
                }
                else{
                    courseName.setVisibility(View.INVISIBLE);
                    courseNumber.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView post = findViewById(R.id.makePostPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Yardsale/MakePost", "post button clicked!");

                // make sure all inputs are valid and filled
                if(title.getText().toString().equals("") || 
                   description.getText().toString().equals("") || 
                   price.getText().toString().equals("") || 
                   condition.getText().toString().equals("")) {

                    final AlertDialog.Builder a_builder1 = new AlertDialog.Builder(MakePost.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    a_builder1.setMessage("You need to fill all options in order to proceed!").setCancelable(false)
                            .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = a_builder1.create();
                    alert1.setTitle("Need more Info!");
                    alert1.show();
                }
                else{
                    int place = category.getSelectedItemPosition();

                    switch(place) {
                        case(0):
                            type = "books";
                            break;
                        case(1):
                            type = "bikes";
                            break;
                    }

                    String thePrice = price.getText().toString();
                    double p = Double.parseDouble(thePrice);
                    newPost = new Post(title.getText().toString(), description.getText().toString(), p);
                    newPost.setActive(true);
                    newPost.setCondition(condition.getText().toString());

                    // check to see if the course name or number are empty
                    if (!courseName.getText().toString().isEmpty() || 
                        !courseNumber.getText().toString().isEmpty()) {
                        course = new Course(courseName.getText().toString(), Integer.parseInt(courseNumber.getText().toString()));
                    }
                    RequestParams params = new RequestParams();
                    params.add("title", newPost.getTitle());
                    params.add("description", newPost.getDescription());
                    params.put("active", newPost.isActive());
                    params.add("price", df.format(newPost.getPrice()));
                    params.add("condition", newPost.getCondition());

                    if(course != null) {
                        params.add("course", new Gson().toJson(course));
                    }

                    // images...
                    // encode them first, and then add them to the params
                    for (String imagePath : imagePaths) {
                        // base64 encode...
                        String encoding = encodeImageBase64Gzip(imagePath);  
                        params.add("images[]", encoding); // array - http://bit.ly/2rmrofX
                    }

                    // disable the posting button
                    TextView post = findViewById(R.id.makePostPost);
                    post.setClickable(false);

                    // show progress indicator
                    progress=new ProgressDialog(MakePost.this);
                    progress.setMessage("Posting...");
                    progress.setIndeterminate(true);
                    progress.show();
                    

                    // submit the post via the API
                    API.submitPost(type, params, new APIDelegate<String>() {
                        public void onAPIDataSuccess(String nothing) {
                            // value means nothing...
                            Log.d("Yardsale/MakePost.onAPIDataSuccess", "post successful");

                            // To dismiss the progress indicator
                            progress.setProgress(100);
                    
                            // close activity
                            finish();
                        }
                      
                        public void onAPIFailure(Error error) {
                            Log.e("Yardsale/MakePost.APIFailure", error.toString());

                            // To dismiss the progress indicator
                            progress.setProgress(100);

                            // re-enable the posting button
                            TextView post = findViewById(R.id.makePostPost);
                            post.setClickable(false);
                        }
                    });
                }
            }
        });
    }

    public void chooseImage(View view) {

        startActivityForResult(ImagePicker.create(this)
                .multi()
                .returnMode(ReturnMode.NONE)
                .folderMode(false)
                .getIntent(this), IpCons.RC_IMAGE_PICKER);
    }

    public void start() {

        ImagePicker imagePicker = ImagePicker.create(this)
                .language("in") // Set image picker language
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE"); // done button text

        imagePicker.limit(10) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                .start(); // start image picker activity with request code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, imageReturnedIntent)) {
            try {
                // get and save the paths of the images
                ArrayList<Image> imgs = (ArrayList<Image>) ImagePicker.getImages(imageReturnedIntent);
                // add all bitmaps to the image bitmaps array
                for (Image image : imgs) {
                    imagePaths.add(image.getPath());
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    // Code from: http://bit.ly/2rcFgcn
    private String encodeImageBase64Gzip(String imagePath) {
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream); // 70% quality
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
