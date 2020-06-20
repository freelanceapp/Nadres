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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.databinding.FragmentCodeVerificationBinding;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Code_Verification extends Fragment {
    private static final String TAG = "DATA";
    private static final String TAG2 = "Type";

    private SignInActivity activity;
    private FragmentCodeVerificationBinding binding;
    private boolean canResend = true;
    private String phone,phone_code;
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

    public static Fragment_Code_Verification newInstance(String phone,String phone_code) {
       Bundle bundle = new Bundle();
        bundle.putString(TAG, phone);
       bundle.putString(TAG2,phone_code);
        Fragment_Code_Verification fragment_code_verification = new Fragment_Code_Verification();
        fragment_code_verification.setArguments(bundle);
        return fragment_code_verification;
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
          phone =  bundle.getString(TAG);
           phone_code=bundle.getString(TAG2);
        }

        activity = (SignInActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        binding.btnConfirm.setOnClickListener(v -> {
            Common.CloseKeyBoard(activity,binding.edtCode);
//activity.displayFragmentChooseType(phone,phone_code);
            checkData();

        });

//        binding.btnResend.setOnClickListener(v -> {
//
//            if (canResend) {
//                startCounter();
//                sendverficationcode(userModel.getPhone(),userModel.getPhone_code().replace("00","+"));
//
//            }
//        });


       // startCounter();

       authn();
       new Handler().postDelayed(new Runnable() {
           @Override
            public void run() {
               sendverficationcode(phone,phone_code.replace("00","+"));
            }
        },3);
     //   startCounter();

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
                login(phone_code,phone);
//                    Intent intent = new Intent(activity, HomeActivity.class);
//                    startActivity(intent);
//                    activity.finish();
            }


            }
        });
    }
    private void login(String phone_code, String phone)
    {
        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .login(phone_code,phone)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                              // activity.displayFragmentCodeVerification(response.body(),1);
                                preferences.create_update_userdata(activity,response.body());
                                activity.navigateToHomeActivity();
                            }else
                            {
                                if (response.code() == 422) {
                                //    Toast.makeText(activity, getString(R.string.inc_phone_pas), Toast.LENGTH_SHORT).show();
                                } else if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                }else if (response.code()==401||response.code()==404)
                                {
                                    activity.displayFragmentChooseType(phone,phone_code);
                                 //   Toast.makeText(activity, R.string.inc_phone_pas, Toast.LENGTH_SHORT).show();

                                }else
                                {
                                    //Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage()!=null)
                                {
                                    Log.e("error",t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect")||t.getMessage().toLowerCase().contains("unable to resolve host"))
                                    {
                                      //  Toast.makeText(activity,R.string.something, Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        Toast.makeText(activity,t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (Exception e){}
                        }
                    });
        }catch (Exception e){
            dialog.dismiss();

        }
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




//    private void startCounter() {
//        countDownTimer = new CountDownTimer(60000, 1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                canResend = false;
//
//                int AllSeconds = (int) (millisUntilFinished / 1000);
//                int seconds = AllSeconds % 60;
//
//
//              //  binding.btnResend.setText("00:" + seconds);
//            }
//
//            @Override
//            public void onFinish() {
//                canResend = true;
//             //   binding.btnResend.setText(activity.getResources().getString(R.string.resend));
//            }
//        }.start();
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
