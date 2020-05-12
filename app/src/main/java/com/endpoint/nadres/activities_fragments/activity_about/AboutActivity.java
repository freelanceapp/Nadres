package com.endpoint.nadres.activities_fragments.activity_about;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.endpoint.nadres.R;
import com.endpoint.nadres.databinding.ActivityAboutBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityAboutBinding binding;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        initView();

    }


    private void initView() {

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());binding.setLang(lang);
        binding.setBackListener(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
      //  getAbout();


    }

//    private void getAbout() {
//
//        Api.getService(Tags.base_url)
//                .getterms(lang)
//                .enqueue(new Callback<App_Data_Model>() {
//                    @Override
//                    public void onResponse(Call<App_Data_Model> call, Response<App_Data_Model> response) {
//                        binding.progBar.setVisibility(View.GONE);
//                        if (response.isSuccessful() && response.body() != null && response.body().getAbout_us() != null ) {
//
//                            binding.setAppdatamodel(response.body());
//                        } else {
//                            try {
//
//                                Log.e("error", response.code() + "_" + response.errorBody().string());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<App_Data_Model> call, Throwable t) {
//                        try {
//                            binding.progBar.setVisibility(View.GONE);
//                            if (t.getMessage() != null) {
//                                Log.e("error", t.getMessage());
//                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                    Toast.makeText(AboutActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(AboutActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//    }

    @Override
    public void back() {
        finish();
    }
}
