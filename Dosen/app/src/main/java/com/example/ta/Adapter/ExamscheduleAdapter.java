package com.example.ta.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta.Examschedule;
import com.example.ta.R;

import java.util.ArrayList;

public class ExamscheduleAdapter extends RecyclerView.Adapter<ExamscheduleAdapter.MyViewHolder> {
    private final ArrayList<Examschedule> mData = new ArrayList<>();

    public void setData(ArrayList<Examschedule> items){
        mData.clear();
        mData.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(final Examschedule item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public void clearData(){
        mData.clear();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_exam, viewGroup, false);
        return new MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_class.setText(mData.get(position).getClasses());
        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_time.setText(mData.get(position).getTime());
        holder.tv_room.setText(mData.get(position).getRoom());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_class;
        private TextView tv_date;
        private TextView tv_time;
        private TextView tv_room;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_class = (TextView)itemView.findViewById(R.id.tv_class);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_room = (TextView) itemView.findViewById(R.id.tv_room);
        }
    }


}
