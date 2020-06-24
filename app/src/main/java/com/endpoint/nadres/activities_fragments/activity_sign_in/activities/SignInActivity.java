package com.endpoint.nadres.activities_fragments.activity_sign_in.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.FragmentSignUpAsTeacher;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.Fragment_ChooseType;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.Fragment_Code_Verification;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.Fragment_SignUpAsStudent;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.Fragment_Sign_In;
import com.endpoint.nadres.databinding.ActivitySignInBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity  {

    private FragmentManager fragmentManager;
    private ActivitySignInBinding binding;

    private int fragment_count = 0;
    private Fragment_Sign_In fragment_sign_in;
    private FragmentSignUpAsTeacher fragmentSignUpAsTeacher;
    private Fragment_SignUpAsStudent fragment_signUpAsStudent;
    private String cuurent_language;
    private Preferences preferences;
    private Fragment_Code_Verification fragment_code_verification;
    private Fragment_ChooseType fragment_chooseType;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        Paper.init(this);


        initView();
        if (savedInstanceState == null) {

            DisplayFragmentSignIn();


            //  DisplayFragmentSignIn();

        }


    }

    private void initView() {
        Paper.init(this);
        cuurent_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        fragmentManager = this.getSupportFragmentManager();
        //binding.setBackListener(this);


    }

    public void DisplayFragmentSignIn() {
        fragment_count += 1;
        fragment_sign_in = Fragment_Sign_In.newInstance();
        if (fragment_sign_in.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_sign_in).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_sign_in, "fragment_sign_in").addToBackStack("fragment_sign_in").commit();
        }

    }

    public void DisplayFragmentSignUpTeacher(String phone,String phone_code) {
        fragment_count += 1;
        fragmentSignUpAsTeacher = FragmentSignUpAsTeacher.newInstance(phone,phone_code);
        if (fragmentSignUpAsTeacher.isAdded()) {
            fragmentManager.beginTransaction().show(fragmentSignUpAsTeacher).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragmentSignUpAsTeacher, "fragment_signUpAsCustomer").addToBackStack("fragment_signUpAsCustomer").commit();
        }
    }

    public void DisplayFragmentSignUpStudent(String phone, String phone_code) {
        fragment_count += 1;
        fragment_signUpAsStudent = Fragment_SignUpAsStudent.newInstance(phone, phone_code);
        if (fragment_signUpAsStudent.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_signUpAsStudent).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_signUpAsStudent, "fragmentSignUpAsMarketr").addToBackStack("fragmentSignUpAsMarketr").commit();
        }
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    public void Back() {
        if (fragment_count==1)
        {
            finish();

        } else {
            if (fragment_count > 1) {
                fragment_count -= 1;
                super.onBackPressed();


            } else {

                finish();

            }
        }


    }

    public void displayFragmentCodeVerification(String phone, String phone_code) {
        fragment_count++;
        fragment_code_verification = Fragment_Code_Verification.newInstance(phone, phone_code);
        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_code_verification, "fragment_code_verification").addToBackStack("fragment_code_verification").commit();

    }

    public void displayFragmentChooseType(String phone, String phone_code) {
        fragment_count++;
        fragment_chooseType = Fragment_ChooseType.newInstance(phone, phone_code);

        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_chooseType, "fragment_forgetpass").addToBackStack("fragment_forgetpass").commit();

    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment:fragmentList){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment:fragmentList){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
