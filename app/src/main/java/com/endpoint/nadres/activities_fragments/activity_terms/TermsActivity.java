package com.endpoint.nadres.activities_fragments.activity_terms;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;


import com.endpoint.nadres.R;
import com.endpoint.nadres.databinding.ActivityTermsBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.SettingModel;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityTermsBinding binding;
    private String lang;
    private int type;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("type"))
        {
            type = intent.getIntExtra("type",0);

        }
    }


    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        if (type==1)
        {
            binding.setTitle(getString(R.string.terms_and_conditions));
        }else if (type ==2)
        {
            binding.setTitle(getString(R.string.about_app));

        }


        getAppData();

    }
    private void getAppData()
    {

        Api.getService(Tags.base_url)
                .getSettings()
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {

                            if (type==1)
                            {

                                binding.setContent(response.body().getData().getTerm_conditions());
                            }else
                            {
                                binding.setContent(response.body().getData().getAbout_app());

                            }

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(TermsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(TermsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                  //  Toast.makeText(AboutAppActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TermsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }


    @Override
    public void back() {
        finish();
    }

}