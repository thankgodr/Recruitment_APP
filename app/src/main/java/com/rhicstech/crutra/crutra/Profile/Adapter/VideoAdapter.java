package com.rhicstech.crutra.crutra.Profile.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyUi.CompanyUtils.Models.ApplicationModel;
import com.rhicstech.crutra.crutra.Profile.Model.VideoModel;
import com.rhicstech.crutra.crutra.R;

import java.util.ArrayList;
import java.util.HashMap;

import hb.xvideoplayer.MxVideoPlayer;

/**
 * Created by rhicstechii on 20/03/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    Context cont;
    Activity activity;
    ArrayList<VideoModel> mData;
    LayoutInflater inflater;
    boolean selectInterface;
    public VideoAdapter(Context c, Activity ac,boolean selectInterFace, ArrayList<VideoModel> models){
        inflater = LayoutInflater.from(c);
        this.cont = c;
        this.activity = ac;
        this.mData = models;
        this.selectInterface = selectInterFace;
    }

    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_card,parent,false);
        VideoAdapter.MyViewHolder holder = new VideoAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoAdapter.MyViewHolder holder, int position) {
        VideoModel current = mData.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout image_holder;
        FrameLayout videoHolder;
        ImageView image;
        ImageView playBtn;
        TextView videoName1;
        MxVideoPlayer video;
        TextView videoName;

        public MyViewHolder(View itemView) {
            super(itemView);
            image_holder = itemView.findViewById(R.id.image_holder);
            videoHolder = itemView.findViewById(R.id.videoHolder);
            image = itemView.findViewById(R.id.image);
            playBtn = itemView.findViewById(R.id.playBtn);
            videoName = itemView.findViewById(R.id.videoName);
            videoName1 = itemView.findViewById(R.id.videoName1);
            video = itemView.findViewById(R.id.video);
         }

        public void setData(final VideoModel current, int position) {
            //Set Animation on image
            videoHolder.setVisibility(View.GONE);
            image_holder.setVisibility(View.VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(800);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            image.setAnimation(rotateAnimation);
            playBtn.setVisibility(View.VISIBLE);
            video.startPlay(current.getUrl().replaceAll("https", "http"),MxVideoPlayer.SCREEN_LAYOUT_NORMAL,current.getName());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = setImage(current);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageBitmap(bitmap);
                            image.clearAnimation();
                            image.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    });
                }
            }).start();
            videoName1.setText(current.getName());
            videoName.setText(current.getName());
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_holder.setVisibility(View.GONE);
                    videoHolder.setVisibility(View.VISIBLE);
                    Log.i("testurl", current.getUrl());
                }
            });
            if(selectInterface){
                videoName.setText(cont.getResources().getString(R.string.select));
                videoName1.setText(cont.getResources().getString(R.string.select));
                videoName.setBackgroundColor(cont.getResources().getColor(R.color.textDefault));
                videoName1.setBackgroundColor(cont.getResources().getColor(R.color.textDefault));
                videoName.setPadding(10,10,10,10);
                videoName1.setPadding(10,10,10,10);

                videoName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data = new Intent();
                        data.putExtra("vidId", current.getId());
                        activity.setResult(Activity.RESULT_OK,data);
                        activity.finish();
                    }
                });
                videoName1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data = new Intent();
                        data.putExtra("vidId", current.getId());
                        activity.setResult(Activity.RESULT_OK,data);
                        activity.finish();
                    }
                });


            }

        }

        public Bitmap retriveVideoFrameFromVideo(String videoPath)
                throws Throwable {
            Bitmap bitmap = null;
            Log.i("url", videoPath);
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);

                bitmap = mediaMetadataRetriever.getFrameAtTime(2, MediaMetadataRetriever.OPTION_CLOSEST);
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

        private Bitmap setImage(VideoModel current) {
            Bitmap bitmap= null;
            try {
                bitmap = retriveVideoFrameFromVideo(current.getUrl());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return bitmap;
        }
    }
}
