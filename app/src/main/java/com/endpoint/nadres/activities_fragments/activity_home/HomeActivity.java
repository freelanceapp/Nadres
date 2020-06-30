package com.endpoint.nadres.activities_fragments.activity_home;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_Main;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_Messages;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_More;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.endpoint.nadres.activities_fragments.activity_notification.NotificationsActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.databinding.ActivityHomeBinding;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FragmentManager fragmentManager;
    private Fragment_Main fragment_main;
    private Fragment_Messages fragment_messages;
    private Fragment_Profile fragment_profile;
    private Fragment_More fragment_more;
    private Preferences preferences;
    private UserModel userModel;
    private String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void listenToNewMessage(MessageModel.SingleMessageModel messageModel) {
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("data",messageModel.getSender_id()+"");
//        intent.putExtra("name",messageModel.getUser_name());
//        startActivityForResult(intent,1000);
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
        if (savedInstanceState == null) {
            // CheckPermission();

            if (userModel == null || userModel.getData().getType().equals("student")) {
                displayFragmentMain();

            } else {
                displayFragmentMessages();
            }
        }
        if (userModel != null) {
            Log.e("token",userModel.getData().getToken());
            updateToken();

        }

    }

    private void updateToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        token = task.getResult().getToken();
                        task.getResult().getId();
                        Log.e("sssssss", token);
                        Api.getService(Tags.base_url)
                                .updateFireBaseToken(token,userModel.getData().getId(), 1)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.isSuccessful()) {
                                            try {
                                                userModel.getData().setFireBaseToken(token);
                                                preferences.create_update_userdata(HomeActivity.this,userModel);
                                                Log.e("Success", "token updated");
                                            } catch (Exception e) {
                                                //  e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                Log.e("error", response.code() + "_" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        try {
                                            Log.e("Error", t.getMessage());
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        fragmentManager = getSupportFragmentManager();
        binding.toolbar.setTitle("");


        binding.imagenotifi.setOnClickListener(view -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivityForResult(intent, 1);
        });


        setUpBottomNavigation();
    }


    private void setUpBottomNavigation() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getResources().getString(R.string.home), R.drawable.ic_nav_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.chat), R.drawable.ic_nav_mail);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.profile), R.drawable.ic_nav_user);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.more), R.drawable.ic_nav_more);

        binding.ahBottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        binding.ahBottomNav.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        binding.ahBottomNav.setTitleTextSizeInSp(14, 12);
        binding.ahBottomNav.setForceTint(true);
        binding.ahBottomNav.setAccentColor(ContextCompat.getColor(this, R.color.white));
        binding.ahBottomNav.setInactiveColor(ContextCompat.getColor(this, R.color.gray0));


        if (userModel == null || userModel.getData().getType().equals("student")) {
            binding.ahBottomNav.addItem(item1);

        }
        binding.ahBottomNav.addItem(item2);
        binding.ahBottomNav.addItem(item3);


        binding.ahBottomNav.addItem(item4);

        updateBottomNavigationPosition(0);

        binding.ahBottomNav.setOnTabSelectedListener((position, wasSelected) -> {
            binding.toolbar.setVisibility(View.VISIBLE);

            switch (position) {
                case 0:
                    if (userModel == null || userModel.getData().getType().equals("student")) {
                        displayFragmentMain();

                    } else {
                        if (userModel != null) {
                            displayFragmentMessages();

                        } else {
                            Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
                        }
                    }
                    break;
                case 1:
                    if (userModel != null) {
                        if (userModel.getData().getType().equals("student")) {
                            displayFragmentMessages();


                        } else {
                            displayFragmentProfile();
                            binding.toolbar.setVisibility(View.GONE);
                        }
                    } else {
                        Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
                    }

                    break;
                case 2:
                    if (userModel != null) {
                        if (userModel.getData().getType().equals("student")) {
                            displayFragmentProfile();
                            binding.toolbar.setVisibility(View.GONE);


                        } else {
                            displayFragmentMore();
                        }
                    } else {
                        Common.CreateDialogAlert(this, getResources().getString(R.string.please_sign_in_or_sign_up));
                    }
                    break;
                case 3:
                    displayFragmentMore();
                    break;

            }
            return false;
        });


    }

    private void updateBottomNavigationPosition(int pos) {

        binding.ahBottomNav.setCurrentItem(pos, false);
    }

    private void displayFragmentMain() {
        try {
            if (fragment_main == null) {
                fragment_main = Fragment_Main.newInstance();
            }

            if (fragment_messages != null && fragment_messages.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_messages).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_main.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_main).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_main, "fragment_main").addToBackStack("fragment_main").commit();

            }
            updateBottomNavigationPosition(0);
        } catch (Exception e) {
        }
    }

    private void displayFragmentMessages() {
        try {
            if (fragment_messages == null) {
                fragment_messages = Fragment_Messages.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_messages.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_messages).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_messages, "fragment_messages").addToBackStack("fragment_messages").commit();

            }
            updateBottomNavigationPosition(1);
        } catch (Exception e) {
        }
    }

    private void displayFragmentProfile() {
        try {
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_messages != null && fragment_messages.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_messages).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_notifications").addToBackStack("fragment_notifications").commit();

            }
            updateBottomNavigationPosition(2);
        } catch (Exception e) {
        }
    }

    private void displayFragmentMore() {
        try {
            if (fragment_more == null) {
                fragment_more = Fragment_More.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_messages != null && fragment_messages.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_messages).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_more.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_more).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_more, "fragment_more").addToBackStack("fragment_more").commit();

            }
            updateBottomNavigationPosition(3);
        } catch (Exception e) {
        }
    }


    private void NavigateToSignInActivity() {
        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public void logout() {
        if (userModel == null) {

            Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));

        } else {
            Logout();
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        if (userModel != null) {
            if (userModel.getData().getType().equals("student")) {
                if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
                    if (userModel == null) {
                        NavigateToSignInActivity();
                    } else {
                        finish();
                    }
                } else {
                    displayFragmentMain();


                }
            } else {

                if (fragment_messages != null && fragment_messages.isAdded() && fragment_messages.isVisible()) {
                    if (userModel == null) {
                        NavigateToSignInActivity();
                    } else {
                        finish();
                    }
                } else {
                    displayFragmentMessages();


                }
            }

        } else {
            if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
                if (userModel == null) {
                    NavigateToSignInActivity();
                } else {
                    finish();
                }
            } else {
                displayFragmentMain();


            }
        }
    }


    public void Logout() {
        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        userModel = preferences.getUserData(this);
        dialog.show();
        Api.getService(Tags.base_url)
                .Logout("Bearer  " + userModel.getData().getToken(),userModel.getData().getFireBaseToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            new Handler()
                                    .postDelayed(() -> {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (manager!=null){
                                            manager.cancel(Tags.not_tag,Tags.not_id);

                                        }
                                    },1);
                            preferences.create_update_userdata(HomeActivity.this, null);
                            preferences.create_update_session(HomeActivity.this, Tags.session_logout);
                            preferences.clear(HomeActivity.this);
                            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

//    private void CheckPermission() {
//        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, gps_req);
//        } else {
//            initGoogleApiClient();
//
//        }
//    }
//
//    private void initGoogleApiClient() {
//        googleApiClient = new GoogleApiClient.
//                Builder(this).
//                addOnConnectionFailedListener(this).
//                addConnectionCallbacks(this).
//                addApi(LocationServices.API).build();
//        googleApiClient.connect();
//    }
//
//
//
//
//
//    @Override
//    public void onLocationChanged(Location location) {
//        this.location = location;
//        lat = location.getLatitude()+"";
//        lng = location.getLongitude()+"";
//
//        //AddMarker(lat, lang);
//
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        }
//        if (locationRequest != null) ;
//        {
//            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
//        }
//        if(dialog!=null){
//        dialog.dismiss();}
//    }
//
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        intLocationRequest();
//
//    }
//
//    private void intLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setFastestInterval(1000 * 60 * 2);
//        locationRequest.setInterval(1000 * 60 * 2);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
//                Status status = locationSettingsResult.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        startLocationUpdate();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            status.startResolutionForResult(HomeActivity.this, 1255);
//                        } catch (Exception e) {
//
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.e("not available", "not available");
//                        break;
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private void startLocationUpdate() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                onLocationChanged(locationResult.getLastLocation());
//            }
//        };
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//    }
//    public void showdetials(int id) {
//        Intent intent=new Intent(HomeActivity.this, AdsDetialsActivity.class);
//        intent.putExtra("search",id);
//        startActivity(intent);
//    }
//
//    public void gotomessage(int receiver_id, String receiver_name, String receiver_mobile) {
//        Intent intent=new Intent(HomeActivity.this, ChatActivity.class);
//        intent.putExtra("data",receiver_id+"");
//        intent.putExtra("name",receiver_name);
//        intent.putExtra("phone",receiver_mobile);
//
//        startActivity(intent);
//    }
//
//    public void deletenotification(int id) {
//        if(fragment_profile !=null&& fragment_profile.isAdded()){
//            fragment_profile.deletenotification(id);
//        }
//    }
//    public void NavigateToSignInActivity(boolean isSignIn) {
////Log.e("data",isSignIn+"");
//        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
//        intent.putExtra("sign_up", isSignIn);
//        startActivity(intent);
//        finish();
//
//    }
}
