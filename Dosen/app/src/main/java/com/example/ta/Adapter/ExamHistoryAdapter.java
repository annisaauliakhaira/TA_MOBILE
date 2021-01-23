package com.example.ta.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta.R;

import org.json.JSONArray;
import org.json.JSONException;

public class ExamHistoryAdapter extends RecyclerView.Adapter<ExamHistoryAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();

    public void setData(JSONArray itemExam){
        mData=itemExam;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_presence, viewGroup, false);
        return new ExamHistoryAdapter.MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.tv_nimPresence.setText(mData.getJSONObject(position).getString("nim"));
            holder.tv_namePresence.setText(mData.getJSONObject(position).getString("name"));
            String presence_status = mData.getJSONObject(position).getString("status");
            if (presence_status.equals("0")){
                holder.iv_hadirPresence.setImageResource(R.drawable.ic_cross);
            }else if (presence_status.equals("1")){
                holder.iv_hadirPresence.setImageResource(R.drawable.ic_check);
            }else if (presence_status.equals("2")){
                holder.iv_hadirPresence.setImageResource(R.drawable.ic_permit);
            }
            if(mData.getJSONObject(position).getString("presence_time_start").equals("null")){
                holder.tv_attendanceHourPr.setText("--");
            }else {
                holder.tv_attendanceHourPr.setText(mData.getJSONObject(position).getString("presence_time_start"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_nimPresence;
        private TextView tv_namePresence;
        private ImageView iv_hadirPresence;
        private TextView tv_attendanceHourPr;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_nimPresence = (TextView) itemView.findViewById(R.id.tv_nimPresence);
            tv_namePresence = (TextView) itemView.findViewById(R.id.tv_namePresence);
            iv_hadirPresence = (ImageView) itemView.findViewById(R.id.iv_hadirPresence);
            tv_attendanceHourPr = (TextView) itemView.findViewById(R.id.tv_attendanceHourPr);
        }
    }
}
