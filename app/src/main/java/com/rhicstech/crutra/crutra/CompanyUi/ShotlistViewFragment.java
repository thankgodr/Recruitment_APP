package com.rhicstech.crutra.crutra.CompanyUi;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters.ShortlistAdapter;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters.UserListAdapter;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobShortlist;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShotlistViewFragment extends Fragment {

    ArrayList<JobShortlist.Shortlist> jobslist;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    Alerts alerts;
    ProgressDialog dialog;

    public ShotlistViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String listString = bundle.getString("list");
        Gson gson = new Gson();
        jobslist = gson.fromJson(listString, new TypeToken<ArrayList<JobShortlist.Shortlist>>(){}.getType());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_shotlist_view, container, false);

        //Start the progress
        Log.i("number", jobslist.size() + "");
        alerts = Alerts.getInstance(getContext());
        dialog = alerts.progress();
        dialog.show();



        ButterKnife.bind(this,view);


        try {
            setUpRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        return view;
    }

    private void setUpRecyclerView() throws JSONException {
        UserListAdapter adapter = new UserListAdapter(jobslist,getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(dialog != null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

}
