package com.rhicstech.crutra.crutra.UserUi.UserUtils.Models;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.CountryCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 16/02/2018.
 */

public class JobModels {
    private String title;
    private String des;
    private int id;
    private String locatiion;
    private String imgurl;
    private String companyName;
    private String companyAddress;
    private String jobImageUrl;
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocatiion() {
        return locatiion;
    }

    public void setLocatiion(String locatiion) {
        this.locatiion = locatiion;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public static ArrayList<JobModels> getData(JSONArray jobs) throws JSONException {
        ArrayList<JobModels> dataList = new ArrayList<>();
        if(jobs != null){
            for(int i =0; i< jobs.length(); i++){
                JSONObject jsonObject = jobs.getJSONObject(i);
                if(jsonObject.get("desc") != null){
                    JSONObject desc = jsonObject.getJSONObject("desc");
                    JobModels jobModels = new JobModels();
                    jobModels.setJobImageUrl(jsonObject.getString("uri"));
                    jobModels.setDes(desc.getString("summary"));
                    jobModels.setId(desc.getInt("id"));
                    jobModels.setTitle(desc.getString("job_title"));
                    jobModels.setLocatiion(desc.getString("job_city") + ", " + getCountryName(desc.getString("job_country")) );

                    if(jsonObject.get("company").toString().length() > 9){
                        JSONObject company = jsonObject.getJSONObject("company");
                        jobModels.setImgurl(company.getString("profile_pix_uri"));
                        jobModels.setCompanyName(company.getString("company_name"));
                        jobModels.setCompanyAddress(company.getString("city") + " "+ new CountryCodes().getKeyByValue(company.getString("country")));
                    }

                    dataList.add(jobModels);
                }
            }
        }

        return dataList;
    }

    private static String getCountryName(String twoLetter){
        CountryCodes codes = new CountryCodes();
        return codes.getKeyByValue(twoLetter);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getJobImageUrl() {
        return jobImageUrl;
    }

    public void setJobImageUrl(String jobImageUrl) {
        this.jobImageUrl = jobImageUrl;
    }
}
