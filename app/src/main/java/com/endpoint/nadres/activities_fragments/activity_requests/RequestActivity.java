package com.endpoint.nadres.activities_fragments.activity_requests;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.endpoint.nadres.adapters.Request_Adapter;
import com.endpoint.nadres.databinding.ActivityRequestBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.RequestDataModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.share.Common;
import com.endpoint.nadres.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity  implements Listeners.BackListener {
    private ActivityRequestBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private boolean isDataChange = false;
    private List<RequestDataModel.RequestModel> requestModelList;
    private Request_Adapter adapter;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_request);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(Tags.not_tag, Tags.not_id);

        }
        initView();
    }



    private void initView() {

        requestModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.input), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Request_Adapter(requestModelList,this);
        binding.recView.setAdapter(adapter);

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        getRequests();




    }

    private void getRequests() {
        Api.getService(Tags.base_url)
                .getTeacherRequests(userModel.getData().getId())
                .enqueue(new Callback<RequestDataModel>() {
                    @Override
                    public void onResponse(Call<RequestDataModel> call, Response<RequestDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getData() != null) {

                                requestModelList.clear();
                                requestModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();

                                if (requestModelList.size()>0){
                                    binding.tvNoRequests.setVisibility(View.GONE);
                                }else {
                                    binding.tvNoRequests.setVisibility(View.VISIBLE);

                                }


                            }


                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(RequestActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RequestActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Log.e("error code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<RequestDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(RequestActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().contains("socket")) {

                                } else {
                                    Toast.makeText(RequestActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    public void accept_refuse(RequestDataModel.RequestModel model, String action, int pos) {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .requestAction("Bearer "+userModel.getData().getToken(),model.getId(),model.getRoom_id(),userModel.getData().getId(),model.getFrom_user(),action)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            requestModelList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            if (action.equals("accept")){
                                isDataChange = true;
                            }

                            if (requestModelList.size()>0){
                                binding.tvNoRequests.setVisibility(View.GONE);
                            }else {
                                binding.tvNoRequests.setVisibility(View.VISIBLE);

                            }
                        } else {
                            dialog.dismiss();
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
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToAction(ChatUserModel chatUserModel){
        getRequests();
    }

    @Override
    public void back() {
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if (isDataChange){
            setResult(RESULT_OK);
        }

        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }
}

