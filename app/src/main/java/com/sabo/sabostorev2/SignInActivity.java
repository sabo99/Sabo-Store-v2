package com.sabo.sabostorev2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilEmailUsername, tilPassword;
    private EditText etEmailUsername, etPassword;
    private TextView tvForgotPassword, tvSignUp;
    private Button btnSignIn;
    private APIRequestData mService;
    private LinearLayout llEmail, llPassword;
    private ProgressBar progressBar;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mService = Common.getAPI();
        initViews();
    }

    private void initViews() {
        tilEmailUsername = findViewById(R.id.tilEmailUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etEmailUsername = findViewById(R.id.etEmailUsername);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.tvSignUp:
                clearErrorFields();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.tvForgotPassword:
                forgotPassword();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void signIn() {
        String emailUsername = etEmailUsername.getText().toString(),
                password = etPassword.getText().toString();
        if (checkFields(true, emailUsername, password)) {
            btnSignIn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            mService.signIn(emailUsername, emailUsername, password).enqueue(new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int code = response.body().getCode();
                    String message = response.body().getMessage();
                    btnSignIn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (code == 1) {
                        etEmailUsername.requestFocus();
                        etEmailUsername.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilEmailUsername.setHelperTextEnabled(true);
                        tilEmailUsername.setHelperText(message);
                        tilEmailUsername.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilEmailUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                    } else if (code == 2) {
                        tilEmailUsername.setHelperTextEnabled(false);
                        etEmailUsername.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        tilEmailUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                        etPassword.requestFocus();
                        etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilPassword.setHelperTextEnabled(true);
                        tilPassword.setHelperText(message);
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        tilPassword.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                    } else if (code == 3 || code == 4) {
                        tilEmailUsername.setHelperTextEnabled(false);
                        etEmailUsername.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        tilEmailUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                        tilPassword.setHelperTextEnabled(false);
                        etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        tilPassword.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                        new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops!")
                                .setContentText(message)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    clearErrorFields();
                                })
                                .show();
                    } else {
                        UserModel userModel = response.body().getUser();
                        Preferences.setUID(SignInActivity.this, userModel.getUid());
                        Preferences.setIsLogIn(SignInActivity.this, true);
                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                        finish();
                        clearErrorFields();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    btnSignIn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    clearErrorFields();
                    new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText(t.getMessage())
                            .show();
                }
            });
        }
    }

    private boolean checkFields(boolean checked, String emailUsername, String password) {
        if (TextUtils.isEmpty(emailUsername) || emailUsername.equals("")) {
            checked = false;
            etEmailUsername.setError("Email or Username is required!");
        }

        if (password.length() < 6) {
            checked = false;
            tilPassword.setHelperTextEnabled(true);
            tilPassword.setHelperText("Password too short (Min.6)");
            tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        } else
            tilPassword.setHelperTextEnabled(false);

        return checked;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clearErrorFields() {
        etEmailUsername.clearFocus();
        etEmailUsername.setText("");
        tilEmailUsername.setHelperTextEnabled(false);
        etEmailUsername.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilEmailUsername.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        etPassword.clearFocus();
        etPassword.setText("");
        tilPassword.setHelperTextEnabled(false);
        etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tilPassword.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void forgotPassword() {
        state = 0;
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null);
        llEmail = view.findViewById(R.id.llEmail);
        llPassword = view.findViewById(R.id.llPassword);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail),
                tilPassword = view.findViewById(R.id.tilPassword),
                tilRetypePassword = view.findViewById(R.id.tilRetypePassword);
        EditText etEmail = view.findViewById(R.id.etEmail),
                etPassword = view.findViewById(R.id.etPassword),
                etRetypePassword = view.findViewById(R.id.etRetypePassword);
        Button btnContinue = view.findViewById(R.id.btnContinue);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        SweetAlertDialog sweetForgotPassword = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetForgotPassword.setTitleText("Forgot Password!")
                .setOnShowListener(dialog -> {

//                    if (state == 0) {
//                        llEmail.setVisibility(View.VISIBLE);
//                        llPassword.setVisibility(View.INVISIBLE);
//                    }
//                    if (state == 1) {
//                        llEmail.setVisibility(View.INVISIBLE);
//                        llPassword.setVisibility(View.VISIBLE);
//                    }

                    btnContinue.setOnClickListener(v -> {
                        String email = etEmail.getText().toString();
                        if (state == 0) {
                            if (email.equals("") || TextUtils.isEmpty(email)) {
                                tilEmail.setHelperText("Email is required!");
                                tilEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                tilEmail.setHelperTextEnabled(true);
                                tilEmail.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etEmail.requestFocus();

                            } else {
                                btnContinue.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(() -> {
                                    mService.checkEmailExist(email).enqueue(new Callback<ResponseModel>() {
                                        @Override
                                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                            int code = response.body().getCode();
                                            btnContinue.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            if (code == 1) {
                                                tilEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                                tilEmail.setHelperText(response.body().getMessage());
                                            }
                                            if (code == 0) {
                                                btnContinue.setText("Change password");
                                                Animation animation = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.slide_to_left);
                                                llEmail.setAnimation(animation);
                                                llEmail.setVisibility(View.GONE);

                                                Animation animation1 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.slide_from_right_to_left);
                                                llPassword.setVisibility(View.VISIBLE);
                                                llPassword.setAnimation(animation1);
                                                /** Change State */
                                                state = 1;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                                            btnContinue.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops!")
                                                    .setContentText(t.getMessage())
                                                    .show();
                                        }
                                    });
                                }, 1000);
                            }
                        }

                        if (state == 1) {
                            String password = etPassword.getText().toString(),
                                    retype = etRetypePassword.getText().toString();
                            if (password.equals("") || password.length() < 6) {
                                tilPassword.setHelperTextEnabled(true);
                                tilPassword.setHelperText("Password to short (Min.6)");
                                tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                tilPassword.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etPassword.requestFocus();
                            } else if (!retype.equals(password)) {
                                tilPassword.setHelperTextEnabled(false);
                                tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                tilRetypePassword.setHelperTextEnabled(true);
                                tilRetypePassword.setHelperText("Password not matched!");
                                tilRetypePassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                tilRetypePassword.setHintTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etRetypePassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                etRetypePassword.requestFocus();
                            } else {
                                tilPassword.setHelperTextEnabled(false);
                                tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                etPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                tilRetypePassword.setHelperTextEnabled(false);
                                tilRetypePassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                etRetypePassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                                btnContinue.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(() -> {
                                    mService.forgotPassword(email, password).enqueue(new Callback<ResponseModel>() {
                                        @Override
                                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                            int code = response.body().getCode();
                                            String message = response.body().getMessage();
                                            btnContinue.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            if (code == 1)
                                                new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Oops!")
                                                        .setContentText(message)
                                                        .show();
                                            else {
                                                /** Change State */
                                                state = 0;
                                                new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success!")
                                                        .setContentText(message)
                                                        .setConfirmClickListener(sweetAlertDialog -> {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            sweetForgotPassword.dismissWithAnimation();
                                                        })
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                                            btnContinue.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new SweetAlertDialog(SignInActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops!")
                                                    .setContentText(t.getMessage())
                                                    .show();
                                        }
                                    });
                                }, 1000);
                            }
                        }
                    });
                });
        sweetForgotPassword.setCanceledOnTouchOutside(true);
        sweetForgotPassword.show();

        LinearLayout linearLayout = sweetForgotPassword.findViewById(R.id.loading);
        Button confirm = sweetForgotPassword.findViewById(R.id.confirm_button);
        confirm.setVisibility(View.GONE);
        int index = linearLayout.indexOfChild(linearLayout.findViewById(R.id.content_text));
        linearLayout.addView(view, index + 1);
    }

    @Override
    public void onBackPressed() {
        if (state == 0)
            super.onBackPressed();
        if (state == 1) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_right);
            llPassword.setAnimation(animation);
            llPassword.setVisibility(View.INVISIBLE);

            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_from_left_to_right);
            llEmail.setVisibility(View.VISIBLE);
            llEmail.setAnimation(animation1);
            state = 0;
        }
    }
}