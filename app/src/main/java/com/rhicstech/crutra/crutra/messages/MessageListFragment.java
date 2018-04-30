package com.rhicstech.crutra.crutra.messages;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.messages.Adapter.ListUserForMessage;
import com.rhicstech.crutra.crutra.messages.MessageModel.CompanyContact;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageListFragment extends Fragment {


    public MessageListFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Alerts alerts;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this,view);
        alerts = Alerts.getInstance(getContext());
        progressDialog = alerts.progress();
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = Connection.getconnectWithToken("fetchcontacts",new UserAuth(getContext()).getToken());
                    if(response.code() == 200){
                        final String body = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setUpRecyclerView(new JSONArray(body));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                   getActivity().runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           progressDialog.dismiss();
                       }
                   });
                }
            }
        }).start();




        return view;
    }


    private void setUpRecyclerView(JSONArray array) throws JSONException {
        ListUserForMessage adapter = new ListUserForMessage(getContext(), CompanyContact.getContact(array));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(progressDialog != null){
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

}
