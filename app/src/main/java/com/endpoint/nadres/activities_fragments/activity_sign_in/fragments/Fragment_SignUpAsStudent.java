package com.endpoint.nadres.activities_fragments.activity_sign_in.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.endpoint.ghair.R;
import com.endpoint.ghair.activities_fragments.activity_sign_in.activities.SignInActivity;
import com.endpoint.ghair.activities_fragments.activity_terms.TermsActivity;
import com.endpoint.ghair.adapters.CityAdapter;
import com.endpoint.ghair.databinding.FragmentSignUpAsCustomerBinding;
import com.endpoint.ghair.interfaces.Listeners;
import com.endpoint.ghair.models.Cities_Model;
import com.endpoint.ghair.models.SignUpCustomerModel;
import com.endpoint.ghair.models.UserModel;
import com.endpoint.ghair.preferences.Preferences;
import com.endpoint.ghair.remote.Api;
import com.endpoint.ghair.share.Common;
import com.endpoint.ghair.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_SignUpAsStudent extends Fragment implements Listeners.SignUpListener,Listeners.BackListener, Listeners.ShowCountryDialogListener, OnCountryPickerListener  {
    private SignInActivity activity;
    private String current_language;
    private FragmentSignUpAsCustomerBinding binding;
    private CountryPicker countryPicker;
    private SignUpCustomerModel signUpModel;
    private Preferences preferences;
    private int isAcceptTerms = 0;
    private CityAdapter adapter;
    private List<Cities_Model.Data> dataList;
    private String city_id = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_as_customer, container, false);

        initView();
        return binding.getRoot();
    }

    private void initView() {
        dataList=new ArrayList<>();
        signUpModel = new SignUpCustomerModel();
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        preferences = Preferences.getInstance();
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());binding.setLang(current_language);
        binding.setSignUpModel(signUpModel);
        binding.setSignUpListener(this);
        binding.setShowDialogListener(this);
        createCountryDialog();
        adapter=new CityAdapter(dataList,activity);
        binding.spinnerCity.setAdapter(adapter);
        binding.checkradio.setOnClickListener(view -> {
            if (binding.checkradio.isChecked())
            {
                isAcceptTerms = 1;
                signUpModel.setIsAcceptTerms(isAcceptTerms);
                navigateToTermsActivity();
            }else
            {
                isAcceptTerms = 0;
                signUpModel.setIsAcceptTerms(isAcceptTerms);

            }
        });








        getCities();

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    city_id = "";
                    signUpModel.setCity_id(city_id);
                    binding.setSignUpModel(signUpModel);
                } else {
                    city_id = String.valueOf(dataList.get(i).getId_city());
                    signUpModel.setCity_id(city_id);
                    binding.setSignUpModel(signUpModel);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
binding.tvRegister.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        activity.DisplayFragmentSignUpMarketr();
    }
});
    }
    private void updateCityAdapter(Cities_Model body) {
        dataList.add(new Cities_Model.Data(activity.getResources().getString(R.string.ch_city)));
        if(body.getData()!=null){
            dataList.addAll(body.getData());
            adapter.notifyDataSetChanged();}
    }
    private void getCities() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .getCity(current_language)
                    .enqueue(new Callback<Cities_Model>() {
                        @Override
                        public void onResponse(Call<Cities_Model> call, Response<Cities_Model> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                if(response.body().getData()!=null){
                                    updateCityAdapter(response.body());}
                                else {
                                    Log.e("error",response.code()+"_"+response.errorBody());

                                }

                            } else {

                                try {

                                    Log.e("error",response.code()+"_"+response.errorBody().string());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();



                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<Cities_Model> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }

    }

    private void navigateToTermsActivity() {

        Intent intent = new Intent(activity, TermsActivity.class);
        startActivity(intent);

    }


    public static Fragment_SignUpAsStudent newInstance() {
        return new Fragment_SignUpAsStudent();
    }

    private void createCountryDialog()
    {
        countryPicker = new CountryPicker.Builder()
                .canSearch(false)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        if (countryPicker.getCountryFromSIM()!=null)
        {
            updatePhoneCode(countryPicker.getCountryFromSIM());
        }else if (telephonyManager!=null&&countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso())!=null)
        {
            updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
        }else if (countryPicker.getCountryByLocale(Locale.getDefault())!=null)
        {
            updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
        }else
        {
            String code = "+966";
            binding.tvCode.setText(code);
            signUpModel.setPhone_code(code.replace("+","00"));

        }

    }

    @Override
    public void showDialog() {

        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);

    }

    private void updatePhoneCode(Country country)
    {
        binding.tvCode.setText(country.getDialCode());
        signUpModel.setPhone_code(country.getDialCode().replace("+","00"));

    }

    @Override
    public void checkDataSignUpCustomer(String name, String phone_code, String phone, String password) {
        if (phone.startsWith("0")) {
            phone = phone.replaceFirst("0", "");
        }
        signUpModel = new SignUpCustomerModel(name,city_id,phone_code,phone,password,isAcceptTerms);
        binding.setSignUpModel(signUpModel);
        if (signUpModel.isDataValid(activity))
        {
            signUp(signUpModel);
        }
    }

    private void signUp(SignUpCustomerModel signUpModel) {
        try {
            ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .sign_up(signUpModel.getName(),signUpModel.getPhone_code(), signUpModel.getPhone(),"1",signUpModel.getCity_id(),signUpModel.getPassword(),"0")
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                activity.displayFragmentCodeVerification(response.body(),1);


                            } else {
                                try {

                                    Log.e("error",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 422) {
                                    Toast.makeText(activity, getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
                                } else if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                }else
                                {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();


                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    @Override
    public void back() {
        activity.Back();
    }


}
