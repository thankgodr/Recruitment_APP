package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobShortlist;
import com.rhicstech.crutra.crutra.CompanyUi.ShotlistViewFragment;
import com.rhicstech.crutra.crutra.R;

import java.util.ArrayList;

/**
 * Created by rhicstechii on 04/04/2018.
 */

public class ShortlistAdapter  extends RecyclerView.Adapter<ShortlistAdapter.MyViewHolder> {


    LayoutInflater layoutInflater;
    ArrayList<JobShortlist> mdata;
    Context context;


    public ShortlistAdapter(Context c, ArrayList<JobShortlist> data){
        this.layoutInflater = LayoutInflater.from(c);
        this.mdata = data;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_application_card,parent,false);
        ShortlistAdapter.MyViewHolder holder = new ShortlistAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JobShortlist shortlist = mdata.get(position);
        holder.setData(shortlist);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView jobtitle, jobDes;
        RelativeLayout jobpan;
        public MyViewHolder(View itemView) {
            super(itemView);
            jobtitle = itemView.findViewById(R.id.jobTitle);
            jobDes = itemView.findViewById(R.id.jobDes);
            jobpan = itemView.findViewById(R.id.jobList);
        }

        public void setData(final JobShortlist shortlist) {
            jobtitle.setText(shortlist.getJobTitle());
            jobDes.setText(shortlist.getDescription());
            jobpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openShortlistDetails(shortlist.getShortlist());
                }
            });
        }
        private void openShortlistDetails(ArrayList<JobShortlist.Shortlist> shortlistArrayList){
            ShotlistViewFragment shotlistViewFragment = new ShotlistViewFragment();
            Gson gson = new Gson();
            String info = gson.toJson(shortlistArrayList,new TypeToken<ArrayList<JobShortlist.Shortlist>>(){}.getType());
            Bundle b = new Bundle();
            b.putString("list", info);
            shotlistViewFragment.setArguments(b);
            Log.i("shortlist", info);
            ((CompanyMainActivity)context).changeFragment(shotlistViewFragment, "viewSh");
        }
    }

}
