package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_about.AboutActivity;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_terms.TermsActivity;
import com.endpoint.nadres.databinding.FragmentMoreBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.share.Common;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

public class Fragment_More extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;


    public static Fragment_More newInstance() {

        return new Fragment_More();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
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
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(activity, AboutActivity.class);
        intent.putExtra("type",2);
        startActivity(intent);
    }

    @Override
    public void logout() {

    }

    @Override
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+activity.getPackageName());
        startActivity(intent);
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+activity.getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+activity.getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(activity, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void arLang() {

    }

    @Override
    public void enLang() {

    }




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


}
