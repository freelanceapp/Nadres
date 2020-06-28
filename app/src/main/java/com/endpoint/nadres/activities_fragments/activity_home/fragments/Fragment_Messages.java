package com.endpoint.nadres.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.adapters.RoomAdapter;
import com.endpoint.nadres.databinding.FragmentMessagesBinding;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.MyRoomDataModel;
import com.endpoint.nadres.models.RoomModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Messages extends Fragment {

    private HomeActivity activity;
    private FragmentMessagesBinding binding;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private UserModel userModel;
    private List<MyRoomDataModel.MyRoomModel> list;
    private RoomAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private Call<MyRoomDataModel> loadMoreCall;

    public static Fragment_Messages newInstance() {
        return new Fragment_Messages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        list = new ArrayList<>();
        adapter = new RoomAdapter(list, activity, this, userModel.getData().getId());
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        binding.recView.setItemViewCacheSize(25);
        binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recView.setDrawingCacheEnabled(true);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setOnRefreshListener(() -> getRooms());
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int current_item_pos = manager.findLastCompletelyVisibleItemPosition();
                    int total_items = adapter.getItemCount();
                    if (total_items >= 19 && current_item_pos >= total_items - 2 && !isLoading) {
                        list.add(null);
                        adapter.notifyItemInserted(list.size() - 1);
                        isLoading = true;
                        int page = current_page + 1;
                        loadMore(page);
                    }
                }
            }
        });

        getRooms();


    }

    public void getRooms() {
        if (loadMoreCall!=null){
            loadMoreCall.cancel();
        }
        Api.getService(Tags.base_url)
                .getMyRooms("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), "on", 20, 1)
                .enqueue(new Callback<MyRoomDataModel>() {
                    @Override
                    public void onResponse(Call<MyRoomDataModel> call, Response<MyRoomDataModel> response) {
                        binding.swipeRefresh.setRefreshing(false);
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getData() != null){
                                if (response.body().getData().size() > 0) {
                                    Log.e("gg","gg");
                                    Log.e("room",response.body().getData().size()+"__");

                                    list.clear();
                                    list.addAll(response.body().getData());
                                    binding.llConversation.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();

                                } else {
                                    binding.llConversation.setVisibility(View.VISIBLE);
                                }
                            }



                        }else {
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<MyRoomDataModel> call, Throwable t) {
                        try {
                            binding.swipeRefresh.setRefreshing(false);
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }else if (t.getMessage().contains("socket")){

                                }else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    private void loadMore(int page) {

        loadMoreCall = Api.getService(Tags.base_url).getMyRooms("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), "on", 20, page);

        loadMoreCall.enqueue(new Callback<MyRoomDataModel>() {
            @Override
            public void onResponse(Call<MyRoomDataModel> call, Response<MyRoomDataModel> response) {


                list.remove(list.size() - 1);
                adapter.notifyItemRemoved(list.size() - 1);
                isLoading = false;

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {

                        if (response.body().getData().size() > 0) {
                            int old_pos = list.size() - 1;
                            list.addAll(response.body().getData());
                            adapter.notifyItemRangeChanged(old_pos, list.size());
                            current_page = response.body().getCurrent_page();
                        } else {

                        }
                    }

                } else {
                    if (list.get(list.size() - 1) == null) {
                        list.remove(list.size() - 1);
                        adapter.notifyItemRemoved(list.size() - 1);
                        isLoading = false;
                    }


                    if (response.code() == 500) {
                        Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<MyRoomDataModel> call, Throwable t) {
                try {

                    if (list.get(list.size() - 1) == null) {
                        list.remove(list.size() - 1);
                        adapter.notifyItemRemoved(list.size() - 1);
                        isLoading = false;
                    }


                    if (t.getMessage() != null) {
                        Log.e("Error", t.getMessage());

                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    public void setItemRoomData(RoomModel model, int adapterPosition) {

        ChatUserModel chatUserModel = new ChatUserModel(model.getId(),model.getNames(),model.getChat_room_image(),model.getRoom_type());
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("data",chatUserModel);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK){
            getRooms();
        }
    }
}
