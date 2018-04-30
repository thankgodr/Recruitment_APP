package com.rhicstech.crutra.crutra.Profile.Model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 20/03/2018.
 */

public class VideoModel {
    private String name;
    private String url;
    private int id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public static ArrayList<VideoModel> getVideos(JSONArray jsonArray) throws JSONException {
        ArrayList<VideoModel> dataList = new ArrayList<>();
        JSONArray jsonArray1 = jsonArray.getJSONArray(0);
        for(int i = 0; i < jsonArray1.length(); i++){
            JSONObject object = jsonArray1.getJSONObject(i);
            VideoModel videoModel = new VideoModel();
            //TOdo rename string index
            videoModel.setName(object.getString("video_title").toUpperCase());
            videoModel.setUrl(object.getString("video_uri"));
            videoModel.setId(object.getInt("id"));
            dataList.add(videoModel);
        }
        return dataList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
