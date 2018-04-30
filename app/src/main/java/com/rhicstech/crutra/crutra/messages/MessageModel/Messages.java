package com.rhicstech.crutra.crutra.messages.MessageModel;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 26/03/2018.
 */

public class Messages {
    private String message;
    private String imageUrl;
    private String name;
    private String date;
    private int isReceiver;
    private String ago;
    private int senderId;
    private int receiverId;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getisReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(int receiver) {
        isReceiver = receiver;
    }

    public static ArrayList<Messages> getMessages(JSONArray array,Context c) throws JSONException {
        UserAuth userAuth = new UserAuth(c);
        ArrayList<Messages> dataList = new ArrayList<>();
        int id = 20;
        Gson gson = new Gson();
        if(userAuth.getUserType() == Constants.companyInt){
            id = userAuth.getCompany().getId();
            Log.i("companyID", gson.toJson(userAuth.getCompany(), Company.class) );
        }
        else if(userAuth.getUserType() == Constants.userInt){
            id = userAuth.getUser().getId();
            Log.i("userId", gson.toJson(userAuth.getUser(), User.class));
        }
        if(array != null){
            JSONObject parentObject = array.getJSONObject(0);
            for(int i = 0; i < parentObject.length(); i++){
                Messages mes = new Messages();
                JSONObject mesObj = parentObject.getJSONObject(i + "");
                Log.i("userid", id + "");
                if(id == mesObj.getInt("sender_id")){
                    mes.setIsReceiver(3);
                }
                else{
                    mes.setIsReceiver(1);
                }
                mes.setMessage(mesObj.getString("message"));
                mes.setDate(mesObj.getString("created_at"));
                mes.setAgo(mesObj.getString("ago"));
                dataList.add(mes);
            }
        }
        return dataList;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }
}
