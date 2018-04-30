package com.rhicstech.crutra.crutra.auth.intro;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.auth.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Introduction extends AppCompatActivity {
   @BindView(R.id.container) LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introduction);

        ButterKnife.bind(this);
        changeFragment(new FirstIntro());
    }

    public void changeFragment(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }
    public void changeFragment(Fragment newFragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    public   void beginMain(){
        Intent intent = new Intent(Introduction.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
