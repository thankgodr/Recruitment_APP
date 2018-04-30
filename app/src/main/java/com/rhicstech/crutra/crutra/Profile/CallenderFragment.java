package com.rhicstech.crutra.crutra.Profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Constants;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallenderFragment extends Fragment {


    public CallenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_callender, container, false);
        ButterKnife.bind(this,view);
        Constants.overrideFonts(getActivity(), view);

        return view;
    }

}
