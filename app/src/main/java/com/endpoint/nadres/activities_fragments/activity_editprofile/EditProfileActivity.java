package com.endpoint.nadres.activities_fragments.activity_editprofile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.adapters.ClassAdapter;
import com.endpoint.nadres.adapters.Notification_Adapter;
import com.endpoint.nadres.adapters.StageAdapter;
import com.endpoint.nadres.databinding.ActivityEditprofileBinding;
import com.endpoint.nadres.databinding.ActivityNotificationsBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.EditProfileStudentModel;
import com.endpoint.nadres.models.NotificationDataModel;
import com.endpoint.nadres.models.StageDataModel;
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

public class EditProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityEditprofileBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private List<StageDataModel.Stage> stageList;
    private List<StageDataModel.Stage.ClassesFk> classesFkList;
    private StageAdapter stageAdapter;
    private ClassAdapter classAdapter;
    private EditProfileStudentModel editProfileStudentModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_editprofile);
        initView();

    }

    private void initView() {
        editProfileStudentModel = new EditProfileStudentModel();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        stageList = new ArrayList<>();
        classesFkList = new ArrayList<>();
        stageAdapter = new StageAdapter(stageList, this);
        classAdapter = new ClassAdapter(classesFkList, this);
        binding.spinnerStage.setAdapter(stageAdapter);
        binding.spinnerClass.setAdapter(classAdapter);
        binding.setModel(editProfileStudentModel);
        binding.setUsermodel(userModel);

        binding.spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editProfileStudentModel.setStage("");
                } else {
                    editProfileStudentModel.setStage(stageList.get(position).getId() + "");
                    classesFkList.clear();
                    classesFkList.add(new StageDataModel.Stage.ClassesFk(getResources().getString(R.string.choose_classe)));
                    classesFkList.addAll(stageList.get(position).getClasses_fk());
                    classAdapter.notifyDataSetChanged();
                }

                binding.setModel(editProfileStudentModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editProfileStudentModel.setClsses("");
                } else {
                    editProfileStudentModel.setClsses(classesFkList.get(position).getId() + "");


                }

                binding.setModel(editProfileStudentModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getStages();
    }

    private void UpdateUI() {
        for (int i = 0; i < stageList.size(); i++) {
            if (userModel.getData().getStage_fk().get(0).getStage_class_name().getId() == stageList.get(i).getId()) {
                if (userModel.getData().getType().equals("student")) {
                    classesFkList.clear();
                    classesFkList.add(new StageDataModel.Stage.ClassesFk(getResources().getString(R.string.choose_classe)));
                    classesFkList.addAll(stageList.get(i).getClasses_fk());
                    classAdapter.notifyDataSetChanged();
                }
                binding.spinnerStage.setSelection(i);
                break;
            }
        }
        if (userModel.getData().getType().equals("student")) {
            editProfileStudentModel.setEmail(userModel.getData().getEmail());
            editProfileStudentModel.setClsses(userModel.getData().getClass_fk().get(0).getClass_id() + "");
            editProfileStudentModel.setStage(userModel.getData().getStage_fk().get(0).getStage_id() + "");
            if (userModel.getData().getImage() != null) {
                editProfileStudentModel.setImage_url(Tags.IMAGE_URL + userModel.getData().getImage());
            }
            for (int i = 0; i < classesFkList.size(); i++) {
                Log.e("lllll", classesFkList.get(i).getId() + "" + userModel.getData().getClass_fk().get(0).getStage_class_name().getId());

                if (userModel.getData().getClass_fk().get(0).getStage_class_name().getId() == classesFkList.get(i)
                        .getId()) {
                    binding.spinnerClass.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getStages() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getStages()
                .enqueue(new Callback<StageDataModel>() {
                    @Override
                    public void onResponse(Call<StageDataModel> call, Response<StageDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                            stageList.clear();
                            stageList.add(new StageDataModel.Stage(getResources().getString(R.string.choose_stage)));
                            stageList.addAll(response.body().getData());
                            stageAdapter.notifyDataSetChanged();
                            if (userModel.getData().getType().equals("student")) {
                                UpdateUI();

                            }
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                //Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                //  Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StageDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    // Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
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
