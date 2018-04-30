package com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.afollestad.materialcamera.util.ImageUtil;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.CompanyUi.ApplicationFragment;
import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.ApplicationModel;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Adapters.JobRecyclerView;
import com.rhicstech.crutra.crutra.Utils.Alerts.Alerts;
import com.rhicstech.crutra.crutra.videoui.VideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;

/**
 * Created by rhicstechii on 26/02/2018.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {
    LayoutInflater inflater;
    ArrayList<ApplicationModel> mData;
    Context c;
    Activity ac;
    int jobid;
    public ApplicationAdapter(Context context, Activity activity, ArrayList<ApplicationModel> models , int id){
        this.inflater = LayoutInflater.from(context);
        this.mData = models;
        this.c = context;
        this.ac = activity;
        this.jobid = id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.application_list,parent,false);
        ApplicationAdapter.MyViewHolder holder = new ApplicationAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ApplicationModel current = mData.get(position);
        holder.setData(current, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView videoView , playBtn, more;
        TextView fullname, headline;
        RelativeLayout jobLayout;
        MxVideoPlayerWidget videoPlayer;

        public MyViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            fullname = itemView.findViewById(R.id.jobTitle);
            headline = itemView.findViewById(R.id.jobDes);
            playBtn = itemView.findViewById(R.id.playBtn);
            jobLayout = itemView.findViewById(R.id.topImage);
            videoPlayer = itemView.findViewById(R.id.mpw_video_player);
            more = itemView.findViewById(R.id.more);
        }

        public void setData(final ApplicationModel current, final int position) {
            fullname.setText(current.getFullname());
            if(current.getHeadline() != null && current.getHeadline() == "null"){
                headline.setText(current.getHeadline());
            }
            else{
                headline.setVisibility(View.INVISIBLE);
            }
            RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(500);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            videoView.startAnimation(rotateAnimation);
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoPlayer.startPlay(current.getVideourl().replaceAll("https", "http"), MxVideoPlayer.SCREEN_LAYOUT_NORMAL,c.getResources().getString(R.string.applicantVidoe));
                    videoPlayer.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    playBtn.setVisibility(View.GONE);
                }
            });
            fullname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openVideo(current.getVideourl(), 1,current.getJobId(),current.getFullname());
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(more, position);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = setImage(current);
                    ac.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoView.setImageBitmap(bitmap);
                            playBtn.setVisibility(View.VISIBLE);
                            videoView.clearAnimation();
                            videoView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    });
                }
            }).start();
        }

        private Bitmap setImage(ApplicationModel current) {
            Bitmap bitmap= null;
            try {
                bitmap = retriveVideoFrameFromVideo(current.getVideourl());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return bitmap;
        }

        private void showPopupMenu(View view,int position) {
            // inflate menu
            PopupMenu popup = new PopupMenu(view.getContext(),view );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.employer_popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
            popup.show();
        }

        public  Bitmap retriveVideoFrameFromVideo(String videoPath)
                throws Throwable {
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);

                bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Throwable(
                        "Exception in retriveVideoFrameFromVideo(String videoPath)"
                                + e.getMessage());

            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }
            return bitmap;
        }

        private void openVideo(String videoUrl, int userid, int jobid, String name) {
            Bundle bundle = new Bundle();
            bundle.putString("videourl", videoUrl);
            bundle.putInt("userid", userid);
            bundle.putInt("jobid", jobid);
            bundle.putString("name", name);
            VideoPlayer videoPlayer = new VideoPlayer();
            videoPlayer.setArguments(bundle);
            ((CompanyMainActivity)c).changeFragment(videoPlayer,"videoplayer");
        }

        private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            public MyMenuItemClickListener(int position) {
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        }
    }
}
