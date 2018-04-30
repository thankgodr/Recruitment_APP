package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils;

import android.content.Context;
import android.util.Log;

import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by rhicstechii on 15/02/2018.
 */

public class ServerConnections {

    public ServerConnections(){

    }

    public String getIndustries() throws IOException {
        Response response = Connection.getconnect("industries");
        return  response.body().string();
    }

    public Response UserUpdate(String fname, String lname, int indus, String country, String city, String address, String phone,String headline,String sumary,String dob, Context context) throws IOException {
        Log.i("Indusid2",indus + "" );
        String data = "last_name="+lname+"&first_name="+fname+"&date_of_birth="+dob+"&industry_id="+indus+"&country="+country+"&city="+city+"&address="+address+"&phone_number=" + phone +"&headline=" + headline+"&summary=" + sumary;
        Response response = Connection.postconnectCustomwithToken("update",data,"application/x-www-form-urlencoded", new UserAuth(context).getToken());
        return response;
    }

    public Response companyUpdate(String name, int indus, String country, String city, String address, String phone,Context context) throws IOException {
        Log.i("Indusid2",indus + "" );
        String data = "company_name="+name+"&industry_id="+indus+"&country="+country+"&city="+city+"&address="+address+"&phone_number=" + phone;
        Response response = Connection.postconnectCustomwithToken("update",data,"application/x-www-form-urlencoded", new UserAuth(context).getToken());
        return response;
    }
}
