package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models;

import android.content.Context;

import com.rhicstech.crutra.crutra.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 04/04/2018.
 */

public class JobShortlist {
    private String jobTitle;
    private String description;
    private ArrayList<Shortlist> shortlist;
    private int jobId;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Shortlist> getShortlist() {
        return shortlist;
    }

    public void setShortlist(ArrayList<Shortlist> shortlist) {
        this.shortlist = shortlist;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }


    public static class Shortlist{
        private int jobID;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getJobID() {
            return jobID;
        }

        public void setJobID(int jobID) {
            this.jobID = jobID;
        }
    }

    public static ArrayList<JobShortlist> getData(JSONArray array, Context context) throws JSONException {
        ArrayList<JobShortlist> datalist = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(array.getString(0));
        JSONArray datacode = jsonObject.getJSONArray("data");
        if(datacode != null ){
            for(int i = 0; i < datacode.length(); i++){
                JSONObject onejob = datacode.getJSONObject(i);
                JobShortlist jobShortlist  = new JobShortlist();
                jobShortlist.setJobId(onejob.getInt("id"));
                JSONObject desc = onejob.getJSONObject("desc");
                jobShortlist.setJobTitle(desc.getString("job_title"));
                JSONArray shotlistobj = onejob.getJSONArray("shotlisted");
                jobShortlist.setDescription(shotlistobj.length() + " " + context.getResources().getString(R.string.shotlist));
                ArrayList<Shortlist> sh = new ArrayList<>();
                if(shotlistobj.length() > 0){
                    for(int j =0; j < shotlistobj.length(); j++){
                        JSONObject jsonObject1 = shotlistobj.getJSONObject(j);
                        Shortlist shortlist1 = new Shortlist();
                        shortlist1.setId(jsonObject1.getInt("userId"));
                        shortlist1.setJobID(jsonObject1.getInt("job_id"));
                        sh.add(shortlist1);
                    }
                    jobShortlist.setShortlist(sh);
                    datalist.add(jobShortlist);
                }

            }
        }
        return datalist;
    }

}
