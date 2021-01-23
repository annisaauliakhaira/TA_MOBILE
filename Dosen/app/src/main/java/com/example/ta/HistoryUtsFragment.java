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
import com.example.ta.Adapter.HistoryAdapter;
import com.example.ta.ViewModel.HistoryViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HistoryUtsFragment extends Fragment {

    View v;
    private RecyclerView rv;
    private HistoryViewModel historyViewModel;
    SessionManager sessionManager;
    private LoadingDialog loadingDialog;

    public HistoryUtsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = v.findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        HistoryAdapter historyAdapter = new HistoryAdapter(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                Intent intent = new Intent(getActivity(), ExamHistoryActivity.class);
                intent.putExtra("data", item.toString());
                startActivity(intent);
            }
        });

        historyAdapter.notifyDataSetChanged();
        rv.setAdapter(historyAdapter);

        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        historyViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(HistoryViewModel.class);
        loadingDialog.startLoadingDialog();
        historyViewModel.setHistoryUts(token);
        historyViewModel.getHistoryUts().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray datas) {
                loadingDialog.dismissDialog();
                historyAdapter.setData(datas);
            }
        });
    }
}
