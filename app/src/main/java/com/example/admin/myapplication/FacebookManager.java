package com.example.admin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by admin on 12/19/2017.
 */

public class FacebookManager {
    public List<Event> fbEventsList;

    public FacebookManager() {
        fbEventsList = new ArrayList<Event>();
    }

    public List getEventList() {
        Toast.makeText(getApplicationContext(), String.valueOf(fbEventsList.size()), Toast.LENGTH_LONG).show();
        return fbEventsList;
    }

    public void getPlaceIds() {
        // the do while is useless
        final String[] nextPage = {""};
        do {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/search",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            try {
                                JSONArray rawData = response.getJSONObject().getJSONArray("data");
                                String nextPageData = response.getJSONObject().getJSONObject("paging").getJSONObject("cursors").getString("after");
                                nextPage[0]  = nextPageData;

                                //Toast.makeText(getApplicationContext(), nextPageData, Toast.LENGTH_LONG).show();

                                ArrayList<String> placeIds = new ArrayList<String>();
                                for (int i = 0; i < rawData.length(); ++i) {
                                    placeIds.add(((JSONObject) rawData.get(i)).getString("id"));
                                }

                                getEvents(placeIds);
                            } catch (JSONException e) {
                                Log.e("ERROR", "unhandled JSON exception", e);
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("pretty", "0");
            parameters.putString("q", "*");
            parameters.putString("type", "place");
            parameters.putString("center", "46.771478,23.624490");
            parameters.putString("distance", "3000");
            parameters.putString("limit", "100"); // This should work
            request.setParameters(parameters);
            request.executeAsync();
        } while (nextPage[0].length() > 0);
    }

    public void getEvents(ArrayList<String> placeIds) {
        for (String s : placeIds) {
            //String placeEventQuery = placeIds.get(0) + "/events";
            String placeEventQuery = s + "/events";

            // Get the events from those place ids
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    placeEventQuery,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONArray rawData = response.getJSONObject().getJSONArray("data");
                                for (int i = 0; i < rawData.length(); ++i) {
                                    //Toast.makeText(getApplicationContext(), ((JSONObject) rawData.get(i)).getString("name"), Toast.LENGTH_SHORT).show();

                                    // Don't add the event to the list if start_time < current_time (date)
                                    // start_time.split("T") (format for start_time is dateTtime
                                    fbEventsList.add(new Event(
                                            i + 1,
                                            ((JSONObject) rawData.get(i)).getString("name"),
                                            ((JSONObject) rawData.get(i)).getString("description"),
                                            0,
                                            ((JSONObject) rawData.get(i)).getString("start_time")
                                    ));

                                    FacebookEvents.setEventList(fbEventsList);

                                   //Toast.makeText(getApplicationContext(), String.valueOf(fbEventsList.size()), Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(getApplicationContext(), String.valueOf(fbEventsList.size()), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                Log.e("ERROR", "unhandled JSON exception", e);
                            }
                        }
                    });

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Or maybe a loading screen and .executeAndWait()
            request.executeAsync();
        }
    }

    //! Not used
    public static void initFacebookLogin(CallbackManager callbackManager, LoginButton loginButton, final TextView textView, final Context baseContext) {
        callbackManager = CallbackManager.Factory.create();
        //LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_events");
        //textView = (TextView) findViewById(R.id.fbloginresulttext);
        // If using in a fragment
        //loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("LOGIN SUCCESS");

                //getFbEvents();

                Intent intent = new Intent(baseContext, HomeActivity.class);
                baseContext.startActivity(intent);
            }

            @Override
            public void onCancel() {
                textView.setText("LOGIN CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                textView.setText("LOGIN ERROR");
            }
        });
    }

    public static boolean isLoggedInOnFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static void logOut() {
        LoginManager.getInstance().logOut();
    }

}
