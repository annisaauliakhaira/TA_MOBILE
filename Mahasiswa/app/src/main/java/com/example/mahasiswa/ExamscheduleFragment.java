package com.example.mahasiswa;

import android.os.Bundle;
import android.util.Log;
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

import com.example.mahasiswa.API.SessionManager;
import com.example.mahasiswa.Adapter.ExamscheduleAdapter;
import com.example.mahasiswa.ViewModel.ExamViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ExamscheduleFragment extends Fragment {
    View v;
    private RecyclerView rv;
    private ArrayList<ExamSchedule> examscheduleList;
    private ExamViewModel examViewModel;
    SessionManager sessionManager;

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

        final ExamscheduleAdapter examAdapter = new ExamscheduleAdapter();
        examAdapter.notifyDataSetChanged();
        rv.setAdapter(examAdapter);
        examscheduleList = new ArrayList<>();

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        examViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(ExamViewModel.class);
        examViewModel.setExamschedule(token);
        examViewModel.getExamschedule().observe(this, new Observer<ArrayList<ExamSchedule>>() {
            @Override
            public void onChanged(ArrayList<ExamSchedule> examSchedules) {
                if (examSchedules != null){
                    Log.e("COba", examSchedules.toString());
                    examAdapter.setData(examSchedules);
                }
            }
        });
    }
}
