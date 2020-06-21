package com.endpoint.nadres.activities_fragments.activity_knowdetilas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.knowledge_activity.KnowledegeActivity;
import com.endpoint.nadres.adapters.Knowdetials_Adapter;
import com.endpoint.nadres.databinding.ActivityKnowledegedetilasBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ArticleModel;
import com.endpoint.nadres.models.SingleArticleModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowledegeDetialsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityKnowledegedetilasBinding binding;
    private GridLayoutManager manager;
    private List<SingleArticleModel> singleArticleModelList;
    private Knowdetials_Adapter knowdetials_adapter;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isLoading = false;
    private int current_page = 1;
    private String lang;
    private String articel_id;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_knowledegedetilas);
        getDataFromIntent();
        initView();
        getArticle();
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.input), PorterDuff.Mode.SRC_IN);
        manager = new GridLayoutManager(this, 2);
        binding.recView.setLayoutManager(manager);
        singleArticleModelList = new ArrayList<>();
        knowdetials_adapter = new Knowdetials_Adapter(singleArticleModelList, this);
        binding.recView.setAdapter(knowdetials_adapter);
        binding.setBackListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);


        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = ((LinearLayoutManager) binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    if (total_item >= 20 && (total_item - last_visible_item) == 5 && !isLoading) {

                        isLoading = true;
                        int page = current_page + 1;
                        singleArticleModelList.add(null);
                        knowdetials_adapter.notifyItemInserted(singleArticleModelList.size() - 1);

                        loadMore(page);
                    }
                }
            }
        });
        getArticles();

    }

    private void getArticles() {
        try {
            current_page = 1;
            Api.getService(Tags.base_url)
                    .getArticlesExpext("on", current_page, 20, articel_id)
                    .enqueue(new Callback<ArticleModel>() {
                        @Override
                        public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                singleArticleModelList.clear();
                                singleArticleModelList.addAll(response.body().getData());
                                if (singleArticleModelList.size() > 0) {

                                    knowdetials_adapter.notifyDataSetChanged();

                                    binding.tvNoEvents.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoEvents.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(KnowledegeDetialsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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

    private void loadMore(int page) {
        try {

            Api.getService(Tags.base_url)
                    .getArticlesExpext("on", page, 20, articel_id)
                    .enqueue(new Callback<ArticleModel>() {
                        @Override
                        public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                            isLoading = false;
                            singleArticleModelList.remove(singleArticleModelList.size() - 1);
                            knowdetials_adapter.notifyItemRemoved(singleArticleModelList.size() - 1);


                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                int oldPos = singleArticleModelList.size() - 1;

                                singleArticleModelList.addAll(response.body().getData());

                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getCurrent_page();
                                    knowdetials_adapter.notifyItemRangeChanged(oldPos, singleArticleModelList.size() - 1);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(KnowledegeDetialsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                                    knowdetials_adapter.notifyItemRemoved(singleArticleModelList.size() - 1);

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


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            articel_id = intent.getStringExtra("article_id");

        }

    }

    private void getArticle() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .articledetials(articel_id)
                    .enqueue(new Callback<SingleArticleModel>() {
                        @Override
                        public void onResponse(Call<SingleArticleModel> call, Response<SingleArticleModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UPDATEUI(response.body());
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(KnowledegeDetialsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(KnowledegeDetialsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleArticleModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        // Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(KnowledegeDetialsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void UPDATEUI(SingleArticleModel body) {
        binding.setModel(body);
    }

    @Override
    public void back() {
        finish();
    }


}
