package com.rhicstech.crutra.crutra.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.Network.ServerAuth;
import com.rhicstech.crutra.crutra.auth.LoginActivity;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import java.io.IOException;

/**
 * Created by rhicstechii on 15/01/2018.
 */

public class UserAuth {


     static Context c;

    public  UserAuth(Context context){
        this.c = context;
    }




    public  void saveUser(User localuser){
        Gson gson = new Gson();
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        String userString = gson.toJson(localuser,User.class);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.prefUser,userString);
        Log.i("userString", userString);
        editor.putInt(Constants.prefUserType, Constants.userInt);
        editor.commit();
    }

    public  User getUser(){
        Gson gson = new Gson();
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        String stringU = preferences.getString(Constants.prefUser,"");
        Log.i("user", stringU);
        User user = gson.fromJson(stringU,User.class);
        return user;
    }

    public   void saveCompany(Company localCompany){
        Gson gson = new Gson();
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        String userString = gson.toJson(localCompany,Company.class);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.prefUser,userString);
        Log.i("ComString", userString);
        editor.putInt(Constants.prefUserType, Constants.companyInt);
        editor.commit();
    }

    public  Company getCompany(){
        Gson gson = new Gson();
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        String stringU = preferences.getString(Constants.prefUser,"");
        Company user = gson.fromJson(stringU,Company.class);
        return user;
    }

    public int getUserType(){
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        int stringU = preferences.getInt(Constants.prefUserType,0);
        return stringU;
    }



    public   boolean isLogin(){
        Gson gson = new Gson();
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        String stringU = preferences.getString(Constants.prefUser,"");
        if(stringU.length() < 3){
            return false;
        }
        else{
            return true;
        }
    }

    public void saveToken(String token){
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.prefToken,token);
        editor.commit();

    }

    public String getToken(){
        SharedPreferences preferences = c.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        return preferences.getString(Constants.prefToken,"");
    }

    public void Logout(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }


    public void refresh(Context context) throws IOException {
        ServerAuth auth = new ServerAuth();
        int companyOrUser = auth.getUserType(getToken());
        if(companyOrUser == 1){
            Company compnayProfile = auth.getCompanyProfile(getToken());
            UserAuth userAuth = new UserAuth(context);
            userAuth.saveCompany(compnayProfile);
        }
        else if(companyOrUser == 2) {
            //its a user or applicant
            User userProfile = auth.getUsersProfile(getToken());
            Log.i("user", userProfile + "");
            UserAuth userAuth = new UserAuth(context);
            userAuth.saveUser(userProfile);
        }
    }



}
