package com.sabo.sabostorev2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Common.Preferences;
import com.sabo.sabostorev2.Model.ResponseModel;
import com.sabo.sabostorev2.Model.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilUsername, tilEmail, tilPassword;
    private EditText etEmail, etUsername, etPassword;
    private Button btnSignUp;
    private TextView tvSignIn;
    private APIRequestData mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mService = Common.getAPI();
        initViews();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signUp();
                break;
            case R.id.tvSignIn:
                finish();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void signUp() {
        String email = etEmail.getText().toString(),
                username = etUsername.getText().toString(),
                password = etPassword.getText().toString();

        if (checkFields(true, username, email, password)) {
            mService.signUp(email, username, password).enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int code = response.body().getCode();
                    String message = response.body().getMessage();
                    if (code == 1) {
                        etUsername.requestFocus();
                        etUsername.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilUsername.setHelperTextEnabled(true);
                        tilUsername.setHelperText(message);
                        tilUsername.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                    } else if (code == 2) {
                        tilUsername.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        tilUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        tilUsername.setHelperTextEnabled(false);
                        etEmail.requestFocus();
                        etEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilEmail.setHelperTextEnabled(true);
                        tilEmail.setHelperText(message);
                        tilEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilEmail.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                    } else if (code == 3 || code == 4) {
                        clearFields();
                        new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops!")
                                .setContentText(response.body().getMessage())
                                .show();
                    } else {
                        clearFields();
                        UserModel userModel = response.body().getUser();
                        Preferences.setUID(SignUpActivity.this, userModel.getUid());
                        Preferences.setIsLogIn(SignUpActivity.this, true);
                        new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success!")
                                .setContentText(response.body().getMessage())
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                    finish();
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops!")
                            .setContentText(t.getMessage())
                            .show();
                }
            });
        }
    }

    private boolean checkFields(boolean checked, String username, String email, String password) {
        if (TextUtils.isEmpty(username) || username.equals("")) {
            checked = false;
            etUsername.setError("Username is required!");
        }

        if (TextUtils.isEmpty(email) || email.equals("")) {
            checked = false;
            etEmail.setError("Email is required!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            checked = false;
            tilEmail.setHelperTextEnabled(true);
            tilEmail.setHelperText("Invalid email format!");
            tilEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        } else
            tilEmail.setHelperTextEnabled(false);

        if (password.length() < 6) {
            checked = false;
            tilPassword.setHelperTextEnabled(true);
            tilPassword.setHelperText("Password too short (Min.6)");
            tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        } else
            tilPassword.setHelperTextEnabled(false);

        return checked;
    }

    private void clearFields() {
        etUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilUsername.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilUsername.setHelperTextEnabled(false);
        etEmail.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilEmail.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilEmail.setHelperTextEnabled(false);
    }


}