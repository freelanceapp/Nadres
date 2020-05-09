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

import com.endpoint.ghair.R;
import com.endpoint.ghair.activities_fragments.activity_home.HomeActivity;
import com.endpoint.ghair.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.ghair.databinding.FragmentCodeVerificationBinding;
import com.endpoint.ghair.models.UserModel;
import com.endpoint.ghair.preferences.Preferences;
import com.endpoint.ghair.share.Common;
import com.endpoint.ghair.tags.Tags;
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

public class Fragment_Code_Verification extends Fragment {
    private static final String TAG = "DATA";
    private static final String TAG2 = "Type";

    private SignInActivity activity;
    private FragmentCodeVerificationBinding binding;
    private boolean canResend = true;
    private UserModel userModel;
    private CountDownTimer countDownTimer;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String id;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
private Preferences preferences;
    private ProgressDialog dialo;
    private String code;
private int type;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_code_verification, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    public static Fragment_Code_Verification newInstance(UserModel userModel,int type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, userModel);
        bundle.putInt(TAG2,type);
        Fragment_Code_Verification fragment_code_verification = new Fragment_Code_Verification();
        fragment_code_verification.setArguments(bundle);
        return fragment_code_verification;
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userModel = (UserModel) bundle.getSerializable(TAG);
            type=bundle.getInt(TAG2);
        }

        activity = (SignInActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        binding.btnConfirm.setOnClickListener(v -> {

            checkData();

        });

        binding.btnResend.setOnClickListener(v -> {

            if (canResend) {
                startCounter();
                sendverficationcode(userModel.getPhone(),userModel.getPhone_code().replace("00","+"));

            }
        });


        startCounter();

        authn();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendverficationcode(userModel.getPhone(),userModel.getPhone_code().replace("00","+"));
            }
        },3);
        startCounter();

    }

    private void authn() {

        mAuth= FirebaseAuth.getInstance();

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.e("id",s);
                id=s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                Log.e("code",phoneAuthCredential.getSmsCode());
//phoneAuthCredential.getProvider();
                if(phoneAuthCredential.getSmsCode()!=null){
                    code=phoneAuthCredential.getSmsCode();
                    binding.edtCode.setText(code);
                    siginwithcredental(phoneAuthCredential);}
                else {
                    siginwithcredental(phoneAuthCredential);
                }


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("llll",e.getMessage());
            }


        };

    }
    private void verfiycode(String code) {
        // Toast.makeText(register_activity,code,Toast.LENGTH_LONG).show();

        Log.e("ccc",code);
        if(id!=null){

            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(id,code);
            siginwithcredental(credential);}
    }

    private void siginwithcredental(PhoneAuthCredential credential) {
        dialo = Common.createProgressDialog(activity,getString(R.string.wait));
        dialo.setCancelable(false);
        dialo.show();
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                  //  Log.e("data",phone);
                    dialo.dismiss();

                    // activity.NavigateToHomeActivity();
                    mAuth.signOut();
                    if(type==1){
                    preferences.create_update_userdata(activity,userModel);
                    preferences.create_update_session(activity, Tags.session_login);
                    Intent intent = new Intent(activity, HomeActivity.class);
                    startActivity(intent);
                    activity.finish();}
                else {
activity.displayFragmentNewpass(userModel);
                   //     checkconfirmation(code);


                }}


            }
        });
    }

    private void sendverficationcode(String phone, String phone_code) {
        Log.e("kkk",phone_code+phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_code+phone,59, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,  mCallbacks);

    }
    private void checkData() {
        String code = binding.edtCode.getText().toString().trim();
        if (!TextUtils.isEmpty(code)) {
            Common.CloseKeyBoard(activity, binding.edtCode);
            verfiycode(code);

        } else {
            binding.edtCode.setError(getString(R.string.field_req));
        }
    }




    private void startCounter() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds = AllSeconds % 60;


                binding.btnResend.setText("00:" + seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setText(activity.getResources().getString(R.string.resend));
            }
        }.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
