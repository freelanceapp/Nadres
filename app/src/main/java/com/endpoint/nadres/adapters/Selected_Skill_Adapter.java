package com.endpoint.nadres.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.icu.text.Edits;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_editprofile.EditProfileActivity;
import com.endpoint.nadres.activities_fragments.activity_sign_in.fragments.FragmentSignUpAsTeacher;
import com.endpoint.nadres.databinding.LoadMoreBinding;
import com.endpoint.nadres.databinding.SelectedSkillRowBinding;
import com.endpoint.nadres.databinding.TeacherRowBinding;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Selected_Skill_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> list;
    private Context context;
    private LayoutInflater inflater;
    private FragmentSignUpAsTeacher fragment;

    public Selected_Skill_Adapter(List<String> list, Context context,FragmentSignUpAsTeacher fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SelectedSkillRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.selected_skill_row, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       String skill = list.get(position);
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;

            myHolder.binding.setName(skill);


            myHolder.binding.imageDelete.setOnClickListener(v -> {
                if(fragment!=null){
                fragment.setItemDelete(holder.getAdapterPosition());}
                else {
                    EditProfileActivity editProfileActivity=(EditProfileActivity)context;
                    editProfileActivity.setItemDelete(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SelectedSkillRowBinding binding;

        public MyHolder(@NonNull SelectedSkillRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }





}
