package com.endpoint.nadres.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_Messages;
import com.endpoint.nadres.databinding.UserSearchRowBinding;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.models.UserRoomModelData;
import com.endpoint.nadres.preferences.Preferences;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Room_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserRoomModelData.UserRoomModel> userRoomModels;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private HomeActivity activity;
private int i=-1;
private Fragment_Messages fragment_messages;
private Fragment fragment;
private UserModel userModel;
private Preferences preferences;
    public Room_Adapter(List<UserRoomModelData.UserRoomModel> userRoomModels, Context context, Fragment fragment) {
        this.userRoomModels = userRoomModels;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        this.activity = (HomeActivity) context;
        this.fragment=fragment;
        preferences=Preferences.getInstance();
        userModel=preferences.getUserData(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        UserSearchRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_search_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        EventHolder eventHolder = (EventHolder) holder;
eventHolder.binding.setUserroommodel(userRoomModels.get(position));
eventHolder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        i=position;
        notifyDataSetChanged();
       // activity.gotomessage(userRoomModels.get(eventHolder.getLayoutPosition()).getReceiver_id());
    }
});

}




    @Override
    public int getItemCount() {
        return userRoomModels.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public UserSearchRowBinding binding;

        public EventHolder(@NonNull UserSearchRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
