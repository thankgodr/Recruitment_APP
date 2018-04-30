package com.rhicstech.crutra.crutra.auth.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.searchUi.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourthIntro extends Fragment {
    @BindView(R.id.start)
    Button start;


    public FourthIntro() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourth_intro, container, false);
        ButterKnife.bind(this, view);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 SearchFragment searchFragment = new SearchFragment();
                 Bundle bundle = new Bundle();
                 bundle.putBoolean("status", true);
                 searchFragment.setArguments(bundle);
                ((Introduction)getContext()).changeFragment(searchFragment);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
