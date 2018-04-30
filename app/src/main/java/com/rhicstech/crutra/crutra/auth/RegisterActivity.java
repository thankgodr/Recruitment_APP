package com.rhicstech.crutra.crutra.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Circle;
import com.jaychang.sa.SocialUser;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.Network.Connection;
import com.rhicstech.crutra.crutra.Network.NetworkStatus;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.ButtonCallback;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.Validations.EmailValidate;
import com.rhicstech.crutra.crutra.auth.Social.CallBacks.FacebookCallback;
import com.rhicstech.crutra.crutra.auth.Social.Facebook;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    //Views
    @BindView(R.id.facebook)
    ImageView facebook;
    @BindView(R.id.twitter) ImageView twitter;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.slogan) TextView slogan;
    @BindView(R.id.user)
    EditText userName;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.confirm_password) EditText confirm_password;
    @BindView(R.id.login)
    Button register;
    @BindView(R.id.forgotPass) TextView forgotPass;
    @BindView(R.id.loginHere) TextView login;
    @BindView(R.id.linkedin) ImageView linkedin;
    @BindView(R.id.userType)
    RadioGroup userType;
    @BindView(R.id.social) LinearLayout social;
    @BindView(R.id.bottomText)
    RelativeLayout bottomtex;
    ProgressDialog pd;


    //Helpers
    int userTypeInt = 2;
    private Alerts alerts;
    Facebook fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //Initialise  Helpers
        alerts = Alerts.getInstance(this);
        fb = Facebook.getInstance();

        Typeface hevetica = Typeface.createFromAsset(getAssets(), "helveticaNeue.ttf");


        //Set fonts for all views helveticaNeue
        slogan.setTypeface(hevetica);
        userName.setTypeface(hevetica);
        password.setTypeface(hevetica);
        login.setTypeface(hevetica);
        forgotPass.setTypeface(hevetica);
        register.setTypeface(hevetica);
        confirm_password.setTypeface(hevetica);


        //Remove the status bar and notification bar
        LinearLayout linearLayout = findViewById(R.id.front);
        linearLayout.bringToFront();
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Initiate all buttton click listerner on another thread
        btnClick();

        Constants.overrideFonts(RegisterActivity.this, getWindow().getDecorView().getRootView());
        Typeface face = Typeface.createFromAsset(getAssets(),
                "anabel.ttf");
        logo.setTypeface(face);



    }


    //Button listerner method
    private  void btnClick(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Login Button
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                //Register Button
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validate()){
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            pd = new ProgressDialog(RegisterActivity.this);
                            pd.setCancelable(false);
                            pd.setMessage(getResources().getString(R.string.pleasewait));
                            Circle circle = new Circle();
                            circle.setColor(getResources().getColor(R.color.textDefault));
                            pd.setIndeterminateDrawable(circle);
                            pd.setIndeterminate(true);
                            final String email = userName.getText().toString();
                            final String pass  = password.getText().toString();
                            final String pass2 = confirm_password.getText().toString();
                            if(NetworkStatus.status(RegisterActivity.this)){
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
                                            String data = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"email\"\r\n\r\n"+email+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"password\"\r\n\r\n"+pass+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"password_confirmation\"\r\n\r\n"+pass2+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"provider_id\"\r\n\r\n1\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"account_type\"\r\n\r\n"+userTypeInt+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";
                                            String datatype = "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
                                            final Response response = Connection.postconnectCustom("auth/signup",data,datatype);
                                            if(response.code() == 201){

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                     pd.dismiss();
                                                     success();
                                                    }
                                                });
                                            }
                                            else if(response.code() == 208){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pd.dismiss();
                                                       // Toast.makeText(RegisterActivity.this,getResources().getString(R.string.alreadyRegistered),Toast.LENGTH_LONG).show();
                                                        Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.alreadyRegistered),Snackbar.LENGTH_LONG).show();

                                                    }
                                                });
                                            }
                                            else{
                                                Log.i("Register", response.body().string());
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pd.dismiss();
                                                       // Toast.makeText(RegisterActivity.this, getResources().getString(R.string.errorHappen), Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG).show();

                                                    }
                                                });
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                            else{
                                pd.dismiss();
                                //Toast.makeText(RegisterActivity.this,getResources().getString(R.string.nointernate),Toast.LENGTH_LONG).show();
                                Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG).show();

                            }
                        }
                    }
                });

                //SignUp with Facebook
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        facebookSignUp();
                    }
                });

                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        twiterSignUp();
                    }
                });


            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.applicant:
                                userTypeInt = 2;
                                linkedin.setVisibility(View.VISIBLE);
                                social.setVisibility(View.VISIBLE);
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bottomtex.getLayoutParams();
                                params.topMargin = 10;
                                break;
                            case R.id.company:
                                userTypeInt = 1;
                                linkedin.setVisibility(View.GONE);
                                social.setVisibility(View.GONE);
                                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) bottomtex.getLayoutParams();
                                params2.topMargin = 170;
                                break;

                        }
                    }
                });
            }
        }).start();

    }


    //Registration has complete
    private void success() {
        alerts.withButton(getResources().getString(R.string.complete), getResources().getString(R.string.Registeredcomplete), getResources().getString(R.string.loginnow), new ButtonCallback() {
            @Override
            public void startAction() {
                startLogin();
            }
        });
    }

    private boolean validate(){
        EmailValidate emailValidate = new EmailValidate();
        if(!emailValidate.validate(userName.getText().toString())){
           userName.setError(getResources().getString(R.string.invalidEmail));
           return false;
        }
        else if(!emailValidate.passwordMatch(password.getText().toString(),confirm_password.getText().toString())){
            confirm_password.setError(getResources().getString(R.string.passwordDontmatch));
            return  false;
        }
        else{
            return true;
        }
    }


    //Return to signin after login
    private void startLogin(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //Facebook signin
    private void facebookSignUp() {
        fb.begin(RegisterActivity.this, new FacebookCallback() {
            @Override
            public void onSuccess(final SocialUser socialUser) {
                final ProgressDialog p2 = alerts.progress();
                String fullname = socialUser.fullName;
                String[] names = fullname.split(" ");
                final String fname = names[0];
                final String lname = names[names.length - 1];
                p2.show();
                if(NetworkStatus.status(RegisterActivity.this)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Response response =  fb.signUpServer(socialUser.email,4,2,socialUser.accessToken,fname,lname);
                                if(response.code() == Constants.redirectCode){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            p2.dismiss();
                                            //Toast.makeText(RegisterActivity.this,getResources().getString(R.string.registerFirster),Toast.LENGTH_LONG).show();
                                            Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.registerFirster),Snackbar.LENGTH_LONG).show();

                                        }
                                    });
                                }
                                else if(response.code() == Constants.existCode){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            p2.dismiss();
                                            //Toast.makeText(RegisterActivity.this,getResources().getString(R.string.alreadyRegistered),Toast.LENGTH_LONG).show();
                                            Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.alreadyRegistered),Snackbar.LENGTH_LONG).show();


                                        }
                                    });


                                }

                                else if(response.code() == Constants.createdCode){
                                    String body = response.body().string();
                                    final JSONObject jsonObject = new JSONObject(body);
                                    final String token = jsonObject.getString("token");
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           success();
                                       }
                                   });


                                }
                                else{

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            p2.dismiss();
                                            //Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                                            Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG).show();


                                        }
                                    });

                                }
                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        p2.dismiss();
                                       // Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                                        Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG).show();


                                    }
                                });
                            } catch (JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        p2.dismiss();
                                        //Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                                        Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                else{
                    p2.dismiss();
                    //Toast.makeText(LoginActivity.this,getResources().getString(R.string.nointernate),Toast.LENGTH_LONG).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG).show();
                }



            }

            @Override
            public void onError(Throwable error) {
                Snackbar.make(getWindow().getDecorView().getRootView(),error.getMessage(),Snackbar.LENGTH_LONG).show();


            }

            @Override
            public void canceled(String msg) {
                Snackbar.make(getWindow().getDecorView().getRootView(),msg,Snackbar.LENGTH_LONG).show();

            }
        });

    }

    private void twiterSignUp(){
        fb.twiter(RegisterActivity.this, new FacebookCallback() {
            @Override
            public void onSuccess(final SocialUser socialUser) {
                final ProgressDialog p2 = alerts.progress();
                String fullname = socialUser.fullName;
                String[] names = fullname.split(" ");
                final String fname = names[0];
                final String lname = names[names.length - 1];
                p2.show();
                if(NetworkStatus.status(RegisterActivity.this)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Response response =  fb.signUpServer(socialUser.email,4,2,socialUser.accessToken,fname,lname);
                                if(response.code() == Constants.redirectCode){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            p2.dismiss();
                                            Toast.makeText(RegisterActivity.this,getResources().getString(R.string.registerFirster),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                else if(response.code() == Constants.createdCode){
                                    String body = response.body().string();
                                    final JSONObject jsonObject = new JSONObject(body);
                                    final String token = jsonObject.getString("token");
                                    success();


                                }
                                else{

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            p2.dismiss();
                                            Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        p2.dismiss();
                                        Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();

                                    }
                                });
                            } catch (JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        p2.dismiss();
                                        Toast.makeText(RegisterActivity.this,getResources().getString(R.string.errorHappen),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                else{
                    p2.dismiss();
                    //Toast.makeText(LoginActivity.this,getResources().getString(R.string.nointernate),Toast.LENGTH_LONG).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(),getResources().getString(R.string.nointernate),Snackbar.LENGTH_LONG).show();
                }



            }

            @Override
            public void onError(Throwable error) {
                Snackbar.make(getWindow().getDecorView().getRootView(),error.getMessage(),Snackbar.LENGTH_LONG).show();


            }

            @Override
            public void canceled(String msg) {
                Snackbar.make(getWindow().getDecorView().getRootView(),msg,Snackbar.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        alerts = Alerts.getInstance(RegisterActivity.this);
        fb = Facebook.getInstance();
    }
}



