package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobShortlist;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by rhicstechii on 06/04/2018.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    ArrayList<JobShortlist.Shortlist> mData ;
    LayoutInflater inflater;
    Context c;

    public UserListAdapter(ArrayList<JobShortlist.Shortlist> shortlistArrayList, Context context){
        this.mData = shortlistArrayList;
        inflater = LayoutInflater.from(context);
        this.c =  context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_card,parent, false);
        UserListAdapter.MyViewHolder holder = new UserListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JobShortlist.Shortlist shortlist = mData.get(position);
        holder.setData(shortlist);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public void setData(JobShortlist.Shortlist shortlist) {

        }

        private Response getUserInfomation(int userId) throws IOException {
            String postData = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"id\"\r\n\r\n"+userId+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
            Response response = Connection.postconnectCustomwithToken("getpro",postData,"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW", new UserAuth(c).getToken());
            return response;

        }
    }


}
