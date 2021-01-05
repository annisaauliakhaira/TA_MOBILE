package com.example.ta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamscheduleAdapter;
import com.example.ta.ViewModel.ExamViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ExamscheduleFragment extends Fragment {

    View v;
    private RecyclerView rv;
    private ExamViewModel examViewModel;
    SessionManager sessionManager;
    private LoadingDialog loadingDialog;

    public ExamscheduleFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_examschedule, container, false);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v, savedInstanceState);

        rv =(RecyclerView) v.findViewById(R.id.rv_exam);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingDialog = new LoadingDialog(getActivity());

        ExamscheduleAdapter examAdapter = new ExamscheduleAdapter(new ExamscheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                Intent intent = new Intent(getActivity(), ExamclassActivity.class);
                intent.putExtra("data", item.toString());
                startActivity(intent);
            }

        });
        examAdapter.notifyDataSetChanged();
        rv.setAdapter(examAdapter);

        sessionManager = new SessionManager(getContext());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        examViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(ExamViewModel.class);
        loadingDialog.startLoadingDialog();
        examViewModel.setExamUasSchedule(token);
        examViewModel.getExamUasSchedule().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                loadingDialog.dismissDialog();
                examAdapter.setData(datas);
            }
        });
    }
}
