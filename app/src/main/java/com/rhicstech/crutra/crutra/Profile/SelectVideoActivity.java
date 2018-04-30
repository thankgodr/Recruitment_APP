package com.rhicstech.crutra.crutra.Profile;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Profile.Adapter.VideoAdapter;
import com.rhicstech.crutra.crutra.Profile.Model.VideoModel;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Adapters.JobRecyclerView;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class SelectVideoActivity extends AppCompatActivity {

    Alerts alerts;
    ProgressDialog progressDialog;
    boolean selectOrNot = false;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);
        ButterKnife.bind(this);
        alerts = Alerts.getInstance(this);
        progressDialog = alerts.progress();
        progressDialog.show();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            selectOrNot = bundle.getBoolean("select");
        }
        init();
        Constants.overrideFonts(SelectVideoActivity.this, getWindow().getDecorView().getRootView());

    }

    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = Connection.getconnectWithToken("getvideo", new UserAuth(SelectVideoActivity.this).getToken());
                    if(response.code() == 200){
                        final String body = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setUpRecyclerView(new JSONArray(body));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setUpRecyclerView(JSONArray object)  {
        VideoAdapter adapter = null;
        try {
            adapter = new VideoAdapter(this, SelectVideoActivity.this, selectOrNot , VideoModel.getVideos(object));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("json", e.getMessage());
        }
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // Even if we dont use it then also our items shows default animation. #Check Docs
        //remove the loader
        progressDialog.dismiss();
    }
}
