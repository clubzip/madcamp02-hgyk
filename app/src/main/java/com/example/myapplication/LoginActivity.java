package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity  extends AppCompatActivity {

    private LoginButton login;
    private String UserID;
    private String UserName;
    CallbackManager callbackManager;

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {

            UserID = currentAccessToken.getUserId();
            Profile profile = Profile.getCurrentProfile();
            UserName = profile.getName();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("username", UserName);
            intent.putExtra("userid",UserID);
            startActivity(intent);
        }/* else {
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = findViewById(R.id.login_button);

        login.setPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        //로그인 상태라면 바로 setContentView(R.layout.activity_main);
        /*
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLogin = accessToken != null;*/
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        updateWithToken(AccessToken.getCurrentAccessToken());


        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    Profile profile = Profile.getCurrentProfile();
                                    UserName = profile.getName();
                                    UserID = loginResult.getAccessToken().getUserId();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("userid",UserID);
                                    intent.putExtra("username", UserName);
                                    startActivity(intent);

                                    String name = object.getString("name"); // 혹시 쓸일 있을까봐 놔둠.
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
