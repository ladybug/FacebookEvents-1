package com.example.admin.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isLoggedInOnFacebook()) {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }


        pulaMea();
        buton2();
        setupContinueAsGuest();

        initFacebookLogin();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cancel all notifications when opening the app from the launcher
        cancelAllNotifications();
    }

    public void getFbEvents() {
        /*
            To get FB events, it's not going to be so easy, as you can't sort them by city
            What we have to do:

            Get all locations within a specified range (center = latitude, longitude), distance from that center
            search?pretty=0&q=*&type=place&center=46.771478%2C23.624490&distance=10000

            Get all those places' events
         */

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/search",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(getBaseContext(), "GOT IT", Toast.LENGTH_LONG).show();

                        try {
                            JSONArray rawData = response.getJSONObject().getJSONArray("data");

                            for (int i = 0; i < rawData.length(); ++i) {
                                Toast.makeText(getBaseContext(), ((JSONObject)rawData.get(i)).getString("name"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("ERROR", "unhandled JSON exception", e);
                        }

                        //Toast.makeText(getBaseContext(), object.getString("name"), Toast.LENGTH_LONG).show();

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("q", "*");
        parameters.putString("type", "event");
        parameters.putString("center", "37.76,-122.427");
        parameters.putString("distance", "1000");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public boolean isLoggedInOnFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_events");
        textView = (TextView) findViewById(R.id.fbloginresulttext);
        // If using in a fragment
        //loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("LOGIN SUCCESS");

                //getFbEvents();

                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
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

    public void setupContinueAsGuest() {
        Button button = (Button) findViewById(R.id.continue_as_guest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(i);
            }
        });
    }

    public void pulaMea() {
        FloatingActionButton emailBut = (FloatingActionButton) findViewById(R.id.emailBut);
        emailBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), "logging out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);

                //LoginManager.getInstance().logOut();
            }
        });
    }

    public void buton2() {
        FloatingActionButton sendBut = (FloatingActionButton) findViewById(R.id.sendBut);
        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNotification();
            }
        });
    }

    public void buildNotification() {
        // create intent (action if user clicks on the notification)
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder mNotifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notif_test)
                        .setContentTitle("test notification title")
                        .setContentText("test notification text")
                        .setContentIntent(intent)
                        .setAutoCancel(true); // remove notification after click

        sendNotification(mNotifBuilder);
    }

    public void sendNotification(NotificationCompat.Builder mNotifBuilder) {
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(1, mNotifBuilder.build());
    }

    public void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
