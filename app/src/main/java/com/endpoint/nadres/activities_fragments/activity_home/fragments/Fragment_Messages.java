package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.adapters.Room_Adapter;
import com.endpoint.nadres.databinding.FragmentMessagesBinding;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.models.UserRoomModelData;
import com.endpoint.nadres.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Messages extends Fragment {

    private HomeActivity activity;
    private FragmentMessagesBinding binding;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private UserModel userModel;
private List<UserRoomModelData.UserRoomModel> userRoomModels;
private Room_Adapter room_adapter;

    public static Fragment_Messages newInstance() {
        return new Fragment_Messages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages,container,false);
        initView();
//        if(userModel!=null){
//       // getRooms();}
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
userRoomModels=new ArrayList<>();
room_adapter=new Room_Adapter(userRoomModels,activity,this);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
binding.recView.setItemViewCacheSize(25);
binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
binding.recView.setDrawingCacheEnabled(true);
binding.recView.setAdapter(room_adapter);
binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {

        //getRooms();
    }
});
    }
//    public void getRooms() {
//        Api.getService(Tags.base_url)
//                .getRooms(userModel.getUser().getId()+"")
//                .enqueue(new Callback<UserRoomModelData>() {
//                    @Override
//                    public void onResponse(Call<UserRoomModelData> call, Response<UserRoomModelData> response) {
//                        binding.swipeRefresh.setRefreshing(false);
//                        binding.progBar.setVisibility(View.GONE);
//                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
//                        {
//                            if (response.body().getData().size()>0)
//                            {
//                                userRoomModels.clear();
//                                userRoomModels.addAll(response.body().getData());
//                                binding.llConversation.setVisibility(View.GONE);
//                                room_adapter.notifyDataSetChanged();
//
//                            }else
//                            {
//                                binding.llConversation.setVisibility(View.VISIBLE);
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<UserRoomModelData> call, Throwable t) {
//                        try {
//                            binding.swipeRefresh.setRefreshing(false);
//
//                            binding.progBar.setVisibility(View.GONE);
//                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
//                            Log.e("Error",t.getMessage());
//                        }catch (Exception e){}
//                    }
//                });
//    }





}
