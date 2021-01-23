package com.example.mahasiswa.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mahasiswa.R;

import org.json.JSONArray;
import org.json.JSONException;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();

    public void setData(JSONArray items){
        mData = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_history, viewGroup, false);
        return new HistoryAdapter.MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            String presence_status = mData.getJSONObject(position).getString("presence_status");
            if (presence_status.equals("0")){
                holder.iv_kehadiran.setImageResource(R.drawable.ic_cross);
            }else if (presence_status.equals("1")){
                holder.iv_kehadiran.setImageResource(R.drawable.ic_check);
            }else if (presence_status.equals("2")){
                holder.iv_kehadiran.setImageResource(R.drawable.ic_permit);
            }
            holder.tv_courseHistory.setText(mData.getJSONObject(position).getString("courses"));
            holder.tv_classHistory.setText(mData.getJSONObject(position).getString("class_name"));
            holder.tv_dateHistory.setText(mData.getJSONObject(position).getString("date"));
            holder.tv_roomHistory.setText(mData.getJSONObject(position).getString("room"));
            holder.tv_timeHistory.setText(mData.getJSONObject(position).getString("start_hour")+" - "+mData.getJSONObject(position).getString("ending_hour"));
            if (mData.getJSONObject(position).getString("enter_time").equals("null")){
                holder.tv_presenceTimeStart.setText("--");
            }else {
                holder.tv_presenceTimeStart.setText(mData.getJSONObject(position).getString("enter_time"));
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
        private ImageView iv_kehadiran;
        private TextView tv_classHistory;
        private TextView tv_courseHistory;
        private TextView tv_dateHistory;
        private TextView tv_timeHistory;
        private TextView tv_roomHistory;
        private TextView tv_presenceTimeStart;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_kehadiran = itemView.findViewById(R.id.iv_kehadiran);
            tv_courseHistory = itemView.findViewById(R.id.tv_courseHistory);
            tv_classHistory = itemView.findViewById(R.id.tv_classHistory);
            tv_dateHistory = itemView.findViewById(R.id.tv_dateHistory);
            tv_roomHistory = itemView.findViewById(R.id.tv_roomHistory);
            tv_presenceTimeStart = itemView.findViewById(R.id.tv_presenceTimeStart);
            tv_timeHistory = itemView.findViewById(R.id.tv_timeHistory);
        }
    }
}
