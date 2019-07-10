package edu.hope.cs.yardsale.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import edu.hope.cs.yardsale.Model.Board;
import edu.hope.cs.yardsale.Model.Post;
import edu.hope.cs.yardsale.Model.User;
import edu.hope.cs.yardsale.Model.Board;
import okhttp3.Headers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class API {

    // MARK: Posts

    public static void submitPost(String board, RequestParams params, final APIDelegate<String> delegate) {
      HttpUtils.post("/boards/"+board+"/posts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                delegate.onAPIDataSuccess("");
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                delegate.onAPIFailure(new Error(errorResponse.toString()));
            }
      });
  }

    public static void getAllPosts(final APIDelegate<ArrayList<Post>> delegate) {

        HttpUtils.get("/posts", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // serialize the object
                    Post[] posts = new Gson().fromJson(response.getString("data"), Post[].class);

                    // return the value to the delegate
                    delegate.onAPIDataSuccess(new ArrayList<Post>(Arrays.asList(posts)));
                  }
                  catch (JSONException e) {
                    Log.e("Yardsale/getAndSetCurrentUser", e.toString());

                    // return the value to the delegate
                    delegate.onAPIFailure(new Error(e.toString()));
                  }
            }
        });
    }

    public static void getSavedPosts(final APIDelegate<ArrayList<Post>> delegate, String authToken) {

      HttpUtils.get("/users/me/saved", null, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              try {
                  // serialize the object
                  Post[] posts = new Gson().fromJson(response.getString("data"), Post[].class);

                  // return the value to the delegate
                  delegate.onAPIDataSuccess(new ArrayList<Post>(Arrays.asList(posts)));
                }
                catch (JSONException e) {
                  Log.e("Yardsale/getSavedPosts", e.toString());

                  // return the value to the delegate
                  delegate.onAPIFailure(new Error(e.toString()));
                }
          }
      }, authToken);
    }

    public static void getUserPosts(final APIDelegate<ArrayList<Post>> delegate, String authToken) {

      HttpUtils.get("/users/me/posts", null, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              try {
                  // serialize the object
                  Post[] posts = new Gson().fromJson(response.getString("data"), Post[].class);

                  // return the value to the delegate
                  delegate.onAPIDataSuccess(new ArrayList<Post>(Arrays.asList(posts)));
                }
                catch (JSONException e) {
                  Log.e("Yardsale/getUserPosts", e.toString());

                  // return the value to the delegate
                  delegate.onAPIFailure(new Error(e.toString()));
                }
          }
      }, authToken);
    }

    public static void getBoardPostById(final APIDelegate<Post> delegate, String boardName, String postId, String authToken) {
      
      HttpUtils.get("/boards/"+boardName+"/posts/"+postId, null, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              try {
                  // serialize the object
                  Post post = new Gson().fromJson(response.getString("data"), Post.class);

                  // return the value to the delegate
                  delegate.onAPIDataSuccess(post);
                }
                catch (JSONException e) {
                  Log.e("Yardsale/getUserPosts", e.toString());

                  // return the value to the delegate
                  delegate.onAPIFailure(new Error(e.toString()));
                }
          }
      });
    }

    public static void getPostsFromBoard(String boardName, final APIDelegate<ArrayList<Post>> delegate) {

      HttpUtils.get("/boards/"+boardName+"/posts", null, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              try {
                  // serialize the object
                  Post[] posts = new Gson().fromJson(response.getString("data"), Post[].class);

                  // return the value to the delegate
                  delegate.onAPIDataSuccess(new ArrayList<Post>(Arrays.asList(posts)));
                }
                catch (JSONException e) {
                  Log.e("Yardsale/getPostsFromBoard", e.toString());

                  // return the value to the delegate
                  delegate.onAPIFailure(new Error(e.toString()));
                }
          }

          @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                delegate.onAPIFailure(new Error(errorResponse.toString()));
            }
      });
    }

    public static void getBoards(final APIDelegate<ArrayList<Board>> delegate) {

        HttpUtils.get("/boards", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // serialize the object
                    Board[] boards = new Gson().fromJson(response.getString("data"), Board[].class);

                    // return the value to the delegate
                    delegate.onAPIDataSuccess(new ArrayList<>(Arrays.asList(boards)));
                }
                catch (JSONException e) {
                    Log.e("Yardsale/getBoards", e.toString());

                    // return the value to the delegate
                    delegate.onAPIFailure(new Error(e.toString()));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                delegate.onAPIFailure(new Error(errorResponse.toString()));
            }
        });
    }

    // MARK: Users

    public static void getMe(final APIDelegate<User> delegate, String authToken) {
        // no params
        HttpUtils.get("/users/me", new RequestParams(), new JsonHttpResponseHandler(){

          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              // todo: serialize the user object and set it to the User.currentUser object
              
              try {
                User userObject = new Gson().fromJson(response.getString("data"), User.class);

                delegate.onAPIDataSuccess(userObject); 
              }
              catch (JSONException e) {
                Log.e("Yardsale/getMe", e.toString());

                // return the value to the delegate
                delegate.onAPIFailure(new Error(e.toString()));
              }
          }

          @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                delegate.onAPIFailure(new Error(errorResponse.toString()));
            }

          // todo: fail block?
      }, authToken);
    }

    public static void getUserById(int id, final APIDelegate<User> delegate, String authToken) {
        HttpUtils.get("/users/id/" + id , new RequestParams(), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // todo: serialize the user object and set it to the User.currentUser object

                try {
                    User userObject = new Gson().fromJson(response.getString("data"), User.class);

                    delegate.onAPIDataSuccess(userObject);
                }
                catch (JSONException e) {
                    Log.e("Yardsale/getUserByID", e.toString());

                    // return the value to the delegate
                    delegate.onAPIFailure(new Error(e.toString()));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                delegate.onAPIFailure(new Error(errorResponse.toString()));
            }

            // todo: fail block?
        }, authToken);
    }

    // MARK: Boards

    public static void getAllBoards(final APIDelegate<ArrayList<Board>> delegate) {
      HttpUtils.get("/boards", new RequestParams(), new JsonHttpResponseHandler(){

          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              // todo: serialize the user object and set it to the User.currentUser object

              try {
                  Board[] boards = new Gson().fromJson(response.getString("data"), Board[].class);

                  delegate.onAPIDataSuccess(new ArrayList<Board>(Arrays.asList(boards)));
              }
              catch (JSONException e) {
                  Log.e("Yardsale/getAllBoards", e.toString());

                  // return the value to the delegate
                  delegate.onAPIFailure(new Error(e.toString()));
              }
          }

          @Override
          public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
            delegate.onAPIFailure(new Error(errorResponse.toString()));
          }
          // todo: fail block?
      });
  }

    // MARK: Auth
    public static void authUser(String googleOAuthToken, final APIDelegate<String> delegate, final Context context) {

      RequestParams params = new RequestParams();
      params.put("google_oauth_token", googleOAuthToken);
      HttpUtils.post("/users/auth", params, new JsonHttpResponseHandler(){

          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              Log.w("Yardsale/authUser success", "good response");
              
              try {
                // todo: serialize into JSON object...
                String token = response.getJSONObject("data").getString("token");
                SharedPreferences prefs = context.getSharedPreferences(HttpUtils.getSharedPrefsKey(), 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("token", token);
                editor.apply();
                // notify delegate
                delegate.onAPIDataSuccess(token);
              }
              catch (JSONException e) {
                  Log.e("Yardsale/authUser", e.toString());

                  // return the value to the delegate
                delegate.onAPIFailure(new Error(e.toString()));
              }
          }

          @Override
          public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
            Log.w("Yardsale/auth failure", "response: "+errorResponse.toString());
          }
      });
    }
  }