package com.endpoint.nadres.activities_fragments.activity_search;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.nadres.activities_fragments.activity_teachers.TeacherActivity;
import com.endpoint.nadres.adapters.Search_Teacher_Adapter;
import com.endpoint.nadres.adapters.Skill_Teacher_Adapter;
import com.endpoint.nadres.adapters.Teacher_Adapter;
import com.endpoint.nadres.databinding.ActivitySearchBinding;
import com.endpoint.nadres.databinding.DialogSkillBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.CreateRoomModel;
import com.endpoint.nadres.models.SearchTeacherModel;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivitySearchBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private List<SearchTeacherModel.Data> teaDatalist;
    private Search_Teacher_Adapter teacher_adapter;
    private List<SearchTeacherModel.Data.SkillsFk> skillsFkList;
    private Skill_Teacher_Adapter skill_teacher_adapter;
    private String query = "all";
    private UserModel userModel;
    private Preferences preferences;
    private CreateRoomModel createRoomModel;
    private AlertDialog dialog;
    private String skill_type;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        initView();
        search();

    }


    private void initView() {
        createRoomModel = new CreateRoomModel();
        preferences = Preferences.getInstance();
        skillsFkList = new ArrayList<>();
        userModel = preferences.getUserData(this);
        if (userModel != null) {
            createRoomModel.setUser_id(userModel.getData().getId());
        }
        teaDatalist = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        skill_teacher_adapter = new Skill_Teacher_Adapter(skillsFkList, this);
        //productModelList = new ArrayList<>();
        manager = new GridLayoutManager(this, 1);
        binding.recView.setLayoutManager(manager);
        teacher_adapter = new Search_Teacher_Adapter(teaDatalist, this);
        binding.recView.setAdapter(teacher_adapter);
       /* searchAdapter = new SearchAdapter(this,productModelList,this);
        binding.recView.setAdapter(searchAdapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total = binding.recView.getAdapter().getItemCount();

                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();


                    if (total > 6 && (total - lastVisibleItem) == 2 && !isLoading) {
                        isLoading = true;
                        int page = current_page + 1;
                        productModelList.add(null);
                        searchAdapter.notifyDataSetChanged();
                        loadMore(page);
                    }
                }
            }
        });*/


        //productModelList = new ArrayList<>();

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);


        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    query = editable.toString();

                    search();
                    binding.progBar.setVisibility(View.GONE);
                    binding.recView.setVisibility(View.VISIBLE);
                } else {
                    query = "";
                    //productModelList.clear();
                    //searchAdapter.notifyDataSetChanged();


                }
            }
        });

//        binding.llType.setOnClickListener(v -> {
//
//            if (displayType==square)
//            {
//                displayType = list;
//                binding.imageType.setImageResource(R.drawable.ic_list2);
//                binding.tvType.setText(getString(R.string.list));
//            }else {
//                displayType = square;
//                binding.imageType.setImageResource(R.drawable.ic_squares);
//                binding.tvType.setText(getString(R.string.normal));
//            }
//        });

    }


    public void search() {
        teaDatalist.clear();
        teacher_adapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);

        try {
            int uid;

            if (userModel != null) {
                uid = userModel.getData().getId();
            } else {
                uid = 0;
            }
            Api.getService(Tags.base_url).
                    Search("off", "teacher", query).
                    enqueue(new Callback<SearchTeacherModel>() {
                        @Override
                        public void onResponse(Call<SearchTeacherModel> call, Response<SearchTeacherModel> response) {
                            binding.progBar.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                teaDatalist.clear();
                                teacher_adapter.notifyDataSetChanged();
                                teaDatalist.addAll(response.body().getData());
                                if (teaDatalist.size() > 0) {
                                    teacher_adapter.notifyDataSetChanged();
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }

                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchTeacherModel> call, Throwable t) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.tvNoData.setVisibility(View.VISIBLE);
                            try {
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(SearchActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }


                        }
                    });
        } catch (Exception e) {

        }


    }

    public void setItemData(SearchTeacherModel.Data model) {
        if (userModel != null) {
            List<Integer> ids = new ArrayList<>();
            ids.add(userModel.getData().getId());
            createRoomModel.setSkill_type(skill_type);
            createRoomModel.setTeacher_id(model.getId());
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
                                    Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
       // finish();
    }

    @Override
    public void back() {
//        if (isFavoriteChange) {
//            setResult(RESULT_OK);
//        }
        finish();
    }

   /* public void setItemData(ProductDataModel.Data model) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("data",model);
        startActivityForResult(intent,100);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void createSkillDialog(SearchTeacherModel.Data skillsFks) {
        skillsFkList.clear();
        skillsFkList.addAll(skillsFks.getSkills_fk());
        skill_teacher_adapter.notifyDataSetChanged();
        dialog = new AlertDialog.Builder(this)
                .create();

        DialogSkillBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_skill, null, false);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(skill_teacher_adapter);
        binding.btnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                setItemData(skillsFks);
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();

    }


    @Override
    public void onBackPressed() {
        back();
    }

    public void setskill(String skill_type) {
        this.skill_type = skill_type;
    }
}
