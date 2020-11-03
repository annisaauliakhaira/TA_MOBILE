package com.example.pengawas.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pengawas.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExamclassAdapter extends RecyclerView.Adapter<ExamclassAdapter.MyViewHolder> {
    private JSONArray mData = new JSONArray();
    public OnItemClickListener listener;

    public ExamclassAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(JSONArray itemExam){
        mData=itemExam;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_student, viewGroup, false);
        return new ExamclassAdapter.MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.tv_studentNim.setText(mData.getJSONObject(position).getString("nim"));
            holder.tv_studentName.setText(mData.getJSONObject(position).getString("name"));
            String presence_status = mData.getJSONObject(position).getString("presence_status");
            String keterangan = "Status Not Found";
            if (presence_status.equals("0")){
                keterangan = "Belum Ujian";
            }else if (presence_status.equals("1")){
                keterangan = "Sudah Ujian";
            }else if (presence_status.equals("2")){
                keterangan = "Tidak Ujian";
            }
            holder.tv_presence.setText(keterangan);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_studentNim;
        private TextView tv_studentName;
        private TextView tv_presence;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_studentNim = (TextView) itemView.findViewById(R.id.tv_studentNim);
            tv_studentName = (TextView) itemView.findViewById(R.id.tv_studentName);
            tv_presence = (TextView) itemView.findViewById(R.id.tv_presence);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(JSONObject item) throws JSONException;
    }
}