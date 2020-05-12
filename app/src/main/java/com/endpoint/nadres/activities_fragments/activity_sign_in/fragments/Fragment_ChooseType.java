package com.endpoint.nadres.activities_fragments.activity_sign_in.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.databinding.FragmentChooseStepBinding;
import com.endpoint.nadres.databinding.FragmentUserTypeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class Fragment_ChooseType extends Fragment {


    private SignInActivity activity;
    private FragmentUserTypeBinding binding;
    private boolean canResend = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_type, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    public static Fragment_ChooseType newInstance() {

        return new Fragment_ChooseType();
    }

    private void initView() {


        activity = (SignInActivity) getActivity();
        Paper.init(activity);
binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(binding.rbTeach.isChecked()){
            activity.DisplayFragmentSignUpTeacher();
        }
        else {
            activity.DisplayFragmentSignUpStudent();
        }
    }
});





    }






}
