package com.rhicstech.crutra.crutra.searchUi;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.rhicstech.crutra.crutra.Network.ConnectionReceiver;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.Utils.Constants;
import com.rhicstech.crutra.crutra.auth.intro.FifthFragment;
import com.rhicstech.crutra.crutra.auth.intro.Introduction;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements ConnectionReceiver.ConnectionReceiverListener {

    @BindView(R.id.tag_group) TagView tagGroup;
    @BindView(R.id.autocom) AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.firstHundred) TextView firstHundred;
    @BindView(R.id.lastHundread) TextView lastHundred;
    @BindView(R.id.range) RangeBar rangeBar;
    @BindView(R.id.permanet) Switch permanentSwitch;
    @BindView(R.id.fulltime) Switch fulltimeSwitch;
    @BindView(R.id.parttime) Switch parttimeSwitch;
    @BindView(R.id.contract) Switch contractSwitch;
    @BindView(R.id.search) EditText search;
    @BindView(R.id.location) EditText location;
    @BindView(R.id.searchBtn)  Button searchBtn;
    @BindView(R.id.top) RelativeLayout upView;
    @BindView(R.id.next) Button next;
    @BindView(R.id.skip) Button skip;
    @BindView(R.id.start) LinearLayout startLayout;

    boolean permanent = false, fulltime = false, partime = false, contract = false;
    String minPrice = "100", maxPrice = "5000";
    ArrayList<String> selectedCategories = new ArrayList<>();
    boolean istart = false;

    int selected = 0;
    Alerts alerts;
    ProgressDialog progressDialog;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            istart = bundle.getBoolean("status");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this,view);
        alerts = Alerts.getInstance(getContext());
        progressDialog = alerts.progress();
        progressDialog.show();
        if(istart){
            upView.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.GONE);
            startLayout.setVisibility(View.VISIBLE);

        }

        // Initailis view

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] tags = Constants.industries();
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,tags);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            autoCompleteTextView.setAdapter(adapter);
                            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if(selected < 5){
                                        Tag tag = new Tag(adapter.getItem(position));
                                        tag.tagTextColor = getResources().getColor(R.color.white);
                                        tag.layoutColor = getResources().getColor(R.color.textDefault);
                                        tagGroup.addTag(tag);
                                        selectedCategories.add(tag.text);
                                        adapter.remove(adapter.getItem(position));
                                        adapter.notifyDataSetChanged();
                                        autoCompleteTextView.setText("");
                                        selected++;

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.maxfiveSelected),Snackbar.LENGTH_LONG);
                                        View view1 = snack.getView();
                                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
                                        params.gravity = Gravity.TOP;
                                        view1.setLayoutParams(params);
                                        snack.show();
                                    }
                                }
                            });
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                            View view1 = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            view1.setLayoutParams(params);
                            snack.show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),getResources().getString(R.string.errorHappen),Snackbar.LENGTH_LONG);
                    View view1 = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view1.setLayoutParams(params);
                    snack.show();
                }
            }
        }).start();


        //Start switch Listerner
        new Thread(new Runnable() {
            @Override
            public void run() {
                switchTaps();
            }
        }).start();



        //LISTERNER ON RANGERBARS
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                firstHundred.setText("$" + leftPinValue);
                lastHundred.setText("$" + rightPinValue);
                minPrice = leftPinValue;
                maxPrice = rightPinValue;
            }
        });

        //listerner on Tag groups
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                deleteTag(position);
            }
        });

        //set delete listener
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                Log.i("tag Delete", tag.text);
                selectedCategories.remove(tag.text);
            }
        });

        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                btnClick();
            }
        }).start();

        Constants.overrideFonts(getActivity(), view);

        return view;
    }


    //Listeners on switchs
    private void switchTaps(){
        permanentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    permanent = true;
                }
                else {
                    permanent = false;
                }
            }
        });
        fulltimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fulltime = true;
                }
                else {
                    fulltime = false;
                }
            }
        });
        contractSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    contract = true;
                }
                else {
                    contract = false;
                }
            }
        });

        parttimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    partime = true;
                }
                else {
                    permanent = false;
                }
            }
        });
    }

    //All Buttton Listeners
    private void btnClick(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Introduction)getContext()).changeFragment(new FifthFragment(), "Fiftinto");
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Introduction)getContext()).beginMain();
            }
        });
    }

    //To delete the tags
   private void deleteTag(final int Tagposition){
       AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
       adb.setTitle(getResources().getString(R.string.confirm));
       adb.setMessage(getResources().getString(R.string.areUsuretoDelete));
       adb.setIcon(android.R.drawable.ic_dialog_alert);
       adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               tagGroup.remove(Tagposition);
               dialog.dismiss();
           } });
       adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           } });
       adb.show();
   }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
