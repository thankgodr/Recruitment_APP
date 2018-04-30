package com.rhicstech.crutra.crutra.auth.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rhicstech.crutra.crutra.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdIntro extends Fragment {
    @BindView(R.id.skip)
    Button skip;
    @BindView(R.id.next) Button next;

    public ThirdIntro() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_intro, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Introduction)getContext()).beginMain();
                    }
                });

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Introduction)getContext()).changeFragment(new FourthIntro(), "third");
                    }
                });
            }
        }).start();
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
