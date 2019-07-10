package edu.hope.cs.yardsale.Model;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.hope.cs.yardsale.Control.HttpUtils;
import edu.hope.cs.yardsale.Model.GetUserDelegate;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;

import org.json.JSONObject;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;


public class User implements Serializable {
    // singleton
    private static User current_user = null;

    private int id;
    private String name;
    private String email;
    private String token;
    private String imageUrl;

    private static String UserSharedPrefsKey = "User_Key";
    private static String UserSharedPrefs_TokenKey = "Token_Key";

    private User() {}

    public User(String name, String email, String imageUrl) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public static User getCurrentUser() { 
        // To ensure only one instance is created 
        if (current_user == null) {
            current_user = new User();
        }
        return current_user;
    }

    public int getId() {
      return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() { return imageUrl;}

    public String getToken() { return token; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
      return "id: "+id+" name: "+name+" email: "+email+" imageKey: "+ imageUrl;
    }

    public static void loadCurrentUserIfSaved(GetUserDelegate delegate, Context context) {
        // get saved token in shared prefs
        SharedPreferences prefs = getSharedPrefs(context);
        String token = prefs.getString(UserSharedPrefs_TokenKey, "");

        Log.d("Yardsale/loadCurrentUser", token != "" ? "saved token exists" : "no saved token");

        // yay! login the user
        if (token != "") {
            getAndSetCurrentUser(token, delegate, context);
        }
    }

    public static SharedPreferences getSharedPrefs(Context context) {
         return context.getSharedPreferences(UserSharedPrefsKey, 0);
    }

    public static void getAndSetCurrentUser(final String token, final GetUserDelegate delegate, final Context context) {
        if (token == null) {
            // todo: crash?
            Log.e("Yardsale/getAndSetCurrentUser","Cannot get current user without token.");
            return;
        }

        API.getMe(new APIDelegate<User>() {
            @Override
            public void onAPIDataSuccess(User user) {
                // set the current user
                User.current_user = user;
        
                // set the token
                User.getCurrentUser().token = token;
        
                // save the token
                SharedPreferences prefs = getSharedPrefs(context);
                prefs.edit().putString(UserSharedPrefs_TokenKey, token).apply();
        
                Log.d("Yardsale/getAndSetCurrentUser", "current user set");

                delegate.onCurrentUserSetSuccess();
            }

            @Override
            public void onAPIFailure(Error error) {
                Log.e("Yardsale/User api failure", error.toString());
            }
        }, token);
    }
}
