package com.endpoint.nadres.activities_fragments.activity_notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.adapters.Notification_Adapter;
import com.endpoint.nadres.databinding.ActivityNotificationsBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.NotificationDataModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class NotificationsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityNotificationsBinding binding;
    private LinearLayoutManager manager;
    private List<NotificationDataModel.NotificationModel> notificationModelList;
    private Notification_Adapter notification_adapter;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isLoading = false;
    private int current_page2 = 1;
    private String lang;
    private boolean isFromFireBase = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);
        getDataFromIntent();
        initView();
//        if(userModel!=null){
//        getnotification();}
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("not")){
            isFromFireBase = true;
        }
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.input), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        notificationModelList = new ArrayList<>();
        notification_adapter = new Notification_Adapter(notificationModelList, this);
        binding.recView.setAdapter(notification_adapter);
        binding.setBackListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());binding.setLang(lang);
        adddata();
//        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (dy > 0) {
//                    int totalItems = notification_adapter.getItemCount();
//                    int lastVisiblePos = manager.findLastCompletelyVisibleItemPosition();
//                    if (totalItems > 5 && (totalItems - lastVisiblePos) == 1 && !isLoading) {
//                        isLoading = true;
//                        notificationModelList.add(null);
//                        notification_adapter.notifyItemInserted(notificationModelList.size() - 1);
//                        int page = current_page2 + 1;
//                        loadMore(page);
//
//
//                    }
//                }
//            }
//        });


    }

    private void adddata() {
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notificationModelList.add(new NotificationDataModel.NotificationModel());
        notification_adapter.notifyDataSetChanged();

    }

//    private void getnotification() {
//        notificationModelList.clear();
//        notification_adapter.notifyDataSetChanged();
//        binding.progBar.setVisibility(View.VISIBLE);
//        try {
//
//
//            Api.getService(Tags.base_url)
//                    .getnotification(1, "Bearer" + " " + userModel.getToken(),lang)
//                    .enqueue(new Callback<NotificationDataModel>() {
//                        @Override
//                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
//                            binding.progBar.setVisibility(View.GONE);
//                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
//                                notificationModelList.clear();
//                                notificationModelList.addAll(response.body().getData());
//                                if (response.body().getData().size() > 0) {
//                                    // rec_sent.setVisibility(View.VISIBLE);
//                                    //  Log.e("data",response.body().getData().get(0).getAr_title());
//
//                                    binding.llNoStore.setVisibility(View.GONE);
//                                    notification_adapter.notifyDataSetChanged();
//                                    //   total_page = response.body().getMeta().getLast_page();
//
//                                } else {
//                                    notification_adapter.notifyDataSetChanged();
//
//                                    binding.llNoStore.setVisibility(View.VISIBLE);
//
//                                }
//                            } else {
//                                notification_adapter.notifyDataSetChanged();
//
//                                binding.llNoStore.setVisibility(View.VISIBLE);
//
//                                //Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                try {
//                                    Log.e("Error_code", response.code() + "_" + response.errorBody().string());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
//                            try {
//
//                                binding.progBar.setVisibility(View.GONE);
//                                binding.llNoStore.setVisibility(View.VISIBLE);
//                                Toast.makeText(NotificationsActivity.this, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();
//
//
//                                Log.e("errorcode", t.getMessage());
//                            } catch (Exception e) {
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            binding.progBar.setVisibility(View.GONE);
//            binding.llNoStore.setVisibility(View.VISIBLE);
//
//        }
//    }
//
//    private void loadMore(int page) {
//       try {
//
//
//            Api.getService(Tags.base_url)
//                    .getnotification(page, userModel.getId() + "",lang)
//                    .enqueue(new Callback<NotificationDataModel>() {
//                        @Override
//                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
//                            notificationModelList.remove(notificationModelList.size() - 1);
//                            notification_adapter.notifyItemRemoved(notificationModelList.size() - 1);
//                            isLoading = false;
//                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
//
//                                notificationModelList.addAll(response.body().getData());
//                                // categories.addAll(response.body().getCategories());
//                                current_page2 = response.body().getCurrent_page();
//                                notification_adapter.notifyDataSetChanged();
//
//                            } else {
//                                //Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                try {
//                                    Log.e("Error_code", response.code() + "_" + response.errorBody().string());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
//                            try {
//                                notificationModelList.remove(notificationModelList.size() - 1);
//                                notification_adapter.notifyItemRemoved(notificationModelList.size() - 1);
//                                isLoading = false;
//                                // Toast.makeText(this, getString(R.string.something), Toast.LENGTH_SHORT).show();
//                                Log.e("error", t.getMessage());
//                            } catch (Exception e) {
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            notificationModelList.remove(notificationModelList.size() - 1);
//            notification_adapter.notifyItemRemoved(notificationModelList.size() - 1);
//            isLoading = false;
//        }
//    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void back() {
        if (isFromFireBase){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }




}
