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
import android.widget.TextView;

import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters.ApplicationAdapter;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.ApplicationModel;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hb.xvideoplayer.MxVideoPlayer;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {

   JSONArray applications;
   String data = "";
   ArrayList videoIds= new ArrayList();


    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.error) TextView error;
    ProgressDialog pd;
    int jobid ;

    Alerts alerts;


    public ApplicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String modelString = bundle.getString("key");
        jobid = bundle.getInt("id");
        try {
            applications = new JSONArray(modelString);
            for(int i = 0; i < applications.length(); i++){
                try {
                    JSONObject object = applications.getJSONObject(i);
                    videoIds.add(object.getInt("video_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_application, container, false);
        ButterKnife.bind(this,view);
        alerts = Alerts.getInstance(getContext());
        pd = alerts.progress();
        pd.setMessage(getResources().getString(R.string.pleasewait));
        pd.show();
        Log.i("job id id ", jobid + "");

        if(videoIds != null){
            if(videoIds.size() <= 0){
                pd.dismiss();
                error.setVisibility(View.VISIBLE);
                error.setText(getResources().getString(R.string.noapplication));
            }
            else{
                for(int j = 0; j < videoIds.size(); j++){
                    if(j != videoIds.size() - 1 ){
                        data += "videoid%5B"+j+"%5D="+videoIds.get(j)+"&";
                    }
                    else{
                        data += "videoid%5B"+j+"%5D="+videoIds.get(j);
                    }
                }

                Log.i("ddd", data);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = fetch();
                            if(response.code() == 200){
                                final String body = response.body().string();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            setUpRecyclerView(new JSONArray(body));
                                            pd.dismiss();
                                        } catch (JSONException e) {
                                            pd.dismiss();
                                            error.setText(getResources().getString(R.string.errorHappen));
                                            error.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                            else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        error.setText(getResources().getString(R.string.errorHappen));
                                        error.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    error.setText(getResources().getString(R.string.errorHappen));
                                    error.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                }).start();
            }

        }
        else{
            pd.dismiss();
            error.setVisibility(View.VISIBLE);
            error.setText(getResources().getString(R.string.noapplication));
        }

        Constants.overrideFonts(getActivity(), view);


        return view;
    }

    private Response fetch() throws IOException {
        return Connection.postconnectCustomwithToken("cvideo", data,"application/x-www-form-urlencoded",new UserAuth(getContext()).getToken());
    }

    private void setUpRecyclerView(JSONArray object) throws JSONException {
        Log.i("obj", String.valueOf(object));
        ApplicationAdapter adapter = new ApplicationAdapter(getContext(),getActivity(),ApplicationModel.getData(object), jobid);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onPause() {
        super.onPause();
        MxVideoPlayer.releaseAllVideos();

    }
}
