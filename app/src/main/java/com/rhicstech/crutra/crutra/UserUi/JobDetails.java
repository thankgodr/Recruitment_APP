package com.rhicstech.crutra.crutra.UserUi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Profile.SelectVideoActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;
import com.rhicstech.crutra.crutra.UserUi.Views.AlertBox;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.ButtonCallback;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.vincent.videocompressor.VideoCompress;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class JobDetails extends Fragment {
   JobModels jobModels;
   @BindView(R.id.jobTitle) TextView jobTitle;
   @BindView(R.id.jobDes)TextView jobDes;
   @BindView(R.id.requirements) TextView requirements;
   @BindView(R.id.apply) Button apply;
   @BindView(R.id.companyName) TextView companyNameAndAddress;
   @BindView(R.id.compannyImage) ImageView compannyImage;

   ProgressDialog pd;
   Alerts alerts;
   int status = 0;
    AlertDialog alertDialog;
    AlertBox.Builder tol;
    AlertBox alertBox;
    int selectFromProfile = 182;

    Bitmap bitmap;


    File saveFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle a = getArguments();
        Gson gson = new Gson();
        String s = a.getString("job");
        if(a.getString("image") != null){
            bitmap = decodeBase64(a.getString("image"));
        }
        saveFolder = new File(Environment.getDownloadCacheDirectory(),"videos");
        setHasOptionsMenu(true);
        saveFolder.mkdir();
        jobModels = gson.fromJson(s,JobModels.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);
        ButterKnife.bind(this,view);
        alerts = Alerts.getInstance(getContext());
        pd = alerts.progress();
        pd.show();
        jobTitle.setText(jobModels.getTitle());
        getActivity().setTitle(jobModels.getTitle().toUpperCase());
        jobDes.setText(jobModels.getDes());
        companyNameAndAddress.setText(jobModels.getCompanyName()+ " " + jobModels.getCompanyAddress());
        requirements.setText("No Requirement for Jobs");
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openButtons();
            }
        });
        compannyImage.setImageBitmap(bitmap);
        Constants.overrideFonts(getActivity(), view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(pd.isShowing()){
            pd.dismiss();
        }
        Constants.hideKeyboard(getView());
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if(alertBox != null){
                        if(alertBox.isShowing()){
                            alertBox.dismiss();
                        }
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    private void openButtons(){
      tol =  new AlertBox.Builder(getContext());
                tol.anchorView(apply)
                .contentView(R.layout.application_buttons)
                .gravity(Gravity.TOP)
                .animated(false)
                .transparentOverlay(true)
                .highlightShape(0)
                        .dismissOnInsideTouch(false);
        alertBox = tol.build();
        alertBox.show();


        View view = tol.getView();
         final Button upload = view.findViewById(R.id.upload);
         final Button record = view.findViewById(R.id.record);
         final Button select = view.findViewById(R.id.select);
         upload.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i("clicked", "Upload was clicked");
                 record.setBackgroundColor(Color.TRANSPARENT);
                 record.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 select.setBackgroundColor(Color.TRANSPARENT);
                 select.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 upload.setBackgroundColor(getContext().getResources().getColor(R.color.textDefault));
                 upload.setTextColor(Color.WHITE);
                 pickVideo();

             }
         });
         record.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 upload.setBackgroundColor(Color.TRANSPARENT);
                 upload.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 select.setBackgroundColor(Color.TRANSPARENT);
                 select.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 record.setBackgroundColor(getContext().getResources().getColor(R.color.textDefault));
                 record.setTextColor(Color.WHITE);
               recordVideo();

             }
         });
         select.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 upload.setBackgroundColor(Color.TRANSPARENT);
                 upload.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 record.setBackgroundColor(Color.TRANSPARENT);
                 record.setTextColor(getContext().getResources().getColor(R.color.grey2));
                 select.setBackgroundColor(getContext().getResources().getColor(R.color.textDefault));
                 select.setTextColor(Color.WHITE);
                 Intent intent3 = new Intent(getContext(), SelectVideoActivity.class);
                 Bundle b = new Bundle();
                 b.putBoolean("select", true);
                 intent3.putExtras(b);
                 startActivityForResult(intent3, selectFromProfile);
             }
         });

    }

    private void recordVideo(){
       Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
       intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,300);
       startActivityForResult(intent,Constants.record);
    }

    private void pickVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),Constants.pickVideo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.listview);
        MenuItem item2 = menu.findItem(R.id.mapview);
        MenuItem item3 = menu.findItem(R.id.filter);
        item2.setVisible(false);
        item.setVisible(false);
        item.setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.record) {
            if(resultCode == getActivity().RESULT_OK){
                Uri uri = data.getData();
                String path = getRealPathFromURI(uri);
                File file = new File(path);
                Log.i("ext", file.getAbsolutePath());
                getDetails(file);
            }
            else if(requestCode == getActivity().RESULT_CANCELED){

            }
        }
        if(requestCode == Constants.pickVideo) {
            if(resultCode == getActivity().RESULT_OK){
                if(data != null){
                    Uri uri2 = data.getData();
                    Log.i("data", uri2.getPath());
                   String path2 = getRealPathFromURI(uri2);
                    Log.i("data", path2);
                    File file2 = new File(path2);
                   getDetails(file2);
                }
                else{

                }


            }
            else if(requestCode == getActivity().RESULT_CANCELED){

            }
        }
        if(requestCode == selectFromProfile){
            if(resultCode == getActivity().RESULT_OK){
                Log.i("selected", String.valueOf(data.getIntExtra("vidId", 0)));
                initApply(data.getIntExtra("vidId", 0));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void getDetails(final File file){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.upload_video_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText videoTitle = dialogView.findViewById(R.id.vidtitle);
        RadioGroup radioGroup = dialogView.findViewById(R.id.statusGroup);
        Button submit = dialogView.findViewById(R.id.uploadbtn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoTitle.getText().toString().length() < 1){
                    videoTitle.setError(getResources().getString(R.string.thisfield));
                }
                else{

                        pd = alerts.progress();
                        pd.setMessage(getResources().getString(R.string.compress));
                        pd.show();
                        try {
                            final File temp = File.createTempFile("temp-file-name", getFileExtension(file));

                             VideoCompress.compressVideoLow(file.getAbsolutePath(), temp.getAbsolutePath(), new VideoCompress.CompressListener() {
                                @Override
                                public void onStart() {
                                    //Start Compress
                                    pd.setMessage(getResources().getString(R.string.prepareing));
                                }

                                @Override
                                public void onSuccess() {
                                    //Finish successfully
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                uploadImage(temp,status,videoTitle.getText().toString(),pd);
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

                                @Override
                                public void onFail() {
                                    //Failed
                                    pd.dismiss();
                                    Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                    View view = snack.getView();
                                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    view.setLayoutParams(params);
                                    snack.show();
                                }

                                @Override
                                public void onProgress(float percent) {
                                    //Progress
                                    pd.setMessage(getResources().getString(R.string.compress) + " " + Math.round(percent) + "%");
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            pd.dismiss();
                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            view.setLayoutParams(params);
                            snack.show();
                        }


                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.privatedtatus:
                        status = 0;
                        break;
                    case R.id.publicstatus:
                        status = 1;
                        break;
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();


    }

    public void uploadImage(File file, int status, String tittle, final ProgressDialog progressDialog) throws IOException {
        long totalSize = file.length();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(getResources().getString(R.string.uploading));
            }
        });

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video_file", file.getName(), RequestBody.create(MediaType.parse("video/*"), file))
                .addFormDataPart("video_title", tittle)
                .addFormDataPart("status",status + "")
                .build();


        final Request request = new Request.Builder()
                .url(Constants.baseUrl + "uploadvideo")
                .addHeader("authorization", "Bearer " + new UserAuth(getContext()).getToken())
                .post(requestBody)
                .build();

        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .build();
        try {
           final Response response = httpClient.newCall(request).execute();
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(response.code() == 201){
                       try {
                           String body = response.body().string();
                           JSONObject videoUpload = new JSONObject(body);
                           final int videoId = videoUpload.getInt("video");
                           Log.i("susess", videoId + "");
                           getActivity().runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   progressDialog.setMessage(getResources().getString(R.string.sendingApplication));
                               }
                           });
                           initApply(videoId, progressDialog);

                       } catch (IOException e) {
                           e.printStackTrace();
                           alerts.withButton(getResources().getString(R.string.error),getResources().getString(R.string.tryagian),getResources().getString(R.string.errorHappen),new ButtonCallback(){
                               @Override
                               public void startAction() {

                               }
                           });
                       } catch (JSONException e) {
                           e.printStackTrace();
                           alerts.withButton(getResources().getString(R.string.error),getResources().getString(R.string.tryagian),getResources().getString(R.string.errorHappen),new ButtonCallback(){
                               @Override
                               public void startAction() {

                               }
                           });
                       }
                   }
                   else{
                       alerts.withButton(getResources().getString(R.string.error),getResources().getString(R.string.tryagian),getResources().getString(R.string.errorHappen),new ButtonCallback(){
                           @Override
                           public void startAction() {

                           }
                       });
                   }
               }
           });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("result", e.getMessage());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }

    }

    private void initApply(final int videoId, final ProgressDialog progressDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   Response response1 = apply(videoId);
                   if(response1.code() == 201){
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               progressDialog.dismiss();
                               alerts.withButton(getResources().getString(R.string.success), getResources().getString(R.string.applicationSent), getResources().getString(R.string.continu), new ButtonCallback() {
                                   @Override
                                   public void startAction() {
                                       if(alertDialog != null){
                                           alertDialog.dismiss();
                                       }
                                       if(alertBox != null){
                                           alertBox.dismiss();
                                       }
                                       ((MainActivity)getActivity()).changeFragment(new ListJobs());
                                   }
                               });
                           }
                       });
                   }
                   else if(response1.code() == 303){
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               progressDialog.dismiss();
                               alerts.withButton(getResources().getString(R.string.applied), getResources().getString(R.string.alreadyApplied), getResources().getString(R.string.continu), new ButtonCallback() {
                                   @Override
                                   public void startAction() {

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
                               alerts.withButton(getResources().getString(R.string.error),getResources().getString(R.string.tryagian),getResources().getString(R.string.errorHappen),new ButtonCallback(){
                                   @Override
                                   public void startAction() {

                                   }
                               });
                           }
                       });
                   }

                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            alerts.withButton(getResources().getString(R.string.error),getResources().getString(R.string.tryagian),getResources().getString(R.string.errorHappen),new ButtonCallback(){
                                @Override
                                public void startAction() {

                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }
    private void initApply(final int videoId) {
        final ProgressDialog dialog = alerts.progress();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response1 = apply(videoId);
                    if(response1.code() == 201){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.applicationSent),Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();
                            }
                        });
                    }
                    else if(response1.code() == 303){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.alreadyApplied),Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();
                            }
                        });
                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.tryagian),Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.tryagian),Snackbar.LENGTH_LONG);
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
        dialog.dismiss();
    }

    Response apply(int videoId) throws IOException {
        String data = "job_id="+jobModels.getId()+"&video_id=" + videoId;
       return Connection.postconnectCustomwithToken("apply",data,"application/x-www-form-urlencoded",new UserAuth(getContext()).getToken());
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,      decodedByte.length);
    }




}
