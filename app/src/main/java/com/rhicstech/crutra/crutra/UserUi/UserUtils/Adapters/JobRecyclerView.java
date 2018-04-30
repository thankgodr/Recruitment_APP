package com.rhicstech.crutra.crutra.UserUi.UserUtils.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.UserUi.JobDetails;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.Models.JobModels;
import com.rhicstech.crutra.crutra.UserUi.UserUtils.PlaceInfoListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by rhicstechii on 16/02/2018.
 */

public class JobRecyclerView extends RecyclerView.Adapter<JobRecyclerView.MyViewHolder> {

    List<JobModels> mData;
    private LayoutInflater inflater;
    Context cont;

    public JobRecyclerView(Context context, List<JobModels> data){
        this.mData = data;
        this.cont = context;
        inflater = LayoutInflater.from(context);

    }

    
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.job_list_card, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JobModels current = mData.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView des;
        TextView location;
        ImageView companyImage;
        ImageView more;

        RelativeLayout jobList;




        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.jobTitle);
            des = (TextView) itemView.findViewById(R.id.jobDes);
            location = (TextView) itemView.findViewById(R.id.location);
            companyImage = (ImageView) itemView.findViewById(R.id.compannyImage);
            more = (ImageView) itemView.findViewById(R.id.more);
            jobList = (RelativeLayout) itemView.findViewById(R.id.jobList);
        }

        public void setData(final JobModels current, final int position) {
            if(current.getJobImageUrl() != null){
                Picasso.with(cont).load(current.getJobImageUrl()).placeholder(R.drawable.wall).error(R.drawable.wall).into(companyImage);
            }
            title.setText(capitalizeFirstLetter(current.getTitle()));
            des.setText(capitalizeFirstLetter(current.getDes()));
            location.setText(capitalizeFirstLetter(current.getCompanyName()) + " | "+ capitalizeFirstLetter(current.getLocatiion()));
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("clicked", current.getId() + " was cliked");
                }
            });
            jobList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("clicked", current.getId() + "open Job Details");
                   //openJobDetails(current);
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openJobDetails(current,companyImage);
                }
            });
            companyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openJobDetails(current,companyImage);
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(more,position);
                }
            });
        }

        private void showPopupMenu(View view,int position) {
            // inflate menu
            PopupMenu popup = new PopupMenu(view.getContext(),view );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
            popup.show();
        }

        public String capitalizeFirstLetter(String original) {
            if (original == null || original.length() == 0) {
                return original;
            }
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }



        private void openJobDetails(JobModels current, ImageView imageView){
            JobDetails jobDetails = new JobDetails();
            Bundle bundle = new Bundle();
             Gson gson = new Gson();
             String job = gson.toJson(current,JobModels.class);
             bundle.putString("job",job);
             if(imageView != null){
                 Drawable drawable = imageView.getDrawable();
                 Bitmap bitmap = drawableToBitmap(drawable);
                 bundle.putString("image",encodeTobase64(bitmap));
             }
             jobDetails.setArguments(bundle);
            ((MainActivity)cont).changeFragment(jobDetails,"jobDetails");
        }

        public Bitmap drawableToBitmap (Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        public  String encodeTobase64(Bitmap image) {
            Bitmap immagex = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            return imageEncoded;
        }


        private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            private int currentPos;
            public MyMenuItemClickListener(int posito) {
                this.currentPos = posito;
            }
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        }
    }
}
