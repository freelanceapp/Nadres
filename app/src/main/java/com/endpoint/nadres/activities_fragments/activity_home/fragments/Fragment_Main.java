package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_teachers.TeacherActivity;
import com.endpoint.nadres.activities_fragments.knowledge_activity.KnowledegeActivity;
import com.endpoint.nadres.databinding.FragmnetMainBinding;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main extends Fragment {
    private static Dialog dialog;
    private HomeActivity activity;
    private FragmnetMainBinding binding;
    private LinearLayoutManager manager, manager2;
    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmnet_main, container, false);
        initView();

        return binding.getRoot();
    }


    private void initView() {

        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        binding.lllisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "listening");
                startActivity(intent);
            }
        });
        binding.llgrammer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "grammer");
                startActivity(intent);
            }
        });
        binding.llread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "reading");
                startActivity(intent);
            }
        });
        binding.llwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "writing");
                startActivity(intent);
            }
        });
        binding.llspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "speaking");
                startActivity(intent);
            }
        });
        binding.llvoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "vocabulary");
                startActivity(intent);
            }
        });
        binding.lldictation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TeacherActivity.class);
                intent.putExtra("skill", "dictation");
                startActivity(intent);
            }
        });
        binding.llknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, KnowledegeActivity.class);
                startActivity(intent);
            }
        });


    }

}
