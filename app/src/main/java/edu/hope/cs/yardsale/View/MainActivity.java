package edu.hope.cs.yardsale.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import edu.hope.cs.yardsale.Control.HttpUtils;
import edu.hope.cs.yardsale.R;
import edu.hope.cs.yardsale.Model.User;
import edu.hope.cs.yardsale.Model.GetUserDelegate;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetUserDelegate, APIDelegate<String> {

    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // build the sign-in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken("324085793157-vejovjt6ajv5c91veqidvs7vp14g8hm6.apps.googleusercontent.com")
                .build(); // backend client id

        // for signin only
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check to see if the current user is already saved
        User.loadCurrentUserIfSaved(this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void onSuccessfulAuthentication() {
      Intent intent = new Intent(getApplicationContext(), DashBoard.class);
      startActivity(intent);
      finish();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w("login success", "google userid: "+account.getId());

            // auth with backend...
            authenticateWithBackendAndFinish(account.getIdToken());
            
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("bad login", "signInResult:failed code=" + e.getMessage() );  
            //.getStatusCode());
        }
    }

    // authenticates with the backend and then "finishes" auth with UI change (dashboard?)
    private void authenticateWithBackendAndFinish(String googleOAuthToken) {
        Log.w("Yardsale/MainActivity/authWBackend", googleOAuthToken);

        // use the api to auth the user using the google oauth token
        API.authUser(googleOAuthToken, this, getApplicationContext());
    }

    public void onAPIDataSuccess(String token) {
        Log.d("Yardsale/FeedAdapter.onAPIDataSuccess", "got token");
        // ask the user to get and set the current user
        User.getAndSetCurrentUser(token, MainActivity.this, MainActivity.this); // calls onCurrentUserSetSuccess
    }
  
    public void onAPIFailure(Error error) {
        Log.e("Yardsale/FeedAdapter.APIFailure", error.toString());
    }

    public void onCurrentUserSetSuccess() {
      // call successful authentication
      // UI change, "finish"
      onSuccessfulAuthentication();
    }

    
}
