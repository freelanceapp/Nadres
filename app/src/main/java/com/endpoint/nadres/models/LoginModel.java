package com.endpoint.nadres.models;

import android.content.Context;
import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.endpoint.nadres.BR;
import com.endpoint.nadres.R;


public class LoginModel extends BaseObservable {

    private String phone_code;
    private String phone;
    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();


    public LoginModel() {
        this.phone_code = "";
        this.phone="";
    }

    public LoginModel(String phone_code, String phone) {
        this.phone_code = phone_code;
        notifyPropertyChanged(com.endpoint.nadres.BR.phone_code);
        this.phone = phone;
        notifyPropertyChanged(BR.phone);


    }

    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }



    public boolean isDataValid(Context context)
    {
        if (!TextUtils.isEmpty(phone_code)&&
                !TextUtils.isEmpty(phone)
        )
        {
            error_phone_code.set(null);
            error_phone.set(null);

            return true;
        }else
            {
                if (phone_code.isEmpty())
                {
                    error_phone_code.set(context.getString(R.string.field_req));
                }else
                    {
                        error_phone_code.set(null);
                    }

                if (phone.isEmpty())
                {
                    error_phone.set(context.getString(R.string.field_req));
                }else
                {
                    error_phone.set(null);
                }


                return false;
            }
    }


}
