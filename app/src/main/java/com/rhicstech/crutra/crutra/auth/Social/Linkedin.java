package com.rhicstech.crutra.crutra.auth.Social;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.auth.LoginActivity;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.LinkedinCallback;

import java.io.IOException;

import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rhicstechii on 13/02/2018.
 */

public class Linkedin {
    private static final Linkedin ourInstance = new Linkedin();
    static Activity c;
    public static Linkedin getInstance(Activity context) {
        c = context;
        return ourInstance;
    }

    private Linkedin() {
    }

    public Response signUpServer(String email, int provider, int accountType, String accestoken, String firstname, String lastname) throws IOException {
        String data =  "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"email\"\r\n\r\n"+email+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"provider_id\"\r\n\r\n"+provider+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"account_type\"\r\n\r\n"+accountType+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"accessToken\"\r\n\r\n"+accestoken+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"fname\"\r\n\r\n"+firstname+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"lname\"\r\n\r\n"+lastname+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
        String datatype = "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
        Response response = Connection.postconnectCustom("auth/lsignup",data,datatype);
        return response;
    }


    public void linkdinStart(final LinkedinCallback callback){
        LISessionManager.getInstance(getApplicationContext()).init(c, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                getLinkedinPofile(callback);
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors

            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    private void getLinkedinPofile(final LinkedinCallback callback){
        callback.onstart();
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-urls::(original),headline,summary,industry,email-address,positions)";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(c, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                callback.onSuccuss(apiResponse);

            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                callback.onError(liApiError);
            }
        });
    }
}
