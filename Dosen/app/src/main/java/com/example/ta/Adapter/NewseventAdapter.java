package com.example.ta.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewseventAdapter extends RecyclerView.Adapter<NewseventAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();
    public OnItemClickListener listener;

    public NewseventAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(JSONArray itemNews){
        mData = itemNews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_newsevent, viewGroup, false);
        return new NewseventAdapter.MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.tv_newsEvent.setText(mData.getJSONObject(position).getString("news_event"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_newsEvent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_newsEvent = (TextView) itemView.findViewById(R.id.tv_newsData);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(JSONObject item) throws JSONException;
    }
}
