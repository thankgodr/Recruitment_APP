package com.rhicstech.crutra.crutra.UserUi;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Adapters.JobRecyclerView;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.RecyclerViewPositionHelper;
import com.rhicstech.crutra.crutra.searchUi.SearchFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListJobs extends Fragment implements ConnectionReceiver.ConnectionReceiverListener {

   @BindView(R.id.recyclerView) RecyclerView recyclerView;
   @BindView(R.id.error)
    TextView error;
   ProgressDialog pd;
   @BindView(R.id.searchHoler)
    LinearLayout searchHoler;
   @BindView(R.id.search) EditText search;


    Alerts alerts;

    public ListJobs() {
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
        View view = inflater.inflate(R.layout.fragment_list_jobs, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.listJob));
        alerts = Alerts.getInstance(getContext());
        pd = alerts.progress();
        pd.show();
        search.clearFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         search.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 LayoutInflater inflater = getLayoutInflater();
                 View alertLayout = inflater.inflate(R.layout.fragment_search, null);
                 AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                 alert.setView(alertLayout);
                 alert.setCancelable(true);
                 alert.show();
             }
         });



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = fetchJobs();
                    if(response.code() == Constants.okay){
                        String body = response.body().string();
                        JSONArray array = new JSONArray(body);
                        JSONObject object = array.getJSONObject(0);
                        final JSONArray data = object.getJSONArray("data");
                        Log.i("dataGett", String.valueOf(data));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpRecyclerView(data);
                                pd.dismiss();
                                error.setText("");
                                error.setVisibility(View.GONE);
                            }
                        });

                    }
                    else if(response.code() == 203){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                error.setText(getResources().getString(R.string.nojobAv));
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
                } catch (JSONException e) {
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
        Constants.overrideFonts(getActivity(), view);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constants.hideKeyboard(getView());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView1, int newState) {
                super.onScrollStateChanged(recyclerView1, newState);
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    searchHoler.setVisibility(View.GONE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView1.getLayoutManager();
                    if(layoutManager.findFirstVisibleItemPosition() == 0){
                        searchHoler.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            error.setText(getResources().getString(R.string.nointernate));
            error.setVisibility(View.VISIBLE);
        }
        else{
            error.setVisibility(View.GONE);
            error.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        alerts = Alerts.getInstance(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.listview);
        item.setVisible(false);
    }

    private Response fetchJobs() throws IOException {
        return Connection.getconnect("jobs");
    }
    private void setUpRecyclerView(JSONArray object)  {
        JobRecyclerView adapter = null;
        try {
            adapter = new JobRecyclerView(getContext(), JobModels.getData(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // Even if we dont use it then also our items shows default animation. #Check Docs
    }
}
