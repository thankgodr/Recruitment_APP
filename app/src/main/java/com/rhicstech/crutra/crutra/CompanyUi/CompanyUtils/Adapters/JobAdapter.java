package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.ApplicationFragment;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobListModel;
import com.rhicstech.crutra.crutra.R;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by rhicstechii on 26/02/2018.
 */

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {
    private Context cont;
    private List<JobListModel> mData;
    LayoutInflater inflater;



    public JobAdapter(List<JobListModel> data, Context context){
        this.mData = data;
        this.cont = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = inflater.inflate(R.layout.list_application_card,parent,false);
       MyViewHolder holder = new MyViewHolder(view);
       return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       JobListModel current = mData.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView companyImage;
        TextView jobtitle;
        TextView applicationNumbers;
        RelativeLayout job;
        public MyViewHolder(View itemView) {
            super(itemView);
            companyImage = itemView.findViewById(R.id.videoView);
            jobtitle = itemView.findViewById(R.id.jobTitle);
            applicationNumbers = itemView.findViewById(R.id.jobDes);
            job = itemView.findViewById(R.id.jobList);
        }

        public void setData(final JobListModel current, int position) {
            jobtitle.setText(current.getJobTitle());
            applicationNumbers.setText(current.getApplication().length()+ " "+ cont.getResources().getString(R.string.applications));
            job.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationFragment applicationFragment = new ApplicationFragment();
                    Bundle bundle = new Bundle();
                    String modelString = String.valueOf(current.getApplication());
                    bundle.putString("key", modelString);
                    bundle.putInt("id", current.getJobId());
                    applicationFragment.setArguments(bundle);
                    ((CompanyMainActivity)cont).changeFragment(applicationFragment,"application");
                }
            });
        }
    }
}
