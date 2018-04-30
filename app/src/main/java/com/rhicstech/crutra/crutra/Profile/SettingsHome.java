package com.rhicstech.crutra.crutra.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Profile.SettingsUi.AccountFragment;
import com.rhicstech.crutra.crutra.Profile.SettingsUi.NotifactionSetingsFragment;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsHome extends Fragment implements View.OnClickListener{

    @BindView(R.id.notification) TextView notication;
    @BindView(R.id.general) TextView general;
    @BindView(R.id.account) TextView accounts;
    @BindView(R.id.privacy) TextView privacy;
    @BindView(R.id.block) TextView block;
    @BindView(R.id.help) TextView help;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.edit) Button Edit;
    int useCase;
    UserAuth userAuth;


    public SettingsHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        useCase = bundle.getInt("userId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_home, container, false);
        ButterKnife.bind(this,view);
        userAuth = new UserAuth(getContext());

        if(useCase == 1){
            Company company = userAuth.getCompany();
            name.setText(company.getCompanyName());
            location.setText(company.getCountry());
        }
        else{
            User user = userAuth.getUser();
            name.setText(user.getFirstname());
            location.setText(user.getCountry());
        }
        Edit.setOnClickListener(this);
        notication.setOnClickListener(this);
        accounts.setOnClickListener(this);
        general.setOnClickListener(this);
        privacy.setOnClickListener(this);
        Constants.overrideFonts(getActivity(), view);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit:
            case R.id.general:
                if(useCase == 1){
                    UpdateProfile updateProfile = new UpdateProfile();
                    Bundle bundle = new Bundle();
                    bundle.putInt("userType", 1);
                    updateProfile.setArguments(bundle);
                    ((CompanyMainActivity)getContext()).changeFragment(updateProfile,"update");
                }
                else{
                    UpdateProfile updateProfile = new UpdateProfile();
                    Bundle bundle = new Bundle();
                    bundle.putInt("userType", 2);
                    updateProfile.setArguments(bundle);
                    ((MainActivity)getContext()).changeFragment(updateProfile,"update");
                }
                break;
            case R.id.notification:
                if(useCase == 1){
                    ((CompanyMainActivity)getActivity()).changeFragment(new NotifactionSetingsFragment(), "notifications");
                }
                else{
                    ((MainActivity)getActivity()).changeFragment(new NotifactionSetingsFragment(), "notifications");
                }
                break;
            case R.id.account:
                if(useCase == 1){
                    ((CompanyMainActivity)getActivity()).changeFragment(new AccountFragment(), "account");
                }
                else{
                    ((MainActivity)getActivity()).changeFragment(new AccountFragment(), "account");
                }
                break;
            case R.id.privacy:
                if(useCase == 1){
                    ((CompanyMainActivity)getActivity()).changeFragment(new UserProfileFragment(), "ProfileManagement");
                }else{
                    ((MainActivity)getActivity()).changeFragment(new UserProfileFragment(), "ProfileManagement");
                }
                break;

        }
    }
}
