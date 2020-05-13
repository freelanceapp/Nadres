package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.endpoint.nadres.databinding.FragmentProfileBinding;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Profile extends Fragment {

    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private LinearLayoutManager manager;
    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {
        return new Fragment_Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences=Preferences.getInstance();
        userModel=preferences.getUserData(activity);


    }


}
