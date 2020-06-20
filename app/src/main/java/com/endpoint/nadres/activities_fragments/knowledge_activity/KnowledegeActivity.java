package com.endpoint.nadres.activities_fragments.knowledge_activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.adapters.Know_Adapter;
import com.endpoint.nadres.databinding.ActivityKnowledegesBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ArticleModel;
import com.endpoint.nadres.models.SingleArticleModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowledegeActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityKnowledegesBinding binding;
    private LinearLayoutManager manager;
    private List<SingleArticleModel> singleArticleModelList;
    private Know_Adapter know_adapter;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isLoading = false;
    private int current_page = 1;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_knowledeges);
        initView();
//        if(userModel!=null){
//        getnotification();}
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.input), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        singleArticleModelList = new ArrayList<>();
        know_adapter = new Know_Adapter(singleArticleModelList, this);
        binding.recView.setAdapter(know_adapter);
        binding.setBackListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = ((LinearLayoutManager)binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    if (total_item>=20&&(total_item-last_visible_item)==5&&!isLoading)
                    {

                        isLoading = true;
                        int page = current_page+1;
                        singleArticleModelList.add(null);
                        know_adapter.notifyItemInserted(singleArticleModelList.size()-1);

                        loadMore(page);
                    }
                }
            }
        });
        getArticles();

    }

    private void getArticles()
    {
        try {
            current_page = 1;
            Api.getService(Tags.base_url)
                    .getaricles("on",current_page,20)
                    .enqueue(new Callback<ArticleModel>() {
                        @Override
                        public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                singleArticleModelList.clear();
                                singleArticleModelList.addAll(response.body().getData());
                                if (singleArticleModelList.size() > 0) {

                                    know_adapter.notifyDataSetChanged();

                                    binding.tvNoEvents.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoEvents.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(KnowledegeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                   // Toast.makeText(KnowledegeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArticleModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                      //  Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                       // Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void loadMore(int page)
    {
        try {

            Api.getService(Tags.base_url)
                    .getaricles("on",page,20)
                    .enqueue(new Callback<ArticleModel>() {
                        @Override
                        public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                            isLoading = false;
                            singleArticleModelList.remove(singleArticleModelList.size() - 1);
                            know_adapter.notifyItemRemoved(singleArticleModelList.size() - 1);


                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                int oldPos = singleArticleModelList.size()-1;

                                singleArticleModelList.addAll(response.body().getData());

                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getCurrent_page();
                                    know_adapter.notifyItemRangeChanged(oldPos,singleArticleModelList.size()-1);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(KnowledegeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArticleModel> call, Throwable t) {
                            try {

                                if (singleArticleModelList.get(singleArticleModelList.size() - 1) == null) {
                                    isLoading = false;
                                    singleArticleModelList.remove(singleArticleModelList.size() - 1);
                                    know_adapter.notifyItemRemoved(singleArticleModelList.size() - 1);

                                }

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                     //   Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                       // Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }
    @Override
    public void back() {
        finish();
    }




}
