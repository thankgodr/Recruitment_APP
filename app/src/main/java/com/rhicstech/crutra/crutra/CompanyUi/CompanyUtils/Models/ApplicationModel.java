package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rhicstechii on 26/02/2018.
 */

public class ApplicationModel implements Serializable {
    private String headline;
    private String fullname;
    private String summary;
    private String email;
    private String videourl;
    private String imageUrr;
    private int jobId;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getImageUrr() {
        return imageUrr;
    }

    public void setImageUrr(String imageUrr) {
        this.imageUrr = imageUrr;
    }
    public static ArrayList<ApplicationModel> getData(JSONArray jsonArray) throws JSONException {
        ArrayList<ApplicationModel> datalist = new ArrayList<>();
        if(jsonArray != null){
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                ApplicationModel model = new ApplicationModel();
                model.setEmail(object.getString("eamil"));
                model.setFullname(object.getString("fullname"));
                model.setHeadline(object.getString("headline"));
                model.setImageUrr(object.getString("picUrl"));
                model.setSummary(object.getString("summary"));
                model.setVideourl(object.getString("videourl"));
                datalist.add(model);
            }
        }
        return datalist;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
