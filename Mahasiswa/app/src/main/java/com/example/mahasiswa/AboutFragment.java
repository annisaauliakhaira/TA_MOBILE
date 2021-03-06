package com.example.mahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mahasiswa.API.SessionManager;
import com.example.mahasiswa.ViewModel.AboutViewModel;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutFragment extends Fragment {
    View v;
    private AboutViewModel aboutViewModel;
    SessionManager sessionManager;
    private TextView tv_name, tv_nim;
    private CardView cv_changePass, cv_examSchedule, cv_examHistory;
    private LoadingDialog loadingDialog;
    private CircleImageView img;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        tv_name = v.findViewById(R.id.tv_name1);
        tv_nim = v.findViewById(R.id.tv_nim1);
        img = v.findViewById(R.id.iv_Profile);


        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        aboutViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(AboutViewModel.class);
        loadingDialog.startLoadingDialog();
        aboutViewModel.setAbout(token);
        aboutViewModel.getAbout().observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                if(stringStringHashMap.size() > 0){
                    tv_name.setText(stringStringHashMap.get(aboutViewModel.NAME));
                    tv_nim.setText("NIM. " +stringStringHashMap.get(aboutViewModel.NIM));
                    if(!stringStringHashMap.get(aboutViewModel.IMAGE).equals("")){
                        Glide.with(getContext())
                                .load(stringStringHashMap
                                        .get(aboutViewModel.IMAGE))
                                .error(R.drawable.logo_unand)
                                .into(img);
                    }
                }
                loadingDialog.dismissDialog();
            }
        });

        cv_changePass = (CardView) v.findViewById(R.id.cv_changePassword);
        cv_examSchedule = (CardView) v.findViewById(R.id.cv_examSchedule);
        cv_examHistory = (CardView) v.findViewById(R.id.cv_examHistory);

        cv_examSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabExamscheduleFragment examscheduleFragment = new tabExamscheduleFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, examscheduleFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        cv_examHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabHistoryFragment tabHistoryFragment = new tabHistoryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, tabHistoryFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        cv_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
