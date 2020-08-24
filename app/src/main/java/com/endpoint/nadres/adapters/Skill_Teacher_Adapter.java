package com.endpoint.nadres.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
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
import com.endpoint.nadres.databinding.SkillRowBinding;
import com.endpoint.nadres.databinding.TeacherRowBinding;
import com.endpoint.nadres.models.SearchTeacherModel;
import com.endpoint.nadres.models.TeacherModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Skill_Teacher_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int LOAD = 2;
    private List<SearchTeacherModel.Data.SkillsFk> dataList;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private int i = 0;

    public Skill_Teacher_Adapter(List<SearchTeacherModel.Data.SkillsFk> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            SkillRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.skill_row, parent, false);
            return new EventHolder(binding);

        } else {
            LoadMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more, parent, false);
            return new LoadHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchTeacherModel.Data.SkillsFk order_data = dataList.get(position);
        if (holder instanceof EventHolder) {
            EventHolder eventHolder = (EventHolder) holder;
            // eventHolder.binding.setLang(lang);

            if(dataList.get(position).getSkill_type().equals("listening")){
             ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.listening));
            }
            else  if(dataList.get(position).getSkill_type().equals("speaking")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.speaking));

            }
            else  if(dataList.get(position).getSkill_type().equals("reading")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.reading));

            }
            else  if(dataList.get(position).getSkill_type().equals("writing")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.writing));

            }
            else  if(dataList.get(position).getSkill_type().equals("vocabulary")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.vocabulary));


            }
            else  if(dataList.get(position).getSkill_type().equals("grammer")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.grammer));

            }
            else  if(dataList.get(position).getSkill_type().equals("dictation")){
                ((EventHolder) eventHolder).binding.tvtitle.setText(context.getResources().getString(R.string.dictation));

            }
           // eventHolder.binding.setModel(order_data);
            eventHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i = holder.getLayoutPosition();
                    notifyDataSetChanged();
                }
            });
            if (i == position) {
                eventHolder.binding.fr.setBackground(context.getResources().getDrawable(R.drawable.small_rounded_select));
                if (context instanceof SearchActivity) {
                    SearchActivity searchActivity = (SearchActivity) context;
                    searchActivity.setskill(dataList.get(position).getSkill_type());
                }
            } else {
                eventHolder.binding.fr.setBackground(null);

            }

        } else {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public SkillRowBinding binding;

        public EventHolder(@NonNull SkillRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreBinding binding;

        public LoadHolder(@NonNull LoadMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemViewType(int position) {
        SearchTeacherModel.Data.SkillsFk order_Model = dataList.get(position);
        if (order_Model != null) {
            return ITEM_DATA;
        } else {
            return LOAD;
        }

    }


}
