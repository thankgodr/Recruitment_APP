package com.rhicstech.crutra.crutra.Utils.Alerts;

import android.app.ProgressDialog;
import android.content.Context;

import com.github.ybq.android.spinkit.style.Circle;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.ButtonCallback;
import com.rhicstech.crutra.crutra.Utils.Alerts.Interface.onLoadingComplete;
import com.rhicstech.crutra.crutra.auth.LoginActivity;

/**
 * Created by rhicstechii on 09/02/2018.
 */

public class Alerts {
    private static final Alerts ourInstance = new Alerts();
    static Context c;
    public static Alerts getInstance(Context context) {
        c = context;
        return ourInstance;
    }

    private Alerts() {
    }

    public void withButton(String title,String content, String ButtonText,final ButtonCallback onLoadingComplete){
        new SweetAlertDialog(c, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(ButtonText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        onLoadingComplete.startAction();
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    public ProgressDialog progress(){
        ProgressDialog pd = new ProgressDialog(c);
        pd.setCancelable(false);
        pd.setMessage(c.getResources().getString(R.string.pleasewait));
        Circle circle = new Circle();
        circle.setColor(c.getResources().getColor(R.color.textDefault));
        pd.setIndeterminateDrawable(circle);
        pd.setIndeterminate(true);
        return pd;
    }



}
