package com.rhicstech.crutra.crutra.Network;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rhicstech.crutra.crutra.Utils.ConvertJsonToUser;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class ServerAuth {



    public ServerAuth(){
    }

    public User getUsersProfile(String token) throws IOException {
       Response response = Connection.getconnectWithToken("profile", token);
        Log.i("serverCodoe", response.code() +"");
           String body =response.body().string();
           Log.i("server", token);
           Log.i("server", body);
           ConvertJsonToUser convertJsonToUser = new ConvertJsonToUser(body);
           return convertJsonToUser.convertUser();
    }

    public Company getCompanyProfile(String token) throws IOException {
        Response response = Connection.getconnectWithToken("profile", token);
        Log.i("serverCodoe", response.code() +"");
        Company company = null;
        if(response.code() == 200){
            String body =response.body().string();
            Log.i("server", token);
            Log.i("server", body);
            ConvertJsonToUser convertJsonToUser = new ConvertJsonToUser(body);
            company = convertJsonToUser.convertCompany();
        }
        return company;
    }

    public int getUserType(String token) throws IOException {
        Response response = Connection.getconnectWithToken("auth/me",token);
        int type = 0;
        if(response.code() == 200) {
            String body = response.body().string();
            Log.i("server", body);
            JsonObject jsonObj = new JsonParser().parse(body).getAsJsonObject();
            type = jsonObj.get("account_type").getAsInt();
            Log.i("server", type + " ");
        }
        return type;
    }

    public String getUserEmail(String token) throws IOException {
        Response response = Connection.getconnectWithToken("auth/me",token);
        String type = "";
        if(response.code() == 200) {
            String body = response.body().string();
            Log.i("server", body);
            JsonObject jsonObj = new JsonParser().parse(body).getAsJsonObject();
            type = jsonObj.get("email").getAsString();
            Log.i("server", type + " ");
        }
        return type;
    }




}
