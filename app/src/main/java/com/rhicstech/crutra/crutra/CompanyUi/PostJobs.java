package com.rhicstech.crutra.crutra.CompanyUi;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.jota.autocompletelocation.AutoCompleteLocation;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.CountryCodes;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.ServerConnections;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.Network.NetworkStatus;
import com.rhicstech.crutra.crutra.Profile.Payment;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.ButtonCallback;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.auth.LoginActivity;
import com.rhicstech.crutra.crutra.job.JobInstace;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostJobs extends Fragment implements ConnectionReceiver.ConnectionReceiverListener {

    @BindView(R.id.title) EditText title;
    @BindView(R.id.city) AutoCompleteLocation city;
    @BindView(R.id.state) EditText state;
    @BindView(R.id.country) Spinner country;
    @BindView(R.id.summary) EditText sumary;
    @BindView(R.id.deadLine) DatePickerEditText deadline;
    @BindView(R.id.industry) Spinner industry;
    @BindView(R.id.submit) Button submit;
    @BindView(R.id.upload)
    ImageView upload;
    String stitle, sCity, sCountry, sState,sSummary, sDeadline;
    int industryCode;
    ProgressDialog pd;
    Alerts alerts;
    String[] indus;
    JobInstace returnJobInstace;
    int done =0;
    File file2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            returnJobInstace = (JobInstace) bundle.getSerializable("job");
        }

    }

    //Helpers
    ServerConnections serverConnections;


    public PostJobs() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_post_jobs, container, false);
        ButterKnife.bind(this,view);
        deadline.setManager(getFragmentManager());
        serverConnections = new ServerConnections();
        alerts = Alerts.getInstance(getContext());
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#07656B'>"+getResources().getString(R.string.postJob)+" </font>"));
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideo();
            }
        });




        //Populate countries
        String[] locales = Locale.getISOCountries();
        final List<String> countries = new ArrayList<>();
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

       btnClicks();
        Constants.overrideFonts(getActivity(), view);

        city.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {

            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                try {
                    LatLng latLng = selectedPlace.getLatLng();
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {

                        String TAG = "place";

                        String Caddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String Cstate = addresses.get(0).getAdminArea();
                        String Ccity = addresses.get(0).getSubAdminArea();
                        String Ccountry = addresses.get(0).getCountryName();
                        String CpostalCode = addresses.get(0).getPostalCode();

                        //Set To selected address
                        countryAdapter.insert(Ccountry,0);
                        countryAdapter.notifyDataSetChanged();
                        state.setText(Cstate);
                        sCity = Ccity;






                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        return  view;
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

    private void btnClicks(){
       pd = alerts.progress();

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(validate()){
                   pd.show();
                 sCountry = getCountries2Letters(country.getSelectedItem().toString());
                 sDeadline = deadline.getText().toString();
                 sState = state.getText().toString();
                 sSummary = sumary.getText().toString();
                 stitle = title.getText().toString();
                 industryCode = industry.getSelectedItemPosition() + 1;
                 if(sCity == null ){
                     sCity = sState;
                 }
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               getActivity().runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       pd.dismiss();
                                   }
                               });
                               if(file2 != null){
                                   submit(file2);
                               }
                               else{
                                   submit();
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
           }
       });
    }

    private boolean validate(){
        //Todo Validate form data

        return true;
    }

    /*private void submit() throws IOException {

        String datatype = "application/x-www-form-urlencoded";
        String body = "title="+stitle+"r&city="+sCity+"&state="+sState+"&summary="+sSummary+"&country="+sCountry+"&deadline="+sDeadline+"&industry_id="+industryCode;
        UserAuth auth = new UserAuth(getContext());
        Log.i("country", sCountry);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = alerts.progress();
                pd.show();
            }
        });

       Response response = Connection.postconnectCustomwithToken("postjop",body,datatype,auth.getToken());
       Log.i("code", response.code() + "");
       switch (response.code()){
           case Constants.NoPaymentYet:
               ((CompanyMainActivity)getActivity()).changeFragment(new Payment());
               break;
           case Constants.okay:
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       alerts.withButton(getActivity().getResources().getString(R.string.success), getActivity().getResources().getString(R.string.jobPosted), getActivity().getResources().getString(R.string.postAgain), new ButtonCallback() {
                           @Override
                           public void startAction() {
                               ((CompanyMainActivity)getActivity()).changeFragment(new PostJobs());
                           }
                       });
                   }
               });
               break;
           default:
               ((CompanyMainActivity)getActivity()).changeFragment(new Payment());
               break;
       }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        });
    }*/

    public void submit(File file) throws IOException {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = alerts.progress();
                pd.show();
            }
        });
        String filename = "";
        if(file != null){
          filename = file.getName();
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", filename, RequestBody.create(MediaType.parse("image/*"), file))
                .addFormDataPart("title", stitle)
                .addFormDataPart("city",sCity)
                .addFormDataPart("state",sState)
                .addFormDataPart("summary",sSummary)
                .addFormDataPart("country",sCountry)
                .addFormDataPart("deadline",sDeadline)
                .addFormDataPart("industry_id", industryCode +"")
                .build();


        final Request request = new Request.Builder()
                .url(Constants.baseUrl + "postjop")
                .addHeader("authorization", "Bearer " + new UserAuth(getContext()).getToken())
                .post(requestBody)
                .build();

        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .build();
        try {
            final Response response = httpClient.newCall(request).execute();
            Log.i("postjob", response.code() + "");
            Log.i("postjob", response.body().string());
            switch (response.code()){

                case Constants.NoPaymentYet:
                    JobInstace jobInstace = new JobInstace();
                    jobInstace.setTitle(stitle);
                    jobInstace.setCity(sCity);
                    jobInstace.setState(sState);
                    jobInstace.setSummary(sSummary);
                    jobInstace.setCountry(sCountry);
                    jobInstace.setDeadline(sDeadline);
                    jobInstace.setIndustry(industryCode);
                    jobInstace.setFile(file);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("job",jobInstace);
                    Payment payment = new Payment();
                    payment.setArguments(bundle);
                    ((CompanyMainActivity)getActivity()).changeFragment(payment);
                    break;
                case Constants.okay:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alerts.withButton(getActivity().getResources().getString(R.string.success), getActivity().getResources().getString(R.string.jobPosted), getActivity().getResources().getString(R.string.postAgain), new ButtonCallback() {
                                @Override
                                public void startAction() {
                                    ((CompanyMainActivity)getActivity()).changeFragment(new PostJobs());
                                }
                            });
                        }
                    });
                    break;
                default:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alerts.withButton(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.errorHappen), getActivity().getResources().getString(R.string.postAgain), new ButtonCallback() {
                                @Override
                                public void startAction() {
                                    ((CompanyMainActivity)getActivity()).changeFragment(new PostJobs());
                                }
                            });
                        }
                    });
                    break;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("result", e.getMessage());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
        }

    }
    public void submit() throws IOException {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = alerts.progress();
                pd.show();
            }
        });
        String filename = "";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", stitle)
                .addFormDataPart("city",sCity)
                .addFormDataPart("state",sState)
                .addFormDataPart("summary",sSummary)
                .addFormDataPart("country",sCountry)
                .addFormDataPart("deadline",sDeadline)
                .addFormDataPart("industry_id", industryCode +"")
                .build();


        final Request request = new Request.Builder()
                .url(Constants.baseUrl + "postjop")
                .addHeader("authorization", "Bearer " + new UserAuth(getContext()).getToken())
                .post(requestBody)
                .build();

        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .build();
        try {
            final Response response = httpClient.newCall(request).execute();
            Log.i("postjob", response.code() + "");
            Log.i("postjob", response.body().string());
            switch (response.code()){

                case Constants.NoPaymentYet:
                    JobInstace jobInstace = new JobInstace();
                    jobInstace.setTitle(stitle);
                    jobInstace.setCity(sCity);
                    jobInstace.setState(sState);
                    jobInstace.setSummary(sSummary);
                    jobInstace.setCountry(sCountry);
                    jobInstace.setDeadline(sDeadline);
                    jobInstace.setIndustry(industryCode);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("job",jobInstace);
                    Payment payment = new Payment();
                    payment.setArguments(bundle);
                    ((CompanyMainActivity)getActivity()).changeFragment(payment);
                    break;
                case Constants.okay:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alerts.withButton(getActivity().getResources().getString(R.string.success), getActivity().getResources().getString(R.string.jobPosted), getActivity().getResources().getString(R.string.postAgain), new ButtonCallback() {
                                @Override
                                public void startAction() {
                                    ((CompanyMainActivity)getActivity()).changeFragment(new PostJobs());
                                }
                            });
                        }
                    });
                    break;
                default:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alerts.withButton(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.errorHappen), getActivity().getResources().getString(R.string.postAgain), new ButtonCallback() {
                                @Override
                                public void startAction() {
                                    ((CompanyMainActivity)getActivity()).changeFragment(new PostJobs());
                                }
                            });
                        }
                    });
                    break;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("result", e.getMessage());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        alerts = Alerts.getInstance(getContext());
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   indus = industries();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }).start();
       if(returnJobInstace != null){
           Log.i("jobinstace", String.valueOf(returnJobInstace));



       }
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
        else if(!isConnected){
            submit.setEnabled(false);
            pd.dismiss();
            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG);
            View view = snack.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public static String join(String[] strings, int startIndex, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i=startIndex; i < strings.length; i++) {
            if (i != startIndex) sb.append(separator);
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    private void pickVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Select Job Image"),Constants.pickImage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.pickImage){
            if(resultCode == getActivity().RESULT_OK){
                if(data != null){
                    Uri uri2 = data.getData();
                    Log.i("data", uri2.getPath());
                    String path2 = getRealPathFromURI(uri2);
                    Log.i("data", path2);
                    file2 = new File(path2);
                    upload.setImageURI(uri2);
                }
                else{

                }


            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
