package com.rhicstech.crutra.crutra.UserUi.Views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.JobDetails;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rhicstechii on 08/03/2018.
 */


public class MapDetails extends Dialog {




    public MapDetails(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        super.setOnShowListener(listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        Log.i("MapDetails", "Dismised");
    }


}
