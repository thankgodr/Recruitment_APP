package com.rhicstech.crutra.crutra.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.jaychang.utils.StringUtils;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.CountryCodes;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.ServerConnections;
import com.rhicstech.crutra.crutra.CompanyUi.PostJobs;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.Network.NetworkStatus;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * Created by rhicstechii on 20/02/2018.
 */

public class UpdateAndContinue extends Fragment implements ConnectionReceiver.ConnectionReceiverListener  {

    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName) EditText lastName;
    @BindView(R.id.companyName) EditText companyName;
    @BindView(R.id.city) EditText city;
    @BindView(R.id.state) EditText state;
    @BindView(R.id.country)
    Spinner country;
    @BindView(R.id.phone) EditText phone;
    @BindView(R.id.industry) Spinner industry;
    @BindView(R.id.address) EditText address;
    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.fnamerow)
    RelativeLayout fnameRow;
    @BindView(R.id.lnamerow) RelativeLayout lnameRow;
    @BindView(R.id.companyRow) RelativeLayout comrow;
    @BindView(R.id.headline) EditText headline;
    @BindView(R.id.sumarry) EditText summary;
    @BindView(R.id.skip) Button skip;
    int type;
    Company company;
    User user;
    String sCompanyName, Sfname, sLaname, sState, sCity, sCountry, sPhone, sAddress, sHeadline, sSumary;
    int industryCode;
    ProgressDialog pd;
    Alerts alerts;
    String[] indus;
    ServerConnections serverConnections;
    private int done = 0;


    public UpdateAndContinue(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        getActivity().setTitle(getResources().getString(R.string.updateProfile));
        setHasOptionsMenu(true);
        if(bundle.get("userType") == null){
            type = 2;
        }
        else{
            type = bundle.getInt("userType");
        }

        serverConnections = new ServerConnections();
        alerts = Alerts.getInstance(getContext());

        if(type == 2){
            user = new UserAuth(getContext()).getUser();
        }
        else if(type ==1){
            company = new UserAuth(getContext()).getCompany();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_and_continue, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.Profile));

        if(type == 2){
            comrow.setVisibility(View.GONE);
            fnameRow.setVisibility(View.VISIBLE);
            lnameRow.setVisibility(View.VISIBLE);
            firstName.setText( StringUtils.capitalize(user.getFirstname()));
            lastName.setText( StringUtils.capitalize(user.getLastname()));
            city.setText( StringUtils.capitalize(user.getCity()));
            state.setText(user.getState());
            address.setText(user.getAddress());
            country.setSelection(getIndex(country, new CountryCodes().getCode(user.getCountry())));
            phone.setText(user.getPhone());
            headline.setText(user.getHeadline());
            summary.setText(user.getSummary());

        }
        else if(type == 1){
            comrow.setVisibility(View.VISIBLE);
            fnameRow.setVisibility(View.GONE);
            lnameRow.setVisibility(View.GONE);
            companyName.setText( StringUtils.capitalize(company.getCompanyName()));
            city.setText(company.getCity());
            state.setText(company.getState());
            address.setText(company.getAddress());
            phone.setText(company.getPhone());
            headline.setText(company.getHeadline());
            summary.setText(company.getSummary());

        }

        imageButtons();

        //Populate countries
        String[] locales = Locale.getISOCountries();
        List<String> countries = new ArrayList<>();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            countries.add(obj.getDisplayCountry());
        }
        final ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(countryAdapter);

        if(NetworkStatus.status(getContext())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd = alerts.progress();
                                pd.show();
                            }
                        });
                        indus =  industries();
                        // Creating adapter for spinner
                        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, indus);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                industry.setAdapter(dataAdapter);
                                pd.dismiss();
                                done = 2;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        Constants.overrideFonts(getActivity(), view);

        return view;
    }

    private void imageButtons(){
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPickImage();

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = alerts.progress();
                if(type == 1){
                    pd.show();
                    sCity = city.getText().toString();
                    sAddress = address.getText().toString();
                    sCompanyName = companyName.getText().toString();
                    sCountry = country.getSelectedItem().toString();
                    sPhone = phone.getText().toString();
                    sState = state.getText().toString();

                    if(vaildate(city,address,companyName,phone,state)){
                        Log.i("Indusid",industry.getSelectedItemPosition() + 1 + "" );
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Response response = serverConnections.companyUpdate(sCompanyName, industry.getSelectedItemPosition() + 1, getCountries2Letters(sCountry),sCity,sAddress,sPhone,getActivity());
                                    if(response.code() == 200 || response.code() == 201){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pd.dismiss();
                                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.profileUpdated),Snackbar.LENGTH_LONG);
                                                View view = snack.getView();
                                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                                params.gravity = Gravity.TOP;
                                                view.setLayoutParams(params);
                                                snack.show();
                                            }
                                        });
                                        new UserAuth(getContext()).refresh(getContext());
                                        proceed();
                                    }
                                    else{
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pd.dismiss();
                                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                                View view = snack.getView();
                                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                                params.gravity = Gravity.TOP;
                                                view.setLayoutParams(params);
                                                snack.show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    else{
                        pd.dismiss();
                    }


                }
                else if(type == 2){
                    Log.i("Indusid",industry.getSelectedItemPosition() + 1 + "" );
                    pd.show();
                    sCity = city.getText().toString();
                    sAddress = address.getText().toString();
                    Sfname = firstName.getText().toString();
                    sLaname = lastName.getText().toString();
                    sCountry = country.getSelectedItem().toString();
                    sPhone = phone.getText().toString();
                    sState = state.getText().toString();
                    sHeadline = headline.getText().toString();
                    sSumary = summary.getText().toString();

                    if(vaildate(city,address,lastName,firstName,phone,state,summary,headline)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Response response = serverConnections.UserUpdate(Sfname,sLaname,industry.getSelectedItemPosition() + 1, getCountries2Letters(sCountry),sCity,sAddress,sPhone,sHeadline,sSumary,"12/12/1991",getActivity());
                                    if(response.code() == 200 || response.code() == 201){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pd.dismiss();
                                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.profileUpdated),Snackbar.LENGTH_LONG);
                                                View view = snack.getView();
                                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                                params.gravity = Gravity.TOP;
                                                view.setLayoutParams(params);
                                                snack.show();
                                            }
                                        });
                                        new UserAuth(getContext()).refresh(getContext());
                                    }
                                    else{
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pd.dismiss();
                                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                                View view = snack.getView();
                                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                                params.gravity = Gravity.TOP;
                                                view.setLayoutParams(params);
                                                snack.show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    else{
                        pd.dismiss();
                    }

                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });
    }

    private void proceed(){
        ((CompanyMainActivity)getContext()).changeFragment(new PostJobs(),"postjob");
    }



    private void showImage(File uri){
        profile_image.setImageURI(Uri.fromFile(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Config.RC_PICK_IMAGES && resultCode != 0 && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            File file = new File(images.get(0).getPath());
            showImage(file);
        }
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("request", requestCode + "");



    }


    public void onPickImage() {
        // Click on image button
        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(false)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle(getResources().getString(R.string.album))           //  Folder title (works with FolderMode = true)
                .setImageTitle(getResources().getString(R.string.galery))         //  Image title (works with FolderMode = false)
                .setDoneTitle(getResources().getString(R.string.done))               //  Done button title
                .setLimitMessage(getResources().getString(R.string.limitSelection))    // Selection limit message
                .setMaxSize(1)                     //  Max images can be selected
                .setSavePath("ImagePicker")         //  Image capture folder name
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();
    }

    private boolean vaildate(EditText... editTexts){
        int i = 1;
        for (EditText editText: editTexts) {
            if(editText.getText().toString().length() < 1 || editText.getText() == null){
                editText.setError(getResources().getString(R.string.thisfield));
                i = 0;
            }
        }
        if(i == 0){
            return false;
        }
        else{
            return true;
        }
    }


    private  String[] industries() throws IOException, JSONException {

        String response = serverConnections.getIndustries();
        JSONArray jsonArray = new JSONArray(response);
        JSONArray jsonArray1 = jsonArray.getJSONArray(0);
        String[] indus = new String[jsonArray1.length()];
        for(int i =0; i < jsonArray1.length(); i++ ){
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            indus[jsonObject.getInt("id") - 1] = jsonObject.getString("industry_name");
        }

        Log.i("indus", indus[0]);
        return indus;
    }

    private String getCountries2Letters(String countryName){
        return new CountryCodes().getCode(countryName);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            if(done == 0){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd = alerts.progress();
                                    pd.show();
                                }
                            });
                            indus =  industries();
                            // Creating adapter for spinner
                            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, indus);
                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    industry.setAdapter(dataAdapter);
                                    pd.dismiss();
                                    done = 2;
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }

    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
}
