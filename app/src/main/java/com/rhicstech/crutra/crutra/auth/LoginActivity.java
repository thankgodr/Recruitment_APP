package com.rhicstech.crutra.crutra.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaychang.sa.SocialUser;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.NetworkStatus;
import com.rhicstech.crutra.crutra.Network.ServerAuth;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.Utils.Validations.EmailValidate;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.FacebookCallback;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.LinkedinCallback;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.LinkedinUser;
import com.rhicstech.crutra.crutra.auth.Social.Facebook;
import com.rhicstech.crutra.crutra.auth.Social.Linkedin;
import com.rhicstech.crutra.crutra.auth.intro.Introduction;
import com.rhicstech.crutra.crutra.user.Company;
import com.rhicstech.crutra.crutra.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.facebook)
    ImageView facebook;
    @BindView(R.id.twitter) ImageView twitter;
    @BindView(R.id.logo) TextView logo;
    @BindView(R.id.slogan) TextView slogan;
    @BindView(R.id.user) EditText userName;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.login) Button login;
    @BindView(R.id.forgotPass) TextView forgotPass;
    @BindView(R.id.register) TextView register;
    @BindView(R.id.linkedin) ImageView linkedin;
    ProgressDialog pd;
    Linkedin linkedinSingleton;

    //Helpers
    Alerts alerts;
    Facebook fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Typeface hevetica = Typeface.createFromAsset(getAssets(), "helveticaNeue.ttf");

        //Set fonts for all views helveticaNeue
        slogan.setTypeface(hevetica);
        userName.setTypeface(hevetica);
        password.setTypeface(hevetica);
        login.setTypeface(hevetica);
        forgotPass.setTypeface(hevetica);
        register.setTypeface(hevetica);

        //Intialize helpers
        alerts = Alerts.getInstance(LoginActivity.this);
        fb = Facebook.getInstance();
        linkedinSingleton = Linkedin.getInstance(LoginActivity.this);





        LinearLayout linearLayout = findViewById(R.id.front);
        linearLayout.bringToFront();
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

           btnClicks();

        Constants.overrideFonts(LoginActivity.this, getWindow().getDecorView().getRootView());
        Typeface face = Typeface.createFromAsset(getAssets(),
                "anabel.ttf");
        logo.setTypeface(face);



    }


    //Start running button listener on new threads

    private void btnClicks(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        plogin();
                    }
                });

                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFacebookLogin();
                    }
                });

                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTwitter();
                    }
                });

                linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Like","CLicked");
                        linkedinSingleton.linkdinStart(new LinkedinCallback() {
                            @Override
                            public void onstart() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd = alerts.progress();
                                        pd.show();
                                    }
                                });
                            }

                            @Override
                            public void onSuccuss(ApiResponse response) {
                                LinkedinUser linkedinUser = new LinkedinUser();
                                JSONObject object = response.getResponseDataAsJson();
                                try {
                                    linkedinUser.setEmail(object.getString("emailAddress"));
                                    linkedinUser.setToken(LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue());
                                    //JSONObject picsObject = object.getJSONObject("pictureUrls");
                                    //JSONArray pics = picsObject.getJSONArray("values");
                                   // linkedinUser.setPicUrl(pics.getString(0) +"");
                                    Log.i("token", linkedinUser.accessToken);
                                    loginToServer(linkedinUser,pd);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            // Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                            Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onError(LIApiError error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        // Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                        Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                        View view = snack.getView();
                                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                        params.gravity = Gravity.TOP;
                                        view.setLayoutParams(params);
                                        snack.show();
                                    }
                                });
                            }
                        });

                    }
                });

            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        register();
                    }
                });
                forgotPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getResources().getString(R.string.resetPasswordUrl)));
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }


    private void plogin(){
        if(validate()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            pd = alerts.progress();
            final String email = userName.getText().toString();
            final String pass = password.getText().toString();
            if(NetworkStatus.status(this)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.show();
                            }
                        });

                        try {
                            final Response response = Connection.postconnect("auth/login", "{\"email\": \""+email+"\",\"password\": \""+pass+"\"}" );
                            if(response.code() == 200){
                                String body = response.body().string();
                                final JSONObject jsonObject = new JSONObject(body);
                                final String token = jsonObject.getString("token");
                                try {
                                    success(token);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        pd.dismiss();
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                       // Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                        Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.invalidLogin),Snackbar.LENGTH_LONG);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    // Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                    Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                    View view = snack.getView();
                                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    view.setLayoutParams(params);
                                    snack.show();

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    // Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                    Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
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
            else{
                pd.dismiss();
                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();            }


        }
    }

    private void register(){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private void resetpassword(String email){

    }

    private boolean validate(){
        EmailValidate validate = new EmailValidate();
        if(!validate.validate(userName.getText().toString())){
           userName.setError(getResources().getString(R.string.invalidEmail));
           return false;
        }
        else if(password.getText().toString().length() < 3){
            password.setError(getResources().getString(R.string.invalidPasswprd));
            return false;
        }
        else{
            return true;
        }
    }

    private void success(String token) throws IOException, JSONException {
        Log.i("token", token);
        ServerAuth auth = new ServerAuth();
        int companyOrUser = auth.getUserType(token);
        if(companyOrUser == 1){
            //its a company
            Company compnayProfile = auth.getCompanyProfile(token);
            UserAuth userAuth = new UserAuth(LoginActivity.this);
            userAuth.saveCompany(compnayProfile);
            userAuth.saveToken(token);
            beginCompanyMain();


        }
        else if(companyOrUser == 2){
            //its a user or applicant
            User userProfile = auth.getUsersProfile(token);
            Log.i("user", userProfile + "");
            UserAuth userAuth = new UserAuth(LoginActivity.this);
            userAuth.saveUser(userProfile);
            userAuth.saveToken(token);
            beginMain();
        }
        else{
            //Return 0 or admin which is not allowed in app
            //Toast.makeText(this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();

            Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
            View view = snack.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();

        }
    }

    private  void beginMain(){
        Intent intent = new Intent(LoginActivity.this,Introduction.class);
        startActivity(intent);
        finish();
    }

    private  void beginCompanyMain(){
        Intent intent = new Intent(LoginActivity.this,CompanyMainActivity.class);
        startActivity(intent);
        finish();
    }


    private void startFacebookLogin(){
        final ProgressDialog p2 = alerts.progress();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                p2.show();
            }
        });
        fb.begin(LoginActivity.this, new FacebookCallback() {
            @Override
            public void onSuccess(final SocialUser socialUser) {
                if(NetworkStatus.status(LoginActivity.this)){
                    loginToServer(socialUser, p2);
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p2.dismiss();
                        }
                    });
                    //Toast.makeText(LoginActivity.this,getResources().getString(R.string.nointernate),Toast.LENGTH_LONG).show();
                    Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();
                }



            }

            @Override
            public void onError(Throwable error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p2.dismiss();
                    }
                });

                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),error.getMessage(),Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();

            }

            @Override
            public void canceled(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p2.dismiss();
                    }
                });

                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),msg,Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }
        });
    }

    private void loginToServer(final SocialUser socialUser, final ProgressDialog p2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response =  fb.loginToServer(socialUser.accessToken,socialUser.email);
                    Log.i("code", response.code() + "");
                    if(response.code() == Constants.redirectCode){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p2.dismiss();
                                //Toast.makeText(LoginActivity.this,getResources().getString(R.string.registerFirster),Toast.LENGTH_LONG).show();
                                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.registerFirster),Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();

                            }
                        });
                    }

                    else if(response.code() == Constants.createdCode){
                        String body = response.body().string();
                        Log.i("body", body);
                        JsonObject jsonObj = new JsonParser().parse(body).getAsJsonObject();
                        String token = jsonObj.get("token").getAsString();
                        success(token);

                    }
                    else{
                        String body = response.body().string();
                        Log.i("facebook",body);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p2.dismiss();

                               // Toast.makeText(LoginActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();


                            }
                        });

                    }
                } catch (IOException e) {
                    Log.e("Json 3", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p2.dismiss();
                            //Toast.makeText(LoginActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                            Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            view.setLayoutParams(params);
                            snack.show();

                        }
                    });
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p2.dismiss();
                           // Toast.makeText(LoginActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                            Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            view.setLayoutParams(params);
                            snack.show();

                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startTwitter(){
        final ProgressDialog p2 = alerts.progress();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                p2.show();
            }
        });

        fb.twiter(LoginActivity.this, new FacebookCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                loginToServer(socialUser, p2);
            }

            @Override
            public void onError(Throwable error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p2.dismiss();
                    }
                });
                Log.e("twwiter error", error.getMessage());
                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }

            @Override
            public void canceled(String msg) {
                Snackbar snack = Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.canceled),Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(LoginActivity.this.getApplicationContext()).onActivityResult(LoginActivity.this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alerts = Alerts.getInstance(LoginActivity.this);
        fb = Facebook.getInstance();
    }
}
