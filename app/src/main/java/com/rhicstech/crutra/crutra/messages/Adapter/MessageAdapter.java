package com.rhicstech.crutra.crutra.messages.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rhicstech.crutra.crutra.R;
import com.rhicstech.crutra.crutra.messages.MessageModel.Messages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by rhicstechii on 26/03/2018.
 */

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    ArrayList<Messages> mData;
    Context c;
    int isReciverMessage;
    LayoutInflater inflaterp;
    String receiverName;
    int currentView;

    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList , String nameReciver){
        this.mData = messagesArrayList;
        this.inflaterp = LayoutInflater.from(context);
        this.c = context;
        doSort(this.mData);
        this.receiverName = nameReciver;
    }

    public void addItem(Messages mess){
        mData.add(mess);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Messages current = mData.get(position);
        return current.getisReceiver();
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1){
            View view = inflaterp.inflate(R.layout.message_receiver,parent,false);
            MessageAdapter.MyViewHolder holder = new  MessageAdapter.MyViewHolder(view);
            return holder;
        }
        else{
            View view = inflaterp.inflate(R.layout.message_sender,parent,false);
            MessageAdapter.MyViewHolder holder = new  MessageAdapter.MyViewHolder(view);
            return holder;
        }

    }





    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Messages current = mData.get(position);
        holder.setData(current);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView messageView;
        TextView time;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageHolder);
            this.name = itemView.findViewById(R.id.name);
            this.messageView = itemView.findViewById(R.id.message);
            this.time = itemView.findViewById(R.id.time);
        }

        public void setData(Messages current) {
            this.messageView.setText(current.getMessage());
            this.time.setText(current.getAgo());
            if(current.getisReceiver() == 1){
                name.setText(receiverName);
            }
            else{
                name.setText(c.getResources().getString(R.string.you));
            }

        }
    }

    private  void swap(ArrayList<Messages> sort, int i, int j) {
        Messages tmp = sort.get(i);
        sort.set(i, sort.get(j));
        sort.set(j, tmp);
    }

    private  void doSort(ArrayList<Messages>  sort) {
        int min;
        for (int i = 0; i < sort.size(); ++i) {
            //find minimum in the rest of array
            min = i;
            for (int j = i + 1; j < sort.size(); ++j) {
              //  if (sort.get(j) < sort.get(min)) {
               //     min = j;
              //  }
                try {
                    if(compareDate(sort.get(j).getDate(),sort.get(min).getDate())){
                        min = j;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //do swap
            swap(sort, i, min);
        }
    }

    private  boolean compareDate(String input1, String input2) throws ParseException {
         Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input1);
         Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input2);
         return date1.before(date2);
    }
}
