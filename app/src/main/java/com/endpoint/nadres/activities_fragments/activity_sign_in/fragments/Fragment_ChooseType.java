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
import com.endpoint.nadres.interfaces.Listeners;
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

public class Fragment_ChooseType extends Fragment implements Listeners.BackListener {


    private SignInActivity activity;
    private FragmentUserTypeBinding binding;
    private boolean canResend = true;
    private static final String TAG = "DATA";
    private static final String TAG2 = "Type";
    private String phone, phone_code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_type, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    public static Fragment_ChooseType newInstance(String phone, String phone_code) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, phone);
        bundle.putString(TAG2, phone_code);
        Fragment_ChooseType fragment_chooseType = new Fragment_ChooseType();
        fragment_chooseType.setArguments(bundle);
        return fragment_chooseType;
    }

    private void initView() {
        binding.setBackListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            phone = bundle.getString(TAG);
            phone_code = bundle.getString(TAG2);
        }

        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        binding.btnConfirm.setOnClickListener(v -> {
            if (binding.rbTeach.isChecked()) {
                activity.DisplayFragmentSignUpTeacher(phone,phone_code);
            } else {
                activity.DisplayFragmentSignUpStudent(phone, phone_code);
            }
        });


    }


    @Override
    public void back() {
        activity.Back();
    }
}
