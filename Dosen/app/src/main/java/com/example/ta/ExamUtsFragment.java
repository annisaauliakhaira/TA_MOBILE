package com.example.ta;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamscheduleAdapter;
import com.example.ta.ViewModel.ExamViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ExamUtsFragment extends Fragment {

    View v;
    private RecyclerView rv;
    private ExamViewModel examViewModel;
    SessionManager sessionManager;
    LoadingDialog loadingDialog;
    String token;

    public ExamUtsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_examschedule, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        rv =(RecyclerView) v.findViewById(R.id.rv_exam);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingDialog = new LoadingDialog(getActivity());

        ExamscheduleAdapter examscheduleAdapter = new ExamscheduleAdapter(new ExamscheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                Intent intent = new Intent(getActivity(), ExamclassActivity.class);
                intent.putExtra("data", item.toString());
                startActivity(intent);
            }
        });

        examscheduleAdapter.notifyDataSetChanged();
        rv.setAdapter(examscheduleAdapter);

        sessionManager = new SessionManager(getContext());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);


        examViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(ExamViewModel.class);
        examViewModel.getExamUtsSchedule().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                loadingDialog.dismissDialog();
                examscheduleAdapter.setData(datas);
            }
        });
    }

    public void onResume() {
        super.onResume();
        loadingDialog.startLoadingDialog();
        examViewModel.setExamUtsSchedule(token);
    }
}