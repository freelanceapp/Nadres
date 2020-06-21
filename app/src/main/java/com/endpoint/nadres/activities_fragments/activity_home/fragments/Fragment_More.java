package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_terms.TermsActivity;
import com.endpoint.nadres.databinding.FragmentMoreBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.SettingModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_More extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private SettingModel settingmodel;


    public static Fragment_More newInstance() {

        return new Fragment_More();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        getAppData();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        userModel = preferences.getUserData(activity);
        binding.setLang(lang);
        binding.setAction(this);
       /* binding.llterms.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TermsActivity.class);
            startActivity(intent);
        });

        binding.llabout.setOnClickListener(view -> {
            Intent intent = new Intent(activity, AboutActivity.class);
            startActivity(intent);
        });


        binding.llShare.setOnClickListener(view -> {
            String app_url = "https://play.google.com/store/apps/details?id=" + activity.getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "تطبيق عالم أعمالنا");
            intent.putExtra(Intent.EXTRA_TEXT, app_url);
            startActivity(intent);
        });

        binding.llRate.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));

            }
        });

        binding.llLogout.setOnClickListener(view -> {
            if (userModel != null) {

                activity.logout();
            } else {
                // Common.CreateNoSignAlertDialog(activity);
            }
        });
*/

    }


    @Override
    public void contactUs() {

    }

    @Override
    public void terms() {
        Intent intent = new Intent(activity, TermsActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(activity, TermsActivity.class);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    @Override
    public void logout() {
        activity.logout();
    }

    @Override
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        startActivity(intent);
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(activity, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void arLang() {
        if (lang.equals("en")) {
            activity.refreshActivity("ar");
        }
    }

    @Override
    public void enLang() {
        if (lang.equals("ar")) {
            activity.refreshActivity("en");
        }
    }

    @Override
    public void whatsapp() {
        if (settingmodel != null && settingmodel.getData().getWhatsapp() != null) {
            ViewSocial("https://api.whatsapp.com/send?phone=" + settingmodel.getData().getWhatsapp());
        } else {

            //  CreateAlertDialog();
        }
    }

    @Override
    public void twitter() {
        if (settingmodel != null && settingmodel.getData().getTwitter() != null) {
            ViewSocial(settingmodel.getData().getTwitter());
        } else {
            //  CreateAlertDialog();
        }
    }

    @Override
    public void instagram() {
        if (settingmodel != null && settingmodel.getData().getInstagram() != null) {
            ViewSocial(settingmodel.getData().getInstagram());
        } else {
            //  CreateAlertDialog();

        }
    }

    //    private void CreateAlertDialog() {
//
//        final AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setCancelable(true)
//                .create();
//
//        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_sign, null);
//        Button doneBtn = view.findViewById(R.id.doneBtn);
//        TextView tv_msg = view.findViewById(R.id.tv_msg);
//        TextView tv_title = view.findViewById(R.id.tv_title);
//        tv_title.setText(getString(R.string.app_name));
//        tv_msg.setText(R.string.no_media_available);
//
//        doneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
//        dialog.setView(view);
//        dialog.show();
//    }
    //    private void CreateLangDialog() {
//        final AlertDialog dialog = new AlertDialog.Builder(activity)
//                .create();
//
//        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_language, null, false);
//        String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
//        if (lang.equals("ar")) {
//            binding.rbAr.setChecked(true);
//        } else {
//            binding.rbEn.setChecked(true);
//
//        }
//        binding.btnCancel.setOnClickListener((v) ->
//                dialog.dismiss()
//
//        );
//        binding.rbAr.setOnClickListener(view -> {
//            dialog.dismiss();
//            activity.refreshActivity("ar");
//
//
//        });
//        binding.rbEn.setOnClickListener(view -> {
//            dialog.dismiss();
//            activity.refreshActivity("en");
//
//
//        });
//        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setView(binding.getRoot());
//        dialog.show();
//    }
    private void getAppData() {

        Api.getService(Tags.base_url)
                .getSettings()
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            update(response.body());
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                //  Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                //   Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {
                            // binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //  Toast.makeText(AboutAppActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    //   Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void update(SettingModel body) {
        this.settingmodel = body;
    }

    private void ViewSocial(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);

    }

}
