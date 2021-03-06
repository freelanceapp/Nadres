package com.endpoint.nadres.activities_fragments.activity_teachers;

import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.nadres.adapters.Teacher_Adapter;
import com.endpoint.nadres.databinding.ActivityTeachersBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.CreateRoomModel;
import com.endpoint.nadres.models.SingleRoomModel;
import com.endpoint.nadres.models.TeacherModel;
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

public class TeacherActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityTeachersBinding binding;
    private LinearLayoutManager manager;
    private List<TeacherModel.Data> teaDatalist;
    private Teacher_Adapter teacher_adapter;
    private Preferences preferences;
    private UserModel userModel;
    private boolean isLoading = false;
    private int current_page2 = 1;
    private String lang;
    private String skill;
    private CreateRoomModel createRoomModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_teachers);
        getDataFromIntent();
        initView();
        getTeacher();
//        if(userModel!=null){
//        getnotification();}

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            skill = intent.getStringExtra("skill");

        }
    }

    private void initView() {
        createRoomModel = new CreateRoomModel();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        if (userModel != null) {
            createRoomModel.setUser_id(userModel.getData().getId());
        }
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.input), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        teaDatalist = new ArrayList<>();
        teacher_adapter = new Teacher_Adapter(teaDatalist, this);
        binding.recView.setAdapter(teacher_adapter);
        binding.setBackListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.tvTitle.setText(getResources().getString(R.string.teachers));
        //adddata();
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

//    private void adddata() {
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notificationModelList.add(new NotificationDataModel.NotificationModel());
//        notification_adapter.notifyDataSetChanged();
//
//    }

    private void getTeacher() {
        teaDatalist.clear();
        teacher_adapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        try {


            Api.getService(Tags.base_url)
                    .getTeacher(skill)
                    .enqueue(new Callback<TeacherModel>() {
                        @Override
                        public void onResponse(Call<TeacherModel> call, Response<TeacherModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                teaDatalist.clear();
                                teaDatalist.addAll(response.body().getData());
                                if (response.body().getData().size() > 0) {
                                    // rec_sent.setVisibility(View.VISIBLE);
                                    //  Log.e("data",response.body().getData().get(0).getAr_title());

                                    binding.llNoStore.setVisibility(View.GONE);
                                    teacher_adapter.notifyDataSetChanged();
                                    //   total_page = response.body().getMeta().getLast_page();

                                } else {
                                    teacher_adapter.notifyDataSetChanged();

                                    binding.llNoStore.setVisibility(View.VISIBLE);

                                }
                            } else {
                                teacher_adapter.notifyDataSetChanged();

                                binding.llNoStore.setVisibility(View.VISIBLE);

                                //Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TeacherModel> call, Throwable t) {
                            try {

                                binding.progBar.setVisibility(View.GONE);
                                binding.llNoStore.setVisibility(View.VISIBLE);
                                // Toast.makeText(TeacherActivity.this, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();


                                Log.e("errorcode", t.getMessage());
                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            binding.progBar.setVisibility(View.GONE);
            binding.llNoStore.setVisibility(View.VISIBLE);

        }
    }

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

    public void setItemData(TeacherModel.Data model) {
        if (userModel != null) {
            List<Integer> ids = new ArrayList<>();
            ids.add(userModel.getData().getId());
            createRoomModel.setSkill_type(skill);
            createRoomModel.setTeacher_id(model.getUser_id());
            createRoomModel.setStudent_ids(ids);
            CreateChatRoom();

        } else {
            Common.CreateDialogAlert(this, getResources().getString(R.string.please_sign_in_or_sign_up));
        }
    }

    private void CreateChatRoom() {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .CreateChatRoom(createRoomModel, "Bearer  " + userModel.getData().getToken() + "")
                    .enqueue(new Callback<SingleRoomModel>() {
                        @Override
                        public void onResponse(Call<SingleRoomModel> call, Response<SingleRoomModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                chat(response.body());
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(TeacherActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(TeacherActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleRoomModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("errorssss", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        // Toast.makeText(TeacherActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(TeacherActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();
            Log.e("errorssss1", e.getMessage());

        }

    }

    private void chat(SingleRoomModel model) {
        ChatUserModel chatUserModel = new ChatUserModel(model.getRoomModel().getId(), model.getRoomModel().getNames(), model.getRoomModel().getChat_room_image(), model.getRoomModel().getRoom_type(), model.getRoomModel().getRoom_code_link());
        chatUserModel.setRoomStatus(model.getRoomModel().getStatus());

        Intent intent = new Intent(TeacherActivity.this, ChatActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }

    @Override
    public void back() {
        finish();
    }


}
