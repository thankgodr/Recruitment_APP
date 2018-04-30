package com.rhicstech.crutra.crutra.videoui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.messages.Messaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import hb.xvideoplayer.MxVideoPlayer;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayer extends Fragment {

    String videourl;
    int userId;
    int jobid;
    String nameOfApplicant;
    @BindView(R.id.mpw_video_player)
    MxVideoPlayer videoPlayer;
    @BindView(R.id.fullname)
    TextView fullname;
    @BindView(R.id.headline)
    TextView headline;
    @BindView(R.id.summary)
    TextView sumarry;
    @BindView(R.id.shorlist)
    Button shorlist;
    @BindView(R.id.message)
    Button message;
    @BindView(R.id.decline)
    Button decline;
    @BindView(R.id.interview)
    Button interview;
    Bundle bundle;
    Alerts alerts;
    ProgressDialog dialog;

    public VideoPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            videourl = bundle.getString("videourl");
            userId = bundle.getInt("userid");
            jobid = bundle.getInt("jobid");
            nameOfApplicant = bundle.getString("name");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        videoPlayer.startPlay(videourl.replaceAll("https", "http"), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, getResources().getString(R.string.applicantVidoe));
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messaging messaging = new Messaging();
                Bundle bundle = new Bundle();
                bundle.putInt("user", userId);
                messaging.setArguments(bundle);
                ((CompanyMainActivity) getContext()).changeFragment(messaging, "messaging");
            }
        });
        alerts = Alerts.getInstance(getContext());
        dialog = alerts.progress();
        fullname.setText(nameOfApplicant);

        if (!checkPermission()) {
            requestPermission();
        }

        shorlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"userID\"\r\n\r\n" + userId + "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"jobID\"\r\n\r\n" + jobid + "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
                        try {
                            Response response = Connection.postconnectCustomwithToken("cshotlist", data, "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW", new UserAuth(getContext()).getToken());
                            if (response.code() == 201) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), getResources().getString(R.string.nointernate), Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                            shorlist.setEnabled(false);
                                            shorlist.setText(R.string.shotlisted);
                                        }
                                    });
                                }
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), getResources().getString(R.string.alereadyshotlisted), Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                            shorlist.setEnabled(false);
                                            shorlist.setText(R.string.shotlisted);
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                }).start();
            }
        });

        interview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
                                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePickerDialog view, final int hourOfDay, final int minute, int second) {
                                        if (dialog == null) {
                                            dialog = alerts.progress();
                                        }

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                        alertDialog.setTitle(getContext().getResources().getString(R.string.location));
                                        alertDialog.setMessage(getContext().getResources().getString(R.string.enterInterviewLocation));
                                        alertDialog.setCancelable(false);

                                        final EditText input = new EditText(getContext());
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                        input.setLayoutParams(lp);
                                        alertDialog.setView(input);

                                        alertDialog.setPositiveButton(getContext().getResources().getString(R.string.dialog_ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                       String password = input.getText().toString();
                                                        if (password.length() > 0) {
                                                            addEvent(year, monthOfYear, dayOfMonth, hourOfDay, minute,nameOfApplicant,password);
                                                        }
                                                        else{
                                                            Toast.makeText(getActivity(),getContext().getResources().getString(R.string.invalidLocation),Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                        alertDialog.setNegativeButton("NO",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        alertDialog.show();



                                    }
                                }, false);
                                timePickerDialog.show(getActivity().getFragmentManager(), "timepiker");


                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        MxVideoPlayer.releaseAllVideos();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 100);
            }
        }
    }

    private void addEvent(int year, int month, int date, int hour, int min, String user, String location){
        Calendar cal = Calendar.getInstance();
        cal.set(year,month,date,hour,min);
        Intent intent = new Intent(Intent.ACTION_INSERT, Uri.parse("content://com.android.calendar/events"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", getContext().getResources().getString(R.string.interviewWith) + " " + user);
        startActivityForResult(intent, 370);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 370){
            if(dialog == null){
                dialog = alerts.progress();
            }
            dialog.setMessage(getContext().getResources().getString(R.string.submitInterview));
            Log.i("data", String.valueOf(data));
            dialog.dismiss();
        }
    }
}
