package com.endpoint.nadres.activities_fragments.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.databinding.ActivitySplashBinding;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class Splash_Activity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Animation animation;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        preferences = Preferences.getInstance();

        animation= AnimationUtils.loadAnimation(getBaseContext(),R.anim.lanuch);
        binding.cons.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String session = preferences.getSession(Splash_Activity.this);
                if (session.equals(Tags.session_login))
                {
                    Intent intent=new Intent(Splash_Activity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    Intent intent=new Intent(Splash_Activity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
