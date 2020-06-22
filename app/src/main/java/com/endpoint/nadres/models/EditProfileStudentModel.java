package com.endpoint.nadres.models;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.endpoint.nadres.BR;
import com.endpoint.nadres.R;


public class EditProfileStudentModel extends BaseObservable {
    private String name;
    private String email;
    private String stage;
    private String clsses;
    private String image_url;
    private boolean isTermsAccepted;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();


    public boolean isDataValid(Context context) {
        if (!name.trim().isEmpty() &&
                !email.trim().isEmpty() &&
                !stage.trim().isEmpty() &&
                !clsses.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                isTermsAccepted
        ) {
            error_name.set(null);
            error_email.set(null);
            error_password.set(null);

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

            if (stage.trim().isEmpty()) {
                Toast.makeText(context, R.string.choose_stage, Toast.LENGTH_SHORT).show();

            }
            if (clsses.trim().isEmpty()) {
                Toast.makeText(context, R.string.choose_classe, Toast.LENGTH_SHORT).show();

            }
            if (!isTermsAccepted) {
                Toast.makeText(context, R.string.cannot_signup, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public EditProfileStudentModel() {
        setName("");
        setEmail("");
        setStage("");
        setClsses("");
        isTermsAccepted = false;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public boolean isTermsAccepted() {
        return isTermsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        isTermsAccepted = termsAccepted;
    }
}
