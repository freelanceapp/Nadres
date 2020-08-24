package com.endpoint.nadres.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_home.HomeActivity;
import com.endpoint.nadres.activities_fragments.activity_home.fragments.Fragment_Messages;
import com.endpoint.nadres.databinding.LoadMoreBinding;
import com.endpoint.nadres.databinding.RoomRowBinding;
import com.endpoint.nadres.models.MyRoomDataModel;
import com.endpoint.nadres.models.RoomModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.share.Time_Ago;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;

    private List<MyRoomDataModel.MyRoomModel> list;
    private Context context;
    private LayoutInflater inflater;
    private HomeActivity activity;
    private Fragment_Messages fragment;
    private int current_user_id;


    public RoomAdapter(List<MyRoomDataModel.MyRoomModel> list, Context context, Fragment_Messages fragment, int current_user_id) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (HomeActivity) context;
        this.fragment = fragment;
        this.current_user_id = current_user_id;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == DATA) {
            RoomRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.room_row, parent, false);
            return new MyHolder(binding);
        } else {
            LoadMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more, parent, false);
            return new LoadHolder(binding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            RoomModel model = list.get(position).getRoom();
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(model);
            if (model.getLast_msg() != null) {
                myHolder.binding.setTime(Time_Ago.getTimeAgo(model.getLast_msg().getDate() * 1000, context));

            }
            if (list.get(position).getRoom().getStatus().equals("close") && Preferences.getInstance().getUserData(context).getData().getType().equals("student")) {
                ((MyHolder) holder).binding.btrate.setVisibility(View.VISIBLE);

            }
            myHolder.binding.btrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.createRateDialog(list.get(holder.getLayoutPosition()));
                }
            });
            myHolder.itemView.setOnClickListener(view -> {

                RoomModel model2 = list.get(myHolder.getAdapterPosition()).getRoom();
                fragment.setItemRoomData(model2, myHolder.getAdapterPosition());
            });
        } else if (holder instanceof LoadHolder) {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadHolder.binding.progBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public RoomRowBinding binding;

        public MyHolder(@NonNull RoomRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


    public class LoadHolder extends RecyclerView.ViewHolder {
        public LoadMoreBinding binding;

        public LoadHolder(@NonNull LoadMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


    @Override
    public int getItemViewType(int position) {
        RoomModel model = list.get(position).getRoom();
        if (model != null) {
            return DATA;
        } else {
            return LOAD;
        }
    }
}
