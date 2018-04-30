package com.rhicstech.crutra.crutra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyApplications;
import com.rhicstech.crutra.crutra.CompanyUi.EmployerHomeFragment;
import com.rhicstech.crutra.crutra.Profile.Payment;
import com.rhicstech.crutra.crutra.Profile.SettingsHome;
import com.rhicstech.crutra.crutra.Profile.UpdateAndContinue;
import com.rhicstech.crutra.crutra.Profile.UpdateProfile;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.user.Company;

public class CompanyMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Initialize views




    UserAuth userAuth;
    Company company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();    // Or getSupportActionBar() if using appCompat
        int red = getResources().getColor(R.color.textDefault);
        setActionbarTextColor(actionBar, red);
        setTitle(getResources().getString(R.string.app_name));

        setOverflowButtonColor(toolbar, R.color.textDefault);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //do your work
            } else {
                requestPermission();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //do your work
            } else {
                requestPermission();
            }
        }
        Constants.darkenStatusBar(this);


        //Innitialise the helpers
        userAuth = new UserAuth(CompanyMainActivity.this);
        company = userAuth.getCompany();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putInt("userType", 1);
        UpdateAndContinue updateAndContinue = new UpdateAndContinue();
        updateAndContinue.setArguments(bundle);
        changeFragment(new EmployerHomeFragment());

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setHomeAsUpIndicator(R.drawable.menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Constants.overrideFonts(CompanyMainActivity.this, getWindow().getDecorView().getRootView());

    }

    public static void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }

    private void setActionbarTextColor(ActionBar actBar, int color) {

        String title = actBar.getTitle().toString();
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.company_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Bundle bundle = new Bundle();
            bundle.putInt("userType", 1);
            UpdateAndContinue updateAndContinue = new UpdateAndContinue();
            updateAndContinue.setArguments(bundle);
            changeFragment(updateAndContinue,"updateandcontinue");
        } else if (id == R.id.nav_gallery) {
            changeFragment(new CompanyApplications(),"applications");

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Bundle arg = new Bundle();
            arg.putInt("userId",1);
            SettingsHome settingsHome = new SettingsHome();
            settingsHome.setArguments(arg);
            changeFragment(settingsHome, "settings");

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            new UserAuth(CompanyMainActivity.this).Logout(CompanyMainActivity.this);
            finish();
        }
        else if(id == R.id.payment){
            changeFragment(new Payment(), "payment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, newFragment);
        transaction.commit();
    }
    public void changeFragment(Fragment newFragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, newFragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }
    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }
}
