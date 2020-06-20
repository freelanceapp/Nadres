package com.endpoint.nadres.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.endpoint.nadres.R;
import com.endpoint.nadres.databinding.SpinnerStageRowBinding;
import com.endpoint.nadres.models.StageDataModel;

import java.util.List;

public class ClassAdapter extends BaseAdapter {
    private List<StageDataModel.Stage.ClassesFk> list;
    private Context context;
    private LayoutInflater inflater;

    public ClassAdapter(List<StageDataModel.Stage.ClassesFk> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") SpinnerStageRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_stage_row,parent,false);
        binding.setName(list.get(position).getTitle());
        return binding.getRoot();
    }
}
