package com.endpoint.nadres.activities_fragments.activity_editprofile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.endpoint.nadres.R;
import com.endpoint.nadres.adapters.ClassAdapter;
import com.endpoint.nadres.adapters.Notification_Adapter;
import com.endpoint.nadres.adapters.StageAdapter;
import com.endpoint.nadres.databinding.ActivityEditprofileBinding;
import com.endpoint.nadres.databinding.ActivityNotificationsBinding;
import com.endpoint.nadres.databinding.DialogSelectImageBinding;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri1 = null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;

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
                    //binding.spinnerClass.setSelection(0);
                    if(stageList.get(position).getId()!=userModel.getData().getStage_fk().get(0).getStage_class_name().getId()){
                        binding.spinnerClass.setSelection(0);
                    }
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
        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog();
            }
        });
binding.btnSend.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(editProfileStudentModel.isDataValid(EditProfileActivity.this)){
            editprofile();
        }
    }
});
        getStages();
    }

    private void CreateImageAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_select_image, null, false);


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
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, IMG_REQ1);
        } else {
            SelectImage(IMG_REQ1);
        }
    }

    private void Check_CameraPermission() {
        if (ContextCompat.checkSelfPermission(this, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, IMG_REQ2);
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
                Toast.makeText(EditProfileActivity.this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

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
                //   Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == IMG_REQ2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG_REQ2);
            } else {
                // Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            imgUri1 = getUriFromBitmap(bitmap);
            // binding.llimage.setVisibility(View.GONE);

            Picasso.get().load(imgUri1).fit().into(binding.image);


        } else if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri1 = data.getData();
            //  binding.llimage.setVisibility(View.GONE);
            Picasso.get().load(imgUri1).fit().into(binding.image);

        }
    }

//    private void navigateToTermsActivity() {
//
//        Intent intent = new Intent(activity, TermsActivity.class);
//        startActivity(intent);
//
//    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(EditProfileActivity.this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(EditProfileActivity.this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
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
            editProfileStudentModel.setName(userModel.getData().getName());
            editProfileStudentModel.setClsses(userModel.getData().getClass_fk().get(0).getStage_class_name().getId() + "");
            editProfileStudentModel.setStage(userModel.getData().getStage_fk().get(0).getStage_class_name().getId() + "");

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

    private void editprofile() {


        if (imgUri1 == null) {
            editprofilestudentWithoutImage();
        } else {
            editprofilestudentpWithImage();
        }
    }

    private void editprofilestudentWithoutImage() {
        List<String> stages = new ArrayList<>();
        List<String> classes = new ArrayList<>();
        stages.add(editProfileStudentModel.getStage());
        classes.add(editProfileStudentModel.getClsses());
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .EditstudentprofileWithoutImage(editProfileStudentModel.getName(), editProfileStudentModel.getEmail(), "student", "1", stages, classes)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(EditProfileActivity.this, response.body());
                        } else {
                            try {
                                Log.e("errrrrr",response.code()+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {

                                Log.e("99999999", response.message() + "");

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {

                                Log.e("6666666", response.message() + "");

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(activity,getString(R.string.failed), Toast.LENGTH_SHORT).show();
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

    private void editprofilestudentpWithImage() {
        List<RequestBody> stages = new ArrayList<>();
        List<RequestBody> classes = new ArrayList<>();

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody name_part = Common.getRequestBodyText(editProfileStudentModel.getName());
//        RequestBody phone_code_part = Common.getRequestBodyText(editProfileStudentModel.getPhone_code());
//        RequestBody phone_part = Common.getRequestBodyText(editProfileStudentModel.getPhone());
        RequestBody email_part = Common.getRequestBodyText(editProfileStudentModel.getEmail());
        RequestBody stage_part = Common.getRequestBodyText(editProfileStudentModel.getStage());
        RequestBody user_type_part = Common.getRequestBodyText("student");
        RequestBody software_part = Common.getRequestBodyText("1");
        RequestBody claaess_part = Common.getRequestBodyText(editProfileStudentModel.getClsses());
        MultipartBody.Part image = Common.getMultiPart(this, imgUri1, "logo");
        stages.add(stage_part);
        classes.add(claaess_part);

        Api.getService(Tags.base_url)
                .EditstudentprofileWithImage(name_part, email_part, user_type_part, software_part, stages, classes, image)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(EditProfileActivity.this, response.body());
                        } else {
                            try {
                                Log.e("errrrrr",response.code()+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {

                                Log.e("99999999", response.message() + "");

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {

                                Log.e("6666666", response.message() + "");

                                Toast.makeText(EditProfileActivity.this, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(activity,getString(R.string.failed), Toast.LENGTH_SHORT).show();
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

    @Override
    public void back() {
        finish();
    }


}
