package com.rhicstech.crutra.crutra.Profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.PostJobs;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.Network.ServerAuth;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.ButtonCallback;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.job.JobInstace;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.Response;

/**
 * Created by rhicstechii on 15/02/2018.
 */

public class Payment extends Fragment implements ConnectionReceiver.ConnectionReceiverListener {
    @BindView(R.id.packages)
    Spinner packagee;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.jobVol) TextView jobvolume;
    @BindView(R.id.paynow)
    Button paynow;
    ProgressDialog pd;
    Alerts alerts;
    String[] names;
    String[] prices;
    int[] pricesId;
    int[] jobVolumes;
    ServerAuth auth ;
    String email;
    int packageInt;

    final int GET_NEW_CARD = 224;
    final int sqareRequest = 348;

    JobInstace jobInstace;
    public Payment() {
        super();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            jobInstace = (JobInstace) bundle.getSerializable("job");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.payment));

        PaystackSdk.initialize(getContext());

        //Helpers
        alerts = Alerts.getInstance(getContext());
        pd = alerts.progress();
        pd.show();
        auth = new ServerAuth();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONArray packages = getPackages();
                    email = auth.getUserEmail(new UserAuth(getContext()).getToken());
                     names = new String[packages.length()];
                     prices = new String[packages.length()];
                     jobVolumes = new int[packages.length()];
                     pricesId = new int[packages.length()];
                    for(int i = 0; i < packages.length(); i++){
                        JSONObject object = packages.getJSONObject(i);
                        names[i] = object.getString("name");
                        prices[i] = object.getString("price");
                        jobVolumes[i] = object.getInt("volume");
                        pricesId[i] = object.getInt("id");
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            packagee.setAdapter(countryAdapter);
                            price.setText("$".toUpperCase() + prices[0]);
                            jobvolume.setText(jobVolumes[0] + "" );
                            packageInt = pricesId[0];
                            pd.dismiss();
                            Log.i("email",email);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        changePrice();


        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCountry() == "Nigeria"){
                    Intent intent = new Intent(getContext(), CardEditActivity.class);
                    startActivityForResult(intent,GET_NEW_CARD);
                }else{
                    //Todo pass to check if country is foreign
                    Intent intent = new Intent(getContext(), CardEditActivity.class);
                    startActivityForResult(intent,GET_NEW_CARD);
                }

            }
        });
        Constants.overrideFonts(getActivity(), view);

        return view;
    }


    private JSONArray getPackages() throws IOException, JSONException {
       Response response = Connection.getconnect("packages");
       String body =  response.body().string();
       JSONArray first = new JSONArray(body);
       JSONArray all = first.getJSONArray(0);
       return all;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void changePrice(){

        packagee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                price.setText("$".toUpperCase() + prices[position]);
                jobvolume.setText(jobVolumes[position] + "");
                packageInt = pricesId[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

           if(requestCode == GET_NEW_CARD){
               String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
               String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
               String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
               String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

               String[] dateArr = expiry.split("/");

               pd = alerts.progress();
               pd.show();

               Card card = new Card(cardNumber,Integer.parseInt(dateArr[0]),Integer.parseInt(dateArr[1]),cvv,cardHolderName);
               if (card.isValid()) {
                   performCharge(card, pd);
               } else {
                  pd.dismiss();
                   Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.invalidCardDetails),Snackbar.LENGTH_LONG);
                   View view = snack.getView();
                   FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                   params.gravity = Gravity.TOP;
                   view.setLayoutParams(params);
                   snack.show();
               }

           }




    }

    public void performCharge(Card card, final ProgressDialog progressDialog){
        //create a Charge object
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge
        charge.setAmount(Math.round(Float.parseFloat(price.getText().toString().substring(1)) * 100));
        charge.setEmail(email);
        charge.setReference(email+"_"+timestamp.getTime());

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(final Transaction transaction) {
                final int AmmoutTo = Math.round(Float.parseFloat(price.getText().toString().substring(1)));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int res = increaseLimits(transaction.getReference(),AmmoutTo,packageInt);
                            if(res == Constants.createdCode){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alerts.withButton(getActivity().getResources().getString(R.string.success), getActivity().getResources().getString(R.string.paymentSuccess), getActivity().getResources().getString(R.string.continu), new ButtonCallback() {
                                            @Override
                                            public void startAction() {
                                                if(jobInstace != null){
                                                    PostJobs postJobs = new PostJobs();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("job",jobInstace);
                                                    postJobs.setArguments(bundle);
                                                    ((CompanyMainActivity)getActivity()).changeFragment(postJobs);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alerts.withButton(getActivity().getResources().getString(R.string.success), getActivity().getResources().getString(R.string.paymentSuccessWithError), getActivity().getResources().getString(R.string.contactSupport), new ButtonCallback() {
                                            @Override
                                            public void startAction() {

                                            }
                                        });
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

            @Override
            public void beforeValidate(final Transaction transaction) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            createPaymentOnServer(transaction.getReference());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                progressDialog.dismiss();
                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.trasactionError),Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }

        });
    }



    private int createPaymentOnServer(String ref) throws IOException {
        Response response = Connection.postconnectCustomwithToken("newpayment","transactionRef=" + ref,"application/x-www-form-urlencoded", new UserAuth(getContext()).getToken());
        return response.code();
    }

    private int increaseLimits(String ref, int amount, int id ) throws IOException {
        String body = "ref="+ref+"&amount="+amount+"&package_id=" + id;
        Response response = Connection.postconnectCustomwithToken("increase",body,"application/x-www-form-urlencoded",new UserAuth(getContext()).getToken());
        return response.code();
    }

    private String getCountry(){
        String country_name = null;
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getContext());
        for(String provider: lm.getAllProviders()) {
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if(location!=null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addresses != null && addresses.size() > 0) {
                        country_name = addresses.get(0).getCountryName();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return country_name;
    }
}
