package com.rhicstech.crutra.crutra.auth.Social;

import android.content.Context;
import android.util.Log;

import com.jaychang.sa.AuthCallback;
import com.jaychang.sa.SimpleAuth;
import com.jaychang.sa.SocialUser;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.FacebookCallback;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

/**
 * Created by rhicstechii on 09/02/2018.
 */

public class Facebook {
    private static final Facebook ourInstance = new Facebook();

    public static Facebook getInstance() {
        return ourInstance;
    }

    private Facebook() {
    }

    public void begin(final Context context, final FacebookCallback callback){
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");
        SimpleAuth.getInstance().connectFacebook(scopes, new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
               callback.onSuccess(socialUser);
            }

            @Override
            public void onError(Throwable error) {
               callback.onError(error);
            }

            @Override
            public void onCancel() {
                callback.canceled(context.getResources().getString(R.string.canceled));
            }
        });

    }


    public void twiter(final Context context, final FacebookCallback callback){
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");
        SimpleAuth.getInstance().connectTwitter(new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                callback.onSuccess(socialUser);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);

            }

            @Override
            public void onCancel() {
                callback.canceled(context.getResources().getString(R.string.canceled));
            }
        });

    }

    public Response loginToServer(String accessToken, String email) throws IOException {
        String data = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"email\"\r\n\r\n"+email+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"provider_id\"\r\n\r\n3\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"accessToken\"\r\n\r\n"+accessToken+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
        String datatype = "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
        Response response = Connection.postconnectCustom("auth/flogin",data,datatype);
        return response;
    }


    public Response signUpServer(String email,int provider, int accountType, String accestoken, String firstname, String lastname) throws IOException {
        String data =  "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"email\"\r\n\r\n"+email+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"provider_id\"\r\n\r\n"+provider+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"account_type\"\r\n\r\n"+accountType+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"accessToken\"\r\n\r\n"+accestoken+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"fname\"\r\n\r\n"+firstname+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"lname\"\r\n\r\n"+lastname+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
        String datatype = "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
        Response response = Connection.postconnectCustom("auth/fsignup",data,datatype);
        return response;
    }


}
