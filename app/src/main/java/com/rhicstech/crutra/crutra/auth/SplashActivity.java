package com.rhicstech.crutra.crutra.auth;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {
    UserAuth auth ;

    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = new UserAuth(SplashActivity.this);
        logo = findViewById(R.id.logo);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }


        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {

                next(auth.isLogin());
            }
        }, secondsDelayed * 1000);

        Constants.overrideFonts(SplashActivity.this, getWindow().getDecorView().getRootView());
        Typeface face = Typeface.createFromAsset(getAssets(),
                "anabel.ttf");
        logo.setTypeface(face);

    }

    private void next(boolean loginin){
        if(loginin){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        auth.refresh(SplashActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            switch(auth.getUserType()){
                case Constants.companyInt:
                    startActivity(new Intent(SplashActivity.this, CompanyMainActivity.class));
                    finish();
                    break;
                case  Constants.userInt:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                default:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    break;
            }
        }
        else{
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
