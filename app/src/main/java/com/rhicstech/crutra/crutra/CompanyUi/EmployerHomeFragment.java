package com.rhicstech.crutra.crutra.CompanyUi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.Profile.CallenderFragment;
import com.rhicstech.crutra.crutra.Profile.Payment;
import com.rhicstech.crutra.crutra.Profile.SettingsHome;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.messages.MessageListFragment;
import com.rhicstech.crutra.crutra.messages.Messaging;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerHomeFragment extends Fragment implements View.OnClickListener {


    public EmployerHomeFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.postjob) LinearLayout postjob;
    @BindView(R.id.candidates) LinearLayout candidates;
    @BindView(R.id.jobs) LinearLayout jobs;
    @BindView(R.id.favourites) LinearLayout favourites;
    @BindView(R.id.messages) LinearLayout messages;
    @BindView(R.id.search) LinearLayout search;
    @BindView(R.id.interview) LinearLayout interview;
    @BindView(R.id.shortlist) LinearLayout shorlist;
    @BindView(R.id.training) LinearLayout training;
    @BindView(R.id.report) LinearLayout reports;
    @BindView(R.id.notifications) LinearLayout notifications;
    @BindView(R.id.settings) LinearLayout settings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employer_home, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.app_name));
        postjob.setOnClickListener(this);
        candidates.setOnClickListener(this);
        jobs.setOnClickListener(this);
        favourites.setOnClickListener(this);
        messages.setOnClickListener(this);
        search.setOnClickListener(this);
        interview.setOnClickListener(this);
        training.setOnClickListener(this);
        shorlist.setEnabled(true);
        reports.setOnClickListener(this);
        notifications.setOnClickListener(this);
        settings.setOnClickListener(this);
        Constants.overrideFonts(getActivity(), view);
        shorlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CompanyMainActivity)getContext()).changeFragment(new ShorListFragment(),"shortlist");
            }
        });

        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postjob:
                ((CompanyMainActivity)getContext()).changeFragment(new PostJobs(), "postjobs");
                break;
            case R.id.candidates:
                ((CompanyMainActivity)getContext()).changeFragment(new CompanyApplications(),"applications");
                break;
            case R.id.jobs:
                ((CompanyMainActivity)getContext()).changeFragment(new CompanyApplications(),"applications");
                break;
            case R.id.favourites:
                //TODO add favorite fragment
                break;
            case R.id.messages:
                //TODO messages fragment
                ((CompanyMainActivity)getContext()).changeFragment(new MessageListFragment(), "messageList ");
                break;
            case R.id.search:
                //Todo Saerch Fragment
                break;
            case R.id.interview:
                ((CompanyMainActivity)getContext()).changeFragment(new CallenderFragment(),"calender");
                break;
            case  R.id.training:
                //TOdo Training Fragment
                break;
            case R.id.report:
                //Todo Report Fragment
                break;
            case R.id.notifications:
                //Todo Notifications Fragments
                ((CompanyMainActivity)getContext()).changeFragment(new Payment(),"payment");
                break;
            case R.id.settings:
                //Todo Seetings Fragments
                Bundle arg = new Bundle();
                arg.putInt("userId",1);
                SettingsHome settingsHome = new SettingsHome();
                settingsHome.setArguments(arg);
                ((CompanyMainActivity)getActivity()).changeFragment(settingsHome, "settings");
                break;
        }

    }
}
