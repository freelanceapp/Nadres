package com.endpoint.nadres.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_knowdetilas.KnowledegeDetialsActivity;
import com.endpoint.nadres.activities_fragments.knowledge_activity.KnowledegeActivity;
import com.endpoint.nadres.databinding.KnowledegeRowBinding;
import com.endpoint.nadres.databinding.LoadMoreBinding;
import com.endpoint.nadres.databinding.TeacherRowBinding;
import com.endpoint.nadres.models.NotificationDataModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Know_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int LOAD = 2;
    private List<NotificationDataModel.NotificationModel> orderlist;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
private KnowledegeActivity knowledegeActivity;
    public Know_Adapter(List<NotificationDataModel.NotificationModel> orderlist, Context context) {
        this.orderlist = orderlist;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        knowledegeActivity=(KnowledegeActivity)context;
 lang = Paper.book().read("lang", Locale.getDefault().getLanguage());}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==ITEM_DATA)
        {
            KnowledegeRowBinding binding  = DataBindingUtil.inflate(inflater, R.layout.knowledege_row,parent,false);
            return new EventHolder(binding);

        }else
            {
                LoadMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more,parent,false);
                return new LoadHolder(binding);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationDataModel.NotificationModel order_data = orderlist.get(position);
        if (holder instanceof EventHolder)
        {
            EventHolder eventHolder = (EventHolder) holder;
           // eventHolder.binding.setLang(lang);
eventHolder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(knowledegeActivity, KnowledegeDetialsActivity.class);
        knowledegeActivity.startActivity(intent);
    }
});
          //  eventHolder.binding.setNotificationModel(order_data);


        }else
            {
                LoadHolder loadHolder = (LoadHolder) holder;
                loadHolder.binding.progBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public KnowledegeRowBinding binding;
        public EventHolder(@NonNull KnowledegeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreBinding binding;
        public LoadHolder(@NonNull LoadMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemViewType(int position) {
        NotificationDataModel.NotificationModel order_Model = orderlist.get(position);
        if (order_Model!=null)
        {
            return ITEM_DATA;
        }else
            {
                return LOAD;
            }

    }


}
