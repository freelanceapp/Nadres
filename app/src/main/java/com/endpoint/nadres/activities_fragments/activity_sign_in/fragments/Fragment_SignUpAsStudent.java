package com.endpoint.nadres.activities_fragments.activity_sign_in.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
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

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.activities_fragments.activity_terms.TermsActivity;
import com.endpoint.nadres.adapters.ClassAdapter;
import com.endpoint.nadres.adapters.StageAdapter;
import com.endpoint.nadres.databinding.DialogSelectImageBinding;
import com.endpoint.nadres.databinding.FragmentSignUpAsStudentBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.models.SignUpStudentModel;
import com.endpoint.nadres.models.StageDataModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
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

public class Fragment_SignUpAsStudent extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener, Listeners.SignUpListener, Listeners.BackListener {
    private SignInActivity activity;
    private static final String TAG = "DATA";
    private static final String TAG2 = "Type";
    private String current_language;
    private FragmentSignUpAsStudentBinding binding;
    private CountryPicker countryPicker;
    private Preferences preferences;
    private SignUpStudentModel signUpModel;
    private String phone;
    private String phone_code;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri1 = null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private List<StageDataModel.Stage> stageList;
    private List<StageDataModel.Stage.ClassesFk> classesFkList;
    private StageAdapter stageAdapter;
    private ClassAdapter classAdapter;

    public static Fragment_SignUpAsStudent newInstance(String phone, String phone_code) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, phone);
        bundle.putString(TAG2, phone_code);
        Fragment_SignUpAsStudent fragment_signUpAsStudent = new Fragment_SignUpAsStudent();
        fragment_signUpAsStudent.setArguments(bundle);
        return fragment_signUpAsStudent;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_as_student, container, false);

        initView();

        return binding.getRoot();
    }


    private void initView() {
        binding.setBackListener(this);
        stageList = new ArrayList<>();
        classesFkList = new ArrayList<>();
        Bundle bundle = getArguments();

        if (bundle != null) {
            phone = bundle.getString(TAG);
            phone_code = bundle.getString(TAG2);
        }


        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        binding.setSignUpListener(this);
        preferences = Preferences.getInstance();
        signUpModel = new SignUpStudentModel();
        signUpModel.setPhone_code(phone_code);
        signUpModel.setPhone(phone);
        binding.setModel(signUpModel);
        binding.setSignUpListener(this);
        binding.setShowDialogListener(this);
        stageAdapter = new StageAdapter(stageList, activity);
        classAdapter = new ClassAdapter(classesFkList, activity);
        binding.spinnerStage.setAdapter(stageAdapter);
        binding.spinnerClass.setAdapter(classAdapter);
        createCountryDialog();


        binding.checkbox.setOnClickListener(v -> {
            if (binding.checkbox.isChecked()) {
                signUpModel.setTermsAccepted(true);
                navigateToTermsActivity();
            } else {
                signUpModel.setTermsAccepted(false);
            }

            binding.setModel(signUpModel);

        });

        binding.flimagw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog();
            }
        });


        binding.spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    signUpModel.setStage("");
                } else {
                    signUpModel.setStage(stageList.get(position).getId() + "");
                    classesFkList.clear();
                    classesFkList.add(new StageDataModel.Stage.ClassesFk(activity.getResources().getString(R.string.choose_classe)));
                    classesFkList.addAll(stageList.get(position).getClasses_fk());
                    classAdapter.notifyDataSetChanged();
                }

                binding.setModel(signUpModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    signUpModel.setClsses("");
                } else {
                    signUpModel.setClsses(classesFkList.get(position).getId() + "");


                }

                binding.setModel(signUpModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getStages();
    }

    private void getStages() {
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getStages()
                .enqueue(new Callback<StageDataModel>() {
                    @Override
                    public void onResponse(Call<StageDataModel> call, Response<StageDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                            stageList.clear();
                            stageList.add(new StageDataModel.Stage(activity.getResources().getString(R.string.choose_stage)));
                            stageList.addAll(response.body().getData());
                            stageAdapter.notifyDataSetChanged();

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
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void navigateToTermsActivity() {

        Intent intent = new Intent(activity, TermsActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
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
            binding.llimage.setVisibility(View.GONE);
            Picasso.get().load(imgUri1).fit().into(binding.imageFill);


        } else if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri1 = data.getData();
            binding.llimage.setVisibility(View.GONE);
            Picasso.get().load(imgUri1).fit().into(binding.imageFill);

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
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }


    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(false)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        if (countryPicker.getCountryFromSIM() != null) {
            updatePhoneCode(countryPicker.getCountryFromSIM());
        } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
            updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
        } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
            updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
        } else {
            String code = "+966";
            //  binding.tvCode.setText(code);
            signUpModel.setPhone_code(code.replace("+", "00"));

        }

    }

    @Override
    public void showDialog() {

        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);

    }

    private void updatePhoneCode(Country country) {
        //  binding.tvCode.setText(country.getDialCode());
        //signUpModel.setPhone_code(country.getDialCode().replace("+","00"));

    }


    @Override
    public void checkDataValid() {


        if (signUpModel.isDataValid(activity)) {
            Common.CloseKeyBoard(activity, binding.edtEmail);
            signUp();
        }

    }

    private void signUp() {


        if (imgUri1 == null) {
            signUpWithoutImage();
        } else {
            signUpWithImage();
        }
    }

    private void signUpWithoutImage() {
        List<String> stages = new ArrayList<>();
        List<String> classes = new ArrayList<>();
        stages.add(signUpModel.getStage());
        classes.add(signUpModel.getClsses());
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .signUpWithoutImage(signUpModel.getName(), signUpModel.getEmail(), signUpModel.getPhone_code(), signUpModel.getPhone(), "student", "1", stages, classes)
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

                                Log.e("99999999", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {

                                Log.e("6666666", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
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

    private void signUpWithImage() {
        List<RequestBody> stages = new ArrayList<>();
        List<RequestBody> classes = new ArrayList<>();

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody name_part = Common.getRequestBodyText(signUpModel.getName());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpModel.getPhone_code());
        RequestBody phone_part = Common.getRequestBodyText(signUpModel.getPhone());
        RequestBody email_part = Common.getRequestBodyText(signUpModel.getEmail());
        RequestBody stage_part = Common.getRequestBodyText(signUpModel.getStage());
        RequestBody user_type_part = Common.getRequestBodyText("student");
        RequestBody software_part = Common.getRequestBodyText("1");
        RequestBody claaess_part = Common.getRequestBodyText(signUpModel.getClsses());
        MultipartBody.Part image = Common.getMultiPart(activity, imgUri1, "logo");
        stages.add(stage_part);
        classes.add(claaess_part);

        Api.getService(Tags.base_url)
                .signUpWithImage(name_part, email_part, phone_code_part, phone_part, user_type_part, software_part, stages, classes, image)
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

                                Log.e("99999999", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 406) {

                                Log.e("6666666", response.message() + "");

                                Toast.makeText(activity, response.errorBody() + "", Toast.LENGTH_SHORT).show();
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
        activity.Back();
    }
}
