package com.rhicstech.crutra.crutra.Profile.SettingsUi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.rhicstech.crutra.crutra.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifactionSetingsFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.applicationNotification) RelativeLayout applicationNotification;
    @BindView(R.id.applationSwitch) Switch appllicationSwitch;
    @BindView(R.id.message) RelativeLayout messages;
    @BindView(R.id.messageSwitch) Switch messageSwitch;
    @BindView(R.id.sound) RelativeLayout sound;
    @BindView(R.id.soundSwitch) Switch soundSwitch;
    @BindView(R.id.vibrate) RelativeLayout vibrate;
    @BindView(R.id.vibrateSwitch) Switch vibrateSwitch;

    public NotifactionSetingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifaction_setings, container, false);
        ButterKnife.bind(this,view);
        applicationNotification.setOnClickListener(this);
        getActivity().setTitle(R.string.notification);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.applicationNotification:
                if(!appllicationSwitch.isActivated()){
                    appllicationSwitch.setOnCheckedChangeListener (null);
                    appllicationSwitch.setChecked(true);
                }
                else{
                    appllicationSwitch.setOnCheckedChangeListener (null);
                    appllicationSwitch.setChecked(false);
                }
                break;

        }
    }
}
