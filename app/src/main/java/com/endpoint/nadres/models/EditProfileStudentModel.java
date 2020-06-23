package com.endpoint.nadres.models;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.endpoint.nadres.BR;
import com.endpoint.nadres.R;

import java.util.List;


public class EditProfileStudentModel extends BaseObservable {
    private String name;
    private String email;
    private String stage;
    private String clsses;
    private List<String> skills;
    private String details;
    private String type;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public boolean isDataValid(Context context) {
        if (!name.trim().isEmpty() &&
                !email.trim().isEmpty() &&
                !stage.trim().isEmpty()
                && ((type.equals("teacher") && skills.size() > 0 &&
                !details.isEmpty()) || (type.equals("student")&&
                !clsses.isEmpty())) &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        ) {
            error_name.set(null);
            error_email.set(null);
            error_password.set(null);
            error_details.set(null);

            return true;
        } else {
            if (name.trim().isEmpty()) {
                error_name.set(context.getString(R.string.field_required));

            } else {
                error_name.set(null);

            }

            if (email.trim().isEmpty()) {
                error_email.set(context.getString(R.string.field_required));

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                error_email.set(context.getString(R.string.inv_email));

            } else {
                error_email.set(null);

            }
            if (type.equals("teacher")) {
                if (details.isEmpty()) {
                    error_email.set(context.getString(R.string.field_required));

                }
                if (skills.size() == 0) {
                    Toast.makeText(context, context.getString(R.string.ch_skill), Toast.LENGTH_SHORT).show();
                }

            }
            else {
                if (clsses.trim().isEmpty()) {
                    Toast.makeText(context, R.string.choose_classe, Toast.LENGTH_SHORT).show();

                }
            }
            if (stage.trim().isEmpty()) {
                Toast.makeText(context, R.string.choose_stage, Toast.LENGTH_SHORT).show();

            }


            return false;
        }
    }

    public EditProfileStudentModel() {
        setName("");
        setEmail("");
        setStage("");
        setClsses("");
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


    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getClsses() {
        return clsses;
    }

    public void setClsses(String clsses) {
        this.clsses = clsses;
    }

    public String getType() {
        return type;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setType(String type) {
        this.type = type;
    }
}
