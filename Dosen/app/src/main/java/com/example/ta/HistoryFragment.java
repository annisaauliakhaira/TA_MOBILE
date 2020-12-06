package com.example.ta;

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
import com.example.ta.Adapter.HistoryAdapter;
import com.example.ta.ViewModel.HistoryViewModel;

import org.json.JSONArray;

import java.util.HashMap;

public class HistoryFragment extends Fragment {
    View v;
    private RecyclerView rv;
    private HistoryViewModel historyViewModel;
    SessionManager sessionManager;

    public HistoryFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv = v.findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        HistoryAdapter historyAdapter = new HistoryAdapter();
        historyAdapter.notifyDataSetChanged();
        rv.setAdapter(historyAdapter);

        sessionManager = new SessionManager(getContext());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        historyViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(HistoryViewModel.class);
        historyViewModel.setHistory(token);
        historyViewModel.getHistory().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                historyAdapter.setData(datas);
            }
        });
    }
}
