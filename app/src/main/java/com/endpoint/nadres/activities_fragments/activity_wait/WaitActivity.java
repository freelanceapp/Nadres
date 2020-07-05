package com.endpoint.nadres.activities_fragments.activity_wait;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.nadres.databinding.ActivityWaitBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.RoomModel;
import com.endpoint.nadres.models.SingleRoomModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityWaitBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private String code="";

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wait);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.getData()!=null){
            List<String> pathSegments = intent.getData().getPathSegments();
            code = pathSegments.get(pathSegments.size()-1);

        }
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);

        if (userModel!=null){
            binding.setName(userModel.getData().getName());
            sendJoinChat();
        }else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }



    }

    private void sendJoinChat() {

        Api.getService(Tags.base_url)
                .joinRoom(code,userModel.getData().getId())
                .enqueue(new Callback<SingleRoomModel>() {
                    @Override
                    public void onResponse(Call<SingleRoomModel> call, Response<SingleRoomModel> response) {

                        if (response.isSuccessful()) {
                            try {
                                if (response.body()!=null&&response.body().getRoomModel()!=null){

                                    if (response.body().getUser_status().equals("in_chat")){
                                        RoomModel model = response.body().getRoomModel();
                                        ChatUserModel chatUserModel = new ChatUserModel(model.getId(),model.getNames(),model.getChat_room_image(),model.getRoom_type(),model.getRoom_code_link());
                                        Intent intent = new Intent(WaitActivity.this, ChatActivity.class);
                                        intent.putExtra("data",chatUserModel);
                                        startActivity(intent);
                                    }else if (response.body().getUser_status().equals("close")){
                                        Toast.makeText(WaitActivity.this, R.string.meeting_ended, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(WaitActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }


                                }


                            } catch (Exception e) {
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
                    public void onFailure(Call<SingleRoomModel> call, Throwable t) {
                        try {
                            Log.e("Error", t.getMessage());
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
