package com.rhicstech.crutra.crutra.Utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rhicstechii on 16/01/2018.
 */

public class ConvertJsonToUser {
    JSONObject userObjecr;
    JSONArray arr;
    public ConvertJsonToUser(String jsonString) {
        Log.i("convert", jsonString);

        try {
            this.arr = new JSONArray(jsonString);
            this.userObjecr = this.arr.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User convertUser(){
        User user = new User();
        try {
            user.setFirstname(userObjecr.getString("first_name"));
            user.setAddress(userObjecr.getString("address"));
            user.setLastname(userObjecr.getString("last_name"));
            user.setMiddlename(userObjecr.getString("middle_name"));
            user.setPhone(userObjecr.getString("phone_number"));
            user.setCity(userObjecr.getString("city"));
            user.setId(userObjecr.getInt("id"));
            user.setCountry(userObjecr.getString("country"));
            user.setHeadline(userObjecr.getString("headline"));
            if(userObjecr.getString("industry_id") == "null"){
                user.setIndustry_id(0);
            }
            else{
                user.setIndustry_id(userObjecr.getInt("industry_id"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
            return user;
    }

    public Company convertCompany(){
        Company user = new Company();
        try {
            user.setCompanyName(userObjecr.getString("company_name"));
            user.setAddress(userObjecr.getString("address"));
            user.setPhone(userObjecr.getString("phone_number"));
            user.setCity(userObjecr.getString("city"));
            user.setId(userObjecr.getInt("id"));
            user.setCountry(userObjecr.getString("country"));
            user.setHeadline(userObjecr.getString("headline"));
            if(userObjecr.getString("industry_id") == "null"){
                user.setIndustry_id(0);
            }
            else{
                user.setIndustry_id(userObjecr.getInt("industry_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

            return user;

    }



}
