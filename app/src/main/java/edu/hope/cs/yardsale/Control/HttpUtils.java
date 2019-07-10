package edu.hope.cs.yardsale.Control;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class HttpUtils {
    public static final String BASE_URL = "http://yardsale.forsale:3000/";
    private static final String PREFS_KEY = "HttpUtilsPrefs_Key";
    private static final int CHAN = 0;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String postAuthorization) {
      client.addHeader("Authorization", "Bearer " + postAuthorization);
      client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String postAuthorization) {
        client.addHeader("Authorization", "Bearer " + postAuthorization);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String getSharedPrefsKey(){
        return PREFS_KEY;
    }

}
