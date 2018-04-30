package com.rhicstech.crutra.crutra.messages.MessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 27/03/2018.
 */

public class CompanyContact {
    private String companyName;
    private int messagesNo;
    private String lastMessage;
    private int contactID;
    private String timeAgo;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getMessagesNo() {
        return messagesNo;
    }

    public void setMessagesNo(int messagesNo) {
        this.messagesNo = messagesNo;
    }

    public static ArrayList<CompanyContact> getContact(JSONArray array) throws JSONException {
        ArrayList<CompanyContact> datalist = new ArrayList<>();
        if(array != null){
            for(int i= 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                 CompanyContact contact = new CompanyContact();
                 contact.setCompanyName(object.getString("user"));
                 contact.setContactID(object.getInt("id"));
                 contact.setLastMessage(object.getString("last"));
                 contact.setTimeAgo(object.getString("time"));
                 datalist.add(contact);
            }
        }
        return datalist;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
