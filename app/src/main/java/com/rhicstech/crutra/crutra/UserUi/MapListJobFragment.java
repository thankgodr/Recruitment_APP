package com.rhicstech.crutra.crutra.UserUi;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;
import com.rhicstech.crutra.crutra.UserUi.Views.MapDetails;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListJobFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    Alerts alerts;
    ProgressDialog pd;
    String bod;
    LatLng p1 = null;

    public MapListJobFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_list_job, container, false);
        ButterKnife.bind(this, view);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        alerts = Alerts.getInstance(getContext());
        getActivity().setTitle(getResources().getString(R.string.mapview).toUpperCase());
        pd = alerts.progress();
        pd.show();

        Constants.overrideFonts(getActivity(), view);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((MainActivity)getActivity()).requestPermission();
        }
        mMap.setMyLocationEnabled(true);
        //LatLng center =getLocationFromAddress(getContext(),"victoria isaland, Lagos");
        // Add a marker in Sydney, Australia, and move the camera.
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,14));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = fetchJobs();
                    if(response.code() == Constants.okay){
                        String body = response.body().string();
                        JSONArray array = new JSONArray(body);
                        JSONObject object = array.getJSONObject(0);
                        JSONArray data = object.getJSONArray("data");
                        final ArrayList<JobModels> jobModels = JobModels.getData(data);
                        ArrayList<LatLng> geos = new ArrayList<>();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Marker> markers = new ArrayList<>();
                                for(JobModels model: jobModels){
                                    Log.i("tag", String.valueOf(jobModels));
                                    if(getLocationFromAddress(getContext(),model.getLocatiion()) != null){
                                        LatLng loc = getLocationFromAddress(getContext(),model.getLocatiion());
                                        Gson gson = new Gson();
                                        MarkerOptions mark = new MarkerOptions().position(loc).title(model.getTitle().toUpperCase()).snippet(gson.toJson(model,JobModels.class));
                                        markers.add(mMap.addMarker(mark));
                                        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            @Override
                                            public void onInfoWindowClick(Marker marker) {
                                                openJobDetails(marker);
                                            }
                                        });
                                    }
                                }

                                Location myLocation = mMap.getMyLocation();

                                if (myLocation != null) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(myLocation.getLatitude(), myLocation
                                                    .getLongitude()), 14));
                                }
                                 //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationFromAddress(getContext(),jobModels.get(0).getLocatiion()),14));
                                /*
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (Marker marker : markers) {
                                        builder.include(marker.getPosition());
                                    }
                                    LatLngBounds bounds = builder.build();
                                    int padding = 0; // offset from edges of the map in pixels
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    mMap.animateCamera(cu);
                                    if(pd != null){
                                        if(pd.isShowing()){
                                            pd.dismiss();
                                        }
                                    }
                                */
                                if(pd != null){
                                    if(pd.isShowing()){
                                        pd.dismiss();
                                    }
                                }
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
                } catch (JSONException e) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constants.hideKeyboard(getView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.listview);
        MenuItem item2 = menu.findItem(R.id.mapview);
        MenuItem item3 = menu.findItem(R.id.filter);
        item2.setVisible(false);
        item.setVisible(true);
        item3.setVisible(false);
    }

    public LatLng getLocationFromAddress(Context context, final String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;


        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            if(address.size() > 0){
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            }

        } catch (IOException ex) {
            ex.printStackTrace();
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         Response response = Connection.getconnect("lat/" + strAddress);
                         if(response.code() == 200){
                            bod = response.body().string();
                             JSONObject temLatlon = new JSONObject(bod);
                             p1 = new LatLng(temLatlon.getDouble("lat"), temLatlon.getDouble("long"));
                         }
                     } catch (IOException e) {
                         e.printStackTrace();
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }).start();
        }
        return p1;
    }

    private Response fetchJobs() throws IOException {
        return Connection.getconnect("jobs");
    }

    private void openJobDetails(Marker marker){
        Gson gson = new Gson();
        final JobModels jobModels = gson.fromJson(marker.getSnippet(), JobModels.class);
        final MapDetails mapDetails = new MapDetails(getContext());
        mapDetails.setContentView(R.layout.map_dialog_content);
        TextView jobtitle = mapDetails.findViewById(R.id.jobTitle);
        TextView companName = mapDetails.findViewById(R.id.companyName);
        TextView description = mapDetails.findViewById(R.id.jobDes);
        TextView requirement = mapDetails.findViewById(R.id.requirements);
        Button viewore = mapDetails.findViewById(R.id.apply);
        ImageButton cancel = mapDetails.findViewById(R.id.btnCancel);
        jobtitle.setText(jobModels.getTitle());
        companName.setText(jobModels.getCompanyName()+ " " + jobModels.getCompanyAddress());
        description.setText(jobModels.getDes());
        requirement.setText("No requirement yet");
        viewore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Bundle bundle = new Bundle();
                String job = gson.toJson(jobModels,JobModels.class);
                bundle.putString("job",job);
                JobDetails jobDetails = new JobDetails();
                jobDetails.setArguments(bundle);
                ((MainActivity)getContext()).changeFragment(jobDetails,"jobdetails");
                mapDetails.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDetails.dismiss();
            }
        });
        mapDetails.setCancelable(false);
        mapDetails.show();
    }


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View eachView;
        JobModels model;


        MyInfoWindowAdapter(){
            eachView = getLayoutInflater().inflate(R.layout.map_view_list, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            Gson gson = new Gson();
            model = gson.fromJson(marker.getSnippet(),JobModels.class);
            TextView currerentTitle = eachView.findViewById(R.id.jobTitle);
            TextView currentDes = eachView.findViewById(R.id.jobDes);
            TextView currentLocation = eachView.findViewById(R.id.location);
            currerentTitle.setText(model.getTitle());
            currentDes.setText(model.getDes());
            currentLocation.setText(model.getLocatiion());
            return eachView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }





    }





}
