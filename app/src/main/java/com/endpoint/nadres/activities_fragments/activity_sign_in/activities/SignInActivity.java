package com.endpoint.nadres.activities_fragments.activity_sign_in.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.endpoint.ghair.R;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.FragmentSignUpAsMarketr;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.Fragment_Code_Verification;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.Fragment_ForgetPassword;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.Fragment_Newpass;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.Fragment_SignUpAsCustomer;
import com.endpoint.ghair.activities_fragments.activity_sign_in.fragments.Fragment_Sign_In;
import com.endpoint.ghair.databinding.ActivitySignInBinding;
import com.endpoint.ghair.language.Language;
import com.endpoint.ghair.models.UserModel;
import com.endpoint.ghair.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ActivitySignInBinding binding;

    private int fragment_count = 0;
    private Fragment_Sign_In fragment_sign_in;
    private Fragment_SignUpAsCustomer fragment_signUpAsCustomer;
    private FragmentSignUpAsMarketr fragmentSignUpAsMarketr;
    private String cuurent_language;
    private Preferences preferences;
    private Fragment_Code_Verification fragment_code_verification;
    private Fragment_ForgetPassword fragment_forgetpass;
    private Fragment_Newpass fragment_newpass;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public void DisplayFragmentSignUpCustomer() {
        fragment_count += 1;
        fragment_signUpAsCustomer = Fragment_SignUpAsCustomer.newInstance();
        if (fragment_signUpAsCustomer.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_signUpAsCustomer).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_signUpAsCustomer, "fragment_signUpAsCustomer").addToBackStack("fragment_signUpAsCustomer").commit();
        }
    }

    public void DisplayFragmentSignUpMarketr() {
        fragment_count+=1;
        fragmentSignUpAsMarketr = FragmentSignUpAsMarketr.newInstance();
        if (fragmentSignUpAsMarketr.isAdded()) {
            fragmentManager.beginTransaction().show(fragmentSignUpAsMarketr).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragmentSignUpAsMarketr, "fragmentSignUpAsMarketr").addToBackStack("fragmentSignUpAsMarketr").commit();
        }
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    public void Back() {
        if (fragment_sign_in !=null&& fragment_sign_in.isAdded()&& fragment_sign_in.isVisible())
        {
            finish();

        }else
        {
            if (fragment_count >1) {
                fragment_count -= 1;
                super.onBackPressed();


            } else  {

                finish();

            }
        }


    }
    public void displayFragmentCodeVerification(UserModel userModel,int type) {
        fragment_count ++;
        fragment_code_verification = Fragment_Code_Verification.newInstance(userModel,type);
        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_code_verification, "fragment_code_verification").addToBackStack("fragment_code_verification").commit();

    }
    public void displayFragmentForgetpass() {
        fragment_count ++;
        fragment_forgetpass = Fragment_ForgetPassword.newInstance();

        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_forgetpass, "fragment_forgetpass").addToBackStack("fragment_forgetpass").commit();

    }
    public void displayFragmentNewpass(UserModel userModel) {
        fragment_count ++;
        fragment_newpass = Fragment_Newpass.newInstance(userModel,1);

        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_newpass, "fragment_newpass").addToBackStack("fragment_newpass").commit();

    }
}
