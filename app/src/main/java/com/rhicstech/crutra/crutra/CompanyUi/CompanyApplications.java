package com.rhicstech.crutra.crutra.CompanyUi;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters.JobAdapter;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobListModel;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyApplications extends Fragment  implements ConnectionReceiver.ConnectionReceiverListener {


    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.error)
    TextView error;
    ProgressDialog pd;


    Alerts alerts;



    public CompanyApplications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_company_applications, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.applications).toUpperCase());

        alerts = Alerts.getInstance(getContext());
        pd = alerts.progress();
        pd.setMessage(getResources().getString(R.string.fetchingUrJobs));
        pd.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = fetchJobs();
                    if(response.code() == 200){
                        final String body = response.body().string();
                        Log.i("res", body);
                        if(body == null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    error.setVisibility(View.VISIBLE);
                                    error.setText(getResources().getString(R.string.youHaveNoJobs));
                                }
                            });
                        }
                        else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        setUpRecyclerView(new JSONArray(body));
                                        pd.dismiss();
                                    } catch (JSONException e) {
                                        pd.dismiss();
                                        error.setVisibility(View.VISIBLE);
                                        error.setText(getResources().getString(R.string.youHaveNoJobs));
                                    }
                                }
                            });
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            error.setVisibility(View.VISIBLE);
                            error.setText(getResources().getString(R.string.errorHappen));
                        }
                    });

                }
            }
        }).start();
        Constants.overrideFonts(getActivity(), view);

        return view;
    }

    private Response fetchJobs() throws IOException {
        return Connection.getconnectWithToken("applications", new UserAuth(getContext()).getToken());
    }


   //HANDLE CONNECTION LISTENER
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            error.setText(getResources().getString(R.string.nointernate));
            error.setVisibility(View.VISIBLE);
        }
        if(isConnected){
            error.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView(JSONArray object) throws JSONException {
        Log.i("obj", String.valueOf(object));
        JobAdapter adapter = new JobAdapter(JobListModel.getData(object),getContext());
            recyclerView.setAdapter(adapter);
            LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
            mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


}
