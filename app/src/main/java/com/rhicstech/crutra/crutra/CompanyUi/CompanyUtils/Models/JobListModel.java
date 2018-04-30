package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 26/02/2018.
 */

public class JobListModel {
    private String jobTitle;
    private int jobId;
    private JSONArray application;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }


    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public static ArrayList<JobListModel> getData(JSONArray jsonArray) throws JSONException {
        ArrayList<JobListModel> datalist = new ArrayList<JobListModel>();
        if(jsonArray != null){
            JSONObject object = jsonArray.getJSONObject(0);
            if(object != null){
                JSONArray jsonArray1 = object.getJSONArray("data");
                if(jsonArray1 != null){
                    for(int i = 0; i < jsonArray1.length(); i++){
                        JSONObject mainObject = jsonArray1.getJSONObject(i);
                        JobListModel applicationModel = new JobListModel();
                        applicationModel.setApplication(mainObject.getJSONArray("applications"));
                        JSONObject desc = mainObject.getJSONObject("desc");
                        applicationModel.setJobId(desc.getInt("id"));
                        applicationModel.setJobTitle(desc.getString("job_title"));
                        datalist.add(applicationModel);
                    }
                }
            }
        }

        return datalist;
    }

    public JSONArray getApplication() {
        return application;
    }

    public void setApplication(JSONArray application) {
        this.application = application;
    }
}
