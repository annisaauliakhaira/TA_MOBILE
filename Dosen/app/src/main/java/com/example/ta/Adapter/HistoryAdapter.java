package com.example.ta.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();

    public OnItemClickListener listener;

    public HistoryAdapter(OnItemClickListener listener){
        this.listener=listener;
    }


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
            holder.tv_classHistory.setText(mData.getJSONObject(position).getString("courses_name"));
            holder.tv_classIdHistory.setText(mData.getJSONObject(position).getString("class_name"));
            holder.tv_dateHistory.setText(mData.getJSONObject(position).getString("date"));
            holder.tv_totalStudent.setText("Students : "+mData.getJSONObject(position).getString("total"));
            holder.tv_presenceTotal.setText("Presence : "+mData.getJSONObject(position).getString("hadir"));
            holder.tv_absenceTotal.setText("Absence : "+mData.getJSONObject(position).getString("tidak_hadir"));
            holder.tv_permitTotal.setText("Permit : "+mData.getJSONObject(position).getString("izin"));
            holder.bind(mData.getJSONObject(position), listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_classHistory;
        private TextView tv_classIdHistory;
        private TextView tv_dateHistory;
        private TextView tv_totalStudent;
        private TextView tv_presenceTotal;
        private TextView tv_absenceTotal;
        private TextView tv_permitTotal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_classHistory = itemView.findViewById(R.id.tv_classNameH);
            tv_classIdHistory = itemView.findViewById(R.id.tv_classCodeH);
            tv_dateHistory = itemView.findViewById(R.id.tv_dateH);
            tv_totalStudent = itemView.findViewById(R.id.tv_totalStudent);
            tv_presenceTotal = itemView.findViewById(R.id.tv_presenceTotal);
            tv_absenceTotal = itemView.findViewById(R.id.tv_absenceTotal);
            tv_permitTotal = itemView.findViewById(R.id.tv_permitTotal);
        }

        public void bind (final JSONObject item, final OnItemClickListener l){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        listener.onItemClick(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(JSONObject item) throws JSONException;
    }

}
