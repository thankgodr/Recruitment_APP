package com.rhicstech.crutra.crutra.messages;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.messages.Adapter.MessageAdapter;
import com.rhicstech.crutra.crutra.messages.MessageModel.Messages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Messaging extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    MessageAdapter adapter = null;
    @BindView(R.id.typing) EditText typing;
    @BindView(R.id.send)
    ImageView send;
    UserAuth userAuth;
    int ReceiverId;
    Alerts alerts;
    ProgressDialog dialog;
    Timer t;
    String kname;


    public Messaging() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            ReceiverId = bundle.getInt("user");
            kname = bundle.getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        ButterKnife.bind(this, view);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#07656B'>"+kname+" </font>"));
        alerts = Alerts.getInstance(getContext());
        userAuth = new UserAuth(getContext());
        //Start dialog
        dialog = alerts.progress();
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                 String data = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"otherUser\"\r\n\r\n"+ReceiverId+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
                try {
                    Response response = Connection.postconnectCustomwithToken("fetchmessage",data,"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW",new UserAuth(getContext()).getToken());
                    if(response.code() == 200){
                        final String body = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setUpRecyclerView(new JSONArray(body));
                                    repeat();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }

            }
        }).start();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mes = typing.getText().toString();
                String pattern = "(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)";
                mes = mes.replaceAll(pattern,"");
                if(mes.length() < 1){

                }
                else{
                    final Messages sendMess = new Messages();
                    sendMess.setName("Test Namw");
                    sendMess.setMessage(mes);
                    sendMess.setIsReceiver(3);
                    if(userAuth.getUserType() == 1){
                        sendMess.setSenderId(userAuth.getCompany().getId());
                    }
                    else{
                        sendMess.setSenderId(userAuth.getUser().getId());
                    }
                    sendMess.setReceiverId(ReceiverId);
                    sendMess.setDate(new Date().toGMTString());
                    addMessage(sendMess);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SendMessage2ser(sendMess);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    typing.getText().clear();
                    typing.setText("");
                }
            }
        });

        return view;
    }

    private void setUpRecyclerView(JSONArray object)  {
        try {
            adapter = new MessageAdapter(getContext() ,Messages.getMessages(object, getContext()),kname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // Even if we dont use it then also our items shows default animation. #Check Docs
        if(dialog != null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    private void addMessage(Messages messagesToAdd){
        adapter.addItem(messagesToAdd);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private void SendMessage2ser(Messages msg) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"receiver\"\r\n\r\n"+msg.getReceiverId()+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"message\"\r\n\r\n"+msg.getMessage()+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        Request request = new Request.Builder()
                .url("http://13.250.43.203/api/message")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("authorization", "Bearer " + userAuth.getToken())
                .build();
        Response response = client.newCall(request).execute();
    }

    public void repeat(){
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                update();

            }
        }, 0, 7000);

    }

    private void update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"otherUser\"\r\n\r\n"+ReceiverId+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
                try {
                    Response response = Connection.postconnectCustomwithToken("fetchmessage",data,"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW",new UserAuth(getContext()).getToken());
                    if(response.code() == 200){
                        final String body = response.body().string();
                        Log.i("email",body);
                        if(getActivity() != null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONArray a = new JSONArray(body);
                                        JSONObject o = a.getJSONObject(0);
                                        if(recyclerView.getAdapter().getItemCount() < o.length()){
                                            recyclerView.setAdapter(null);
                                            dialog = alerts.progress();
                                            dialog.show();
                                            setUpRecyclerView(a);
                                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                   if(getActivity() != null){
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               if(dialog != null){
                                   dialog.dismiss();
                               }
                           }
                       });
                   }
                }

            }
        }).start();
    }


    @Override
    public void onPause() {
        super.onPause();
        if(t != null){
            t.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(t != null){
            t.cancel();
        }
    }
}
