package com.rhicstech.crutra.crutra.CompanyUi;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters.ShortlistAdapter;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.JobShortlist;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
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
public class ShorListFragment extends Fragment implements ConnectionReceiver.ConnectionReceiverListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Alerts alerts;
    ProgressDialog dialog;
    public ShorListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shor_list, container, false);
        ButterKnife.bind(this, view);
        alerts = Alerts.getInstance(getContext());
        dialog = alerts.progress();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = Connection.getconnectWithToken("shotlist",new UserAuth(getContext()).getToken());
                    if(response.code() == 200){
                        final JSONArray jsonArray = new JSONArray(response.body().string());
                        if(getActivity() != null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        setUpRecyclerView(jsonArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(getActivity() != null){
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(getActivity() != null){
                        dialog.dismiss();
                    }
                }
            }
        }).start();

        return view;
    }

    private void setUpRecyclerView(JSONArray jsonArray) throws JSONException {
        ShortlistAdapter adapter = new ShortlistAdapter(getContext(), JobShortlist.getData(jsonArray,getContext()));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(dialog != null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }
}
