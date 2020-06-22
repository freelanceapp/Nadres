package com.endpoint.nadres.activities_fragments.activity_chat_type;


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
import com.endpoint.nadres.databinding.ActivityChatTypeBinding;
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

public class ChatTypeActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityChatTypeBinding binding;
    private String lang;
    private String teacher_id;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_type);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            teacher_id = intent.getStringExtra("teacher_id");

        }
    }


    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.btnConfirm.setOnClickListener(v -> {
            if (binding.rbGroup.isChecked()) {

            } else {
            }
        });



    }



    @Override
    public void back() {
        finish();
    }

}