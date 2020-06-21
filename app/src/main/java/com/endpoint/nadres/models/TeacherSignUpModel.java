package com.endpoint.nadres.models;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.endpoint.nadres.BR;
import com.endpoint.nadres.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeacherSignUpModel extends BaseObservable implements Serializable{
    private Uri image;
    private String name;
    private String email;
    private String stage_id;
    private List<String>skills;
    private String details;
    private boolean accept_terms;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();



    public boolean isDataValid(Context context){

        if (!name.trim().isEmpty()&&
                !email.trim().isEmpty()&&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()&&
                !stage_id.isEmpty()&&
                skills.size()>0&&
                !details.isEmpty()&&
                accept_terms
        ){
            error_name.set(null);
            error_email.set(null);

            return true;
        }else {

            if (name.trim().isEmpty())
            {
                error_name.set(context.getString(R.string.field_required));
            }else {
                error_name.set(null);

            }


            if (email.trim().isEmpty())
            {
                error_email.set(context.getString(R.string.field_required));
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            {
                error_email.set(context.getString(R.string.inv_email));
            }else {
                error_email.set(null);

            }


            if (stage_id.isEmpty())
            {
                Toast.makeText(context, context.getString(R.string.choose_stage), Toast.LENGTH_SHORT).show();
            }

            if (skills.size()==0)
            {
                Toast.makeText(context, context.getString(R.string.ch_skill), Toast.LENGTH_SHORT).show();
            }


            if (details.trim().isEmpty())
            {
                error_details.set(context.getString(R.string.field_required));
            }else {
                error_details.set(null);

            }

            return false;
        }

    }

    public TeacherSignUpModel() {
        setImage(null);
        setName("");
        setEmail("");
        setStage_id("");
        setSkills(new ArrayList<>());
        setDetails("");
        setAccept_terms(false);

    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }


   @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public String getStage_id() {
        return stage_id;
    }

    public void setStage_id(String stage_id) {
        this.stage_id = stage_id;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);
    }

    public boolean isAccept_terms() {
        return accept_terms;
    }

    public void setAccept_terms(boolean accept_terms) {
        this.accept_terms = accept_terms;
    }
}
