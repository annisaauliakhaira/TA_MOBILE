package com.example.pengawas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pengawas.API.SessionManager;
import com.example.pengawas.ViewModel.AboutViewModel;

import java.util.HashMap;

public class AboutFragment extends Fragment {
    View v;
    private AboutViewModel aboutViewModel;
    SessionManager sessionManager;
    private TextView tv_name, tv_nip, tv_email;

    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        tv_name = v.findViewById(R.id.tv_name1);
        tv_nip = v.findViewById(R.id.tv_nip1);
        tv_email = v.findViewById(R.id.tv_email);

        sessionManager = new SessionManager(getContext());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        aboutViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(AboutViewModel.class);
        aboutViewModel.setAbout(token);
        aboutViewModel.getAbout().observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                if(stringStringHashMap.size() > 0){
                    tv_name.setText(stringStringHashMap.get(aboutViewModel.NAME));
                    tv_nip.setText("NIP. "+ stringStringHashMap.get(aboutViewModel.NIP));
                    tv_email.setText(stringStringHashMap.get(aboutViewModel.EMAIL));

                }
            }
        });
    }
}
