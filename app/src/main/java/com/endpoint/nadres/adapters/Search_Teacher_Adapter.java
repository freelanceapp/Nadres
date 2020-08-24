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
import com.endpoint.nadres.activities_fragments.activity_search.SearchActivity;
import com.endpoint.nadres.activities_fragments.activity_teachers.TeacherActivity;
import com.endpoint.nadres.databinding.LoadMoreBinding;
import com.endpoint.nadres.databinding.SearchTeacherRowBinding;
import com.endpoint.nadres.databinding.TeacherRowBinding;
import com.endpoint.nadres.models.SearchTeacherModel;
import com.endpoint.nadres.models.TeacherModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Search_Teacher_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int LOAD = 2;
    private List<SearchTeacherModel.Data> dataList;
    private Context context;
    private LayoutInflater inflater;
    private String lang;

    public Search_Teacher_Adapter(List<SearchTeacherModel.Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
 lang = Paper.book().read("lang", Locale.getDefault().getLanguage());}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==ITEM_DATA)
        {
            SearchTeacherRowBinding binding  = DataBindingUtil.inflate(inflater, R.layout.search_teacher_row,parent,false);
            return new EventHolder(binding);

        }else
            {
                LoadMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more,parent,false);
                return new LoadHolder(binding);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchTeacherModel.Data order_data = dataList.get(position);
        if (holder instanceof EventHolder)
        {
            EventHolder eventHolder = (EventHolder) holder;
           // eventHolder.binding.setLang(lang);

            eventHolder.binding.setModel(order_data);
            eventHolder.binding.imchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof SearchActivity){
                        SearchActivity teacherActivity=(SearchActivity) context;
                        teacherActivity.setItemData(dataList.get(eventHolder.getLayoutPosition()));
                    }

                }
            });


        }else
            {
                LoadHolder loadHolder = (LoadHolder) holder;
                loadHolder.binding.progBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public SearchTeacherRowBinding binding;
        public EventHolder(@NonNull SearchTeacherRowBinding binding) {
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
     SearchTeacherModel.Data order_Model = dataList.get(position);
        if (order_Model!=null)
        {
            return ITEM_DATA;
        }else
            {
                return LOAD;
            }

    }


}
