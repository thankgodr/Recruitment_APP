package com.rhicstech.crutra.crutra.messages.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.CompanyMainActivity;
import com.rhicstech.crutra.crutra.MainActivity;
import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.Utils.UserAuth;
import com.rhicstech.crutra.crutra.messages.MessageModel.CompanyContact;
import com.rhicstech.crutra.crutra.messages.Messaging;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rhicstechii on 27/03/2018.
 */

public class ListUserForMessage extends RecyclerView.Adapter<ListUserForMessage.MyViewHolder> {
    Context c;
    ArrayList<CompanyContact> mData;
    LayoutInflater layoutInflater;

    public ListUserForMessage(Context c, ArrayList<CompanyContact> mData) {
        this.c = c;
        this.mData = mData;
        this.layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public ListUserForMessage.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = layoutInflater.inflate(R.layout.contact_list,parent,false);
         MyViewHolder holder = new MyViewHolder(view);
         return holder;
    }

    @Override
    public void onBindViewHolder(ListUserForMessage.MyViewHolder holder, int position) {
        CompanyContact companyContact = mData.get(position);
        holder.setData(companyContact);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView  imageView;
        TextView companyName;
        TextView time;
        TextView details;
        RelativeLayout contact;
        public MyViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            time = itemView.findViewById(R.id.time);
            details = itemView.findViewById(R.id.details);
            contact = itemView.findViewById(R.id.contact);
        }

        public void setData(final CompanyContact companyContact) {
            companyName.setText(companyContact.getCompanyName());
            time.setText(companyContact.getTimeAgo());
            details.setText(companyContact.getLastMessage());
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessaging(companyContact.getContactID(), companyContact.getCompanyName());
                }
            });
        }

        private void openMessaging(int user, String name){
            Messaging messaging = new Messaging();
            Bundle bundle = new Bundle();
            bundle.putInt("user", user);
            bundle.putString("name", name);
            messaging.setArguments(bundle);
            if(new UserAuth(c).getUserType() == 1){
                ((MainActivity)c).changeFragment(messaging,"messaging");
            }
            else if(new UserAuth(c).getUserType() == 2){
                ((CompanyMainActivity)c).changeFragment(messaging,"messaging");
            }
        }
    }
}
