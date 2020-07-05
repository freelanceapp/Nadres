package com.endpoint.nadres.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_requests.RequestActivity;
import com.endpoint.nadres.databinding.RequestRowBinding;
import com.endpoint.nadres.models.RequestDataModel;

import java.util.List;

public class Request_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RequestDataModel.RequestModel> list;
    private Context context;
    private LayoutInflater inflater;
    private RequestActivity activity;
    public Request_Adapter(List<RequestDataModel.RequestModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (RequestActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RequestRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.request_row, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RequestDataModel.RequestModel model = list.get(position);
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;

            myHolder.binding.setModel(model);


            myHolder.binding.btnAccept.setOnClickListener(v -> {
                RequestDataModel.RequestModel model2 = list.get(holder.getAdapterPosition());
                activity.accept_refuse(model2,"accept",holder.getAdapterPosition());
            });

            myHolder.binding.btnRefuse.setOnClickListener(v -> {
                RequestDataModel.RequestModel model2 = list.get(holder.getAdapterPosition());
                activity.accept_refuse(model2,"refuse", holder.getAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public RequestRowBinding binding;

        public MyHolder(@NonNull RequestRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }





}
