package com.example.ta;

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

import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamscheduleAdapter;
import com.example.ta.ViewModel.ExamViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ExamscheduleFragment extends Fragment {

    View v;
    private RecyclerView rv;
    private ArrayList<Examschedule> examscheduleList;
    private ExamViewModel examViewModel;
    SessionManager sessionManager;

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

        final ExamscheduleAdapter examAdapter = new ExamscheduleAdapter();
        examAdapter.notifyDataSetChanged();
        rv.setAdapter(examAdapter);
        examscheduleList = new ArrayList<>();

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        examViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(ExamViewModel.class);
        examViewModel.setExamschedule(token);
        examViewModel.getExamschedule().observe(this, new Observer<ArrayList<Examschedule>>() {

            @Override
            public void onChanged(ArrayList<Examschedule> examschedules) {
                if (examschedules != null){
                    Log.e("COba", examschedules.toString());
                    examAdapter.setData(examschedules);
                }
            }
        });
    }
}
