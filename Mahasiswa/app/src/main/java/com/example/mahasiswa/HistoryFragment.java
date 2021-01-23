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
import com.example.mahasiswa.Adapter.HistoryAdapter;
import com.example.mahasiswa.ViewModel.HistoryViewModel;

import org.json.JSONArray;

import java.util.HashMap;

public class HistoryFragment extends Fragment {
    View v;
    private RecyclerView rv;
    private HistoryViewModel historyViewModel;
    SessionManager sessionManager;
    private LoadingDialog loadingDialog;

    public HistoryFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rv =(RecyclerView) v.findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        HistoryAdapter historyAdapter = new HistoryAdapter();
        historyAdapter.notifyDataSetChanged();
        rv.setAdapter(historyAdapter);

        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        historyViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(HistoryViewModel.class);
        loadingDialog.startLoadingDialog();
        historyViewModel.setHistoryUas(token);
        historyViewModel.getHistoryUas().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                loadingDialog.dismissDialog();
                historyAdapter.setData(datas);
            }
        });
    }
}
