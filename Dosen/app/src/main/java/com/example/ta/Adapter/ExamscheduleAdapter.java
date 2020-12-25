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

public class ExamscheduleAdapter extends RecyclerView.Adapter<ExamscheduleAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();
    public OnItemClickListener listener;

    public ExamscheduleAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(JSONArray items){
        mData = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_exam,
                viewGroup, false);
        return new MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.tv_class.setText(mData.getJSONObject(position).getString("course_name"));
            holder.tv_classId.setText(mData.getJSONObject(position).getString("class_name"));
            holder.tv_date.setText(mData.getJSONObject(position).getString("date"));
            holder.tv_time.setText(mData.getJSONObject(position).getString("start_hour")+" - "
                    +mData.getJSONObject(position).getString("ending_hour"));
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
        private TextView tv_class;
        private TextView tv_date;
        private TextView tv_classId;
        private TextView tv_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_class = (TextView)itemView.findViewById(R.id.tv_class);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_classId = (TextView) itemView.findViewById(R.id.tv_classIdExam);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
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
