package com.example.mahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mahasiswa.API.SessionManager;
import com.example.mahasiswa.Adapter.ExamscheduleAdapter;
import com.example.mahasiswa.ViewModel.ExamViewModel;

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
    String token;

    public ExamscheduleFragment() {

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

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv =(RecyclerView) v.findViewById(R.id.rv_exam);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout) v.findViewById(R.id.load);

        ExamscheduleAdapter examAdapter = new ExamscheduleAdapter(new ExamscheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                Intent intent = new Intent(getActivity(), ExamcardActivity.class);
                intent.putExtra("data", item.toString());
                startActivity(intent);
            }
        });
        examAdapter.notifyDataSetChanged();
        rv.setAdapter(examAdapter);

        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        examViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(ExamViewModel.class);
        loadingDialog.startLoadingDialog();
        examViewModel.setExamUasSchedule(token);
        examViewModel.getExamUasSchedule().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                loadingDialog.dismissDialog();
                examAdapter.setData(datas);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                examViewModel.setExamUasSchedule(token);
            }
        });

    }
}
