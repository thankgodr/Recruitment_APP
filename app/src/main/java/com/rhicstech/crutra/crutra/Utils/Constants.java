package com.rhicstech.crutra.crutra.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.ServerConnections;
import com.rhicstech.crutra.crutra.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rhicstechii on 15/01/2018.
 */

public class Constants {
    public static final String prefName = "rhicstecCrutraApp";
    public static final String prefUser = "rhicstecCrutraUser";
    public static final String prefUserType = "userType";
    public static final int userInt = 1;
    public static final int companyInt = 2;
    public static final int redirectCode = 301;
    public static final int createdCode = 201;
    public static final int existCode = 303;
    public static final int okay = 200;
    public static String prefToken ="token";
    public static final int NoPaymentYet = 402;
    public final static int record = 4905;
    public final static int pickVideo = 4901;
    public final static int captureImage = 4906;
    public final static int pickImage = 4900;
    public static String baseUrl ="http://13.250.43.203/api/";

    public static void darkenStatusBar(Activity activity) {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            activity.getWindow().setStatusBarColor(
                    darkenColor(
                            ContextCompat.getColor(activity, R.color.textDefault)));
        }

    }


    // Code to darken the color supplied (mostly color of toolbar)
    private static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static  String[] industries() throws IOException, JSONException {
        ServerConnections serverConnections = new ServerConnections();
        String response = serverConnections.getIndustries();
        JSONArray jsonArray = new JSONArray(response);
        JSONArray jsonArray1 = jsonArray.getJSONArray(0);
        String[] indus = new String[jsonArray1.length()];
        for(int i =0; i < jsonArray1.length(); i++ ){
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            indus[jsonObject.getInt("id") - 1] = jsonObject.getString("industry_name");
        }

        Log.i("indus", indus[0]);
        return indus;
    }

    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "play.ttf"));
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "play.ttf"));
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "play.ttf"));
            }
        } catch (Exception e) {
        }
    }
}
