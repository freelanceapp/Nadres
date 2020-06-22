package com.endpoint.nadres.activities_fragments.activity_sign_in.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.activities_fragments.activity_terms.TermsActivity;
import com.endpoint.nadres.adapters.Selected_Skill_Adapter;
import com.endpoint.nadres.adapters.SkillAdapter;
import com.endpoint.nadres.adapters.StageAdapter;
import com.endpoint.nadres.databinding.DialogSelectImageBinding;
import com.endpoint.nadres.databinding.FragmentSignUpAsTeacherBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.models.StageDataModel;
import com.endpoint.nadres.models.TeacherSignUpModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSignUpAsTeacher extends Fragment implements Listeners.BackListener {
    private SignInActivity activity;
    private String lang;
    private FragmentSignUpAsTeacherBinding binding;
    private Preferences preferences;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri = null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private List<StageDataModel.Stage> stageList;
    private StageAdapter stageAdapter;
    private List<String> skillList, enSkillList;
    private SkillAdapter skillAdapter;
    private String phone = "", phone_code = "";
    private List<String> selectedSkills = new ArrayList<>();
    private TeacherSignUpModel teacherSignUpModel;
    private Selected_Skill_Adapter selected_skill_adapter;


    public static FragmentSignUpAsTeacher newInstance(String phone, String phone_code) {
        FragmentSignUpAsTeacher fragment = new FragmentSignUpAsTeacher();
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putString("phone_code", phone_code);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_as_teacher, container, false);

        initView();

        return binding.getRoot();
    }


    private void initView() {
binding.setBackListener(this);
        teacherSignUpModel = new TeacherSignUpModel();
        stageList = new ArrayList<>();
        stageList.add(new StageDataModel.Stage(0, getString(R.string.choose_stage)));
        skillList = new ArrayList<>();
        skillList.add(getString(R.string.ch_skill));
        enSkillList = new ArrayList<>();
        enSkillList.add(getString(R.string.ch_skill));
        selectedSkills = new ArrayList<>();
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        preferences = Preferences.getInstance();
        binding.setModel(teacherSignUpModel);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        selected_skill_adapter = new Selected_Skill_Adapter(selectedSkills, activity, this);
        binding.recView.setAdapter(selected_skill_adapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            phone = bundle.getString("phone");
            phone_code = bundle.getString("phone_code");
        }


        ///////////////////////////////////////////////////////
        addSkills();
        stageAdapter = new StageAdapter(stageList, activity);
        binding.spinnerStage.setAdapter(stageAdapter);
        binding.spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    teacherSignUpModel.setStage_id("");

                } else {
                    teacherSignUpModel.setStage_id(String.valueOf(stageList.get(position).getId()));

                }
                binding.setModel(teacherSignUpModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        skillAdapter = new SkillAdapter(skillList, activity);
        binding.spinnerSkill.setAdapter(skillAdapter);

        binding.spinnerSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (!isSkillAdded(skillList.get(position))) {
                        selectedSkills.add(skillList.get(position));
                        selected_skill_adapter.notifyItemInserted(selectedSkills.size() - 1);
                        teacherSignUpModel.setSkills(selectedSkills);
                    }
                }
                binding.setModel(teacherSignUpModel);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////////////////////////////


        binding.btnSend.setOnClickListener(v -> {
            Intent intent = new Intent(activity, HomeActivity.class);
            startActivity(intent);
        });

        binding.flSelectImage.setOnClickListener(v -> {
            CreateImageAlertDialog();
        });

        binding.checkbox.setOnClickListener(v -> {
            if (binding.checkbox.isChecked()) {
                teacherSignUpModel.setAccept_terms(true);
                navigateToTermsActivity();

            } else {
                teacherSignUpModel.setAccept_terms(false);

            }

            binding.setModel(teacherSignUpModel);
        });

        binding.btnSend.setOnClickListener(v -> {

            if (teacherSignUpModel.isDataValid(activity)) {

                if (imgUri != null) {
                    signUpWithImage();
                } else {
                    signUpWithoutImage();
                }
            }

        });

        getStages();

    }


    private void signUpWithImage() {
        List<RequestBody> stageList = new ArrayList<>();

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody name_part = Common.getRequestBodyText(teacherSignUpModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(phone);
        RequestBody phone_code_part = Common.getRequestBodyText(phone_code);
        RequestBody email_part = Common.getRequestBodyText(teacherSignUpModel.getEmail());
        RequestBody stage_part = Common.getRequestBodyText(teacherSignUpModel.getStage_id());
        RequestBody user_type_part = Common.getRequestBodyText("teacher");
        RequestBody software_part = Common.getRequestBodyText("1");
        MultipartBody.Part image = Common.getMultiPart(activity, imgUri, "logo");

        stageList.add(stage_part);

        Api.getService(Tags.base_url)
                .signUpWithImageTeacher(name_part, email_part, phone_code_part, phone_part, user_type_part, software_part, stageList, getSkillRequestBody(), image)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(activity, response.body());
                            activity.navigateToHomeActivity();
                        } else {
                            dialog.dismiss();
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {

                                Log.e("99999999", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {

                                Log.e("6666666", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Log.e("error code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //  Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void signUpWithoutImage() {
        List<String> selectedStage = new ArrayList<>();
        selectedStage.add(teacherSignUpModel.getStage_id());

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .signUpWithoutImageTeacher(teacherSignUpModel.getName(), teacherSignUpModel.getEmail(), phone_code, phone, "teacher", "1", selectedStage, selectedSkills)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(activity, response.body());
                            activity.navigateToHomeActivity();
                        } else {

                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {


                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {


                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Log.e("error code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addSkills() {
        skillList.add(getString(R.string.listening));
        skillList.add(getString(R.string.speaking));
        skillList.add(getString(R.string.reading));
        skillList.add(getString(R.string.writing));
        skillList.add(getString(R.string.vocabulary));
        skillList.add(getString(R.string.grammer));
        skillList.add(getString(R.string.dictation));
        skillList.add(getString(R.string.knowledge_bank));


        enSkillList.add("Listening");
        enSkillList.add("Speaking");
        enSkillList.add("Reading");
        enSkillList.add("Writing");
        enSkillList.add("Vocabulary");
        enSkillList.add("Grammar");
        enSkillList.add("Dictation");
        enSkillList.add("Knowledge Bank");


    }

    private List<RequestBody> getSkillRequestBody() {
        List<RequestBody> requestBodies = new ArrayList<>();
        for (String skill : selectedSkills) {
            RequestBody requestBody = Common.getRequestBodyText(skill);
            requestBodies.add(requestBody);
        }
        return requestBodies;
    }

    private boolean isSkillAdded(String skill) {
        for (String skill2 : selectedSkills) {
            if (skill.equals(skill2)) {
                return true;
            }
        }

        return false;
    }

    private void getStages() {
        try {
            Api.getService(Tags.base_url)
                    .getStages()
                    .enqueue(new Callback<StageDataModel>() {
                        @Override
                        public void onResponse(Call<StageDataModel> call, Response<StageDataModel> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                stageList.addAll(response.body().getData());
                                if (stageList.size() > 0) {

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            stageAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StageDataModel> call, Throwable t) {
                            try {

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

    private void CreateImageAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_select_image, null, false);


        binding.btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            Check_CameraPermission();

        });

        binding.btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            CheckReadPermission();


        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        // dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void CheckReadPermission() {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, IMG_REQ1);
        } else {
            SelectImage(IMG_REQ1);
        }
    }

    private void Check_CameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, write_permission}, IMG_REQ2);
        } else {
            SelectImage(IMG_REQ2);

        }

    }

    private void SelectImage(int img_req) {

        Intent intent = new Intent();

        if (img_req == IMG_REQ1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, img_req);

        } else if (img_req == IMG_REQ2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, img_req);
            } catch (SecurityException e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG_REQ1);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == IMG_REQ2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG_REQ2);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            imgUri = getUriFromBitmap(bitmap);
            binding.flIcon.setVisibility(View.GONE);
            Picasso.get().load(imgUri).fit().into(binding.imageProfile);


        } else if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.getData();
            binding.flIcon.setVisibility(View.GONE);
            Picasso.get().load(imgUri).fit().into(binding.imageProfile);

        }
    }

    private void navigateToTermsActivity() {

        Intent intent = new Intent(activity, TermsActivity.class);
        startActivity(intent);

    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    public void setItemDelete(int pos) {
        selectedSkills.remove(pos);
        selected_skill_adapter.notifyItemRemoved(pos);

        if (selectedSkills.size() == 0) {
            binding.spinnerSkill.setSelection(0);
        }
    }

    @Override
    public void back() {
        activity.Back();
    }
}
