package com.sabo.sabostorev2.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.API.RetrofitAPI;
import com.sabo.sabostorev2.Model.Item.ItemStoreModel;
import com.sabo.sabostorev2.Model.Item.ItemsModel;
import com.sabo.sabostorev2.Model.ResponseModel;
import com.sabo.sabostorev2.Model.UserModel;
import com.sabo.sabostorev2.R;
import com.sabo.sabostorev2.RoomDB.User.UserDataSource;
import com.sabo.sabostorev2.ui.SignIn.SignIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {
    private static final String BASE_URL = "https://monaksi-v001.000webhostapp.com/android/SaboStore/functions/";
    public static final String USER_IMAGE_URL = "https://monaksi-v001.000webhostapp.com/android/SaboStore/users/";
    public static final String ITEMS_URL = "https://monaksi-v001.000webhostapp.com/android/SaboStore/items/";

    private static final String ExchangeRatesAPI = "https://api.exchangeratesapi.io/";
//    private static final String BASE_URL = "http://192.168.1.3/Android/SaboStore/functions/";
//    public static final String USER_IMAGE_URL = "http://192.168.1.3/Android/SaboStore/users/";
//    public static final String ITEMS_URL = "http://192.168.1.3/Android/SaboStore/items/";

    public static APIRequestData getAPI() {
        return RetrofitAPI.getAPI(BASE_URL).create(APIRequestData.class);
    }

    public static APIRequestData getExchangeRatesAPI() {
        return RetrofitAPI.getExchangeRates(ExchangeRatesAPI).create(APIRequestData.class);
    }

    public static final int MAX_SIZE_UPLOAD = (1024 * 1024) * 2; // 2 MB
    public static final int REQUEST_PERMISSION_CHANGE_PHOTO = 100;
    public static UserModel currentUser;
    public static double totalPrice;

    public static final String SF_KEY_UID = "UID";
    //    public static final String SF_KEY_USERNAME = "USERNAME";
    //    public static final String SF_KEY_EMAIL = "EMAIL";
    //    public static final String SF_KEY_IMAGE = "IMAGE";
    //    public static final String SF_KEY_PHONE = "PHONE";
    //    public static final String SF_KEY_GENDER = "GENDER";
    public static final String SF_KEY_LOG_IN = "LOG-IN";
    public static final String SF_KEY_LOG_IN_PIN = "LOG-IN-with-PIN";
    public static final String SF_KEY_SAVE_STATE_STATUS_ORDER = "SAVE-STATE-STATUS-ORDER";
    public static final String INTENT_IMG = "INT-IMAGE";

    public static final String LTR = "left-to-right";
    public static final String RTL = "right-to-left";
    public static final String FINFOUT = "fadein-to-fadeout";
    public static final String UTB = "up-to-bottom";
    public static final String BTU = "bottom-to-up";

    private static APIRequestData mService = getAPI();
    public static double ratesIDR;
    @Nullable
    public static List<ItemStoreModel> itemStoreModels;
    @NotNull
    public static ItemsModel itemDetails;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public static String formatPhoneNumber(@NotNull Context context, @Nullable String phone) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String defaultCountryIso = countryISO.toUpperCase();
        Log.d("ISO", countryISO.toUpperCase());

        return PhoneNumberUtils.formatNumber(phone, defaultCountryIso);
    }

    @NotNull
    public static String genderToString(@Nullable Integer gender) {
        switch (gender) {
            case 1:
                return "Male";
            case 0:
                return "Female";
            default:
                return "Unknown";
        }
    }

    @NotNull
    public static int genderToInteger(@Nullable String resultGender) {
        switch (resultGender) {
            case "Male":
                return 1;
            case "Female":
                return 0;
            default:
                return 999;
        }
    }

    @NotNull
    public static String formatPriceUSDToString(double price) {
        if (price != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            return new StringBuilder().append(df.format(price)).toString();

        } else
            return "0.00";
    }

    @NotNull
    public static double formatPriceUSDToDouble(double price) {
        if (price != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            return Double.parseDouble(df.format(price));
        } else
            return 0.00;
    }

    public static void setSpanStringNickname(String welcome, String nickname, TextView tvHeader) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(nickname);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        tvHeader.setText(builder, TextView.BufferType.SPANNABLE);
    }

    @NotNull
    public static String orderDateFormat(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Unknown";
        }
    }

    public static void checkOrderStatus(@NotNull ImageView ivLogoStatus, @NotNull TextView tvOrderStatus, int orderStatus) {
        switch (orderStatus) {
            case 0:
                ivLogoStatus.setImageResource(R.drawable.ic_close_black_24dp);
                tvOrderStatus.setText("Order Canceled");
                break;
            case 1:
                ivLogoStatus.setImageResource(R.drawable.ic_history);
                tvOrderStatus.setText("Ordered");
                break;
            case 2:
                ivLogoStatus.setImageResource(R.drawable.ic_on_process);
                tvOrderStatus.setText("On Process");
                break;
            case 3:
                ivLogoStatus.setImageResource(R.drawable.ic_shipped);
                tvOrderStatus.setText("Shipped");
                break;
            case 4:
                ivLogoStatus.setImageResource(R.drawable.ic_received);
                tvOrderStatus.setText("Received");
                break;
            default:
                break;
        }
    }

    @NotNull
    public static String statusOrderToString(int statusOrder) {
        switch (statusOrder) {
            case -1:
                return "All";
            case 0:
                return "Order Canceled";
            case 1:
                return "Ordered";
            case 2:
                return "On Process";
            case 3:
                return "Shipped";
            case 4:
                return "Received";
            default:
                return "";
        }
    }

    @NotNull
    public static int statusOrderToInteger(String statusOrder) {
        switch (statusOrder) {
            case "All":
                return -1;
            case "Order Canceled":
                return 0;
            case "Ordered":
                return 1;
            case "On Process":
                return 2;
            case "Shipped":
                return 3;
            case "Received":
                return 4;
            default:
                return 999;
        }
    }

    public static void twoStepVerification(Context context, Class aClass) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pin, null);
        EditText etPIN = view.findViewById(R.id.etPIN);
        CardView cvConfirm = view.findViewById(R.id.cvConfirm);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        btnConfirm.setEnabled(false);
        cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
        SweetAlertDialog sweetPIN = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetPIN.setTitleText("Two-step verification")
                .setCustomImage(R.drawable.logo_pin_code)
                .setOnShowListener(dialog -> {
                    etPIN.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().length() < 6) {
                                btnConfirm.setEnabled(false);
                                cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                btnConfirm.setEnabled(true);
                                cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    btnConfirm.setOnClickListener(v -> {
                        String uid = Preferences.getUID(context), pin = etPIN.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        btnConfirm.setEnabled(false);
                        cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                        mService.signInWithPIN(uid, pin).enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                int code = response.body().getCode();
                                String message = response.body().getMessage();
                                progressBar.setVisibility(View.INVISIBLE);
                                btnConfirm.setEnabled(true);
                                cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                                if (code == 4) {
                                    sweetPIN.dismissWithAnimation();
                                    context.startActivity(new Intent(context, aClass));
                                    CustomIntent.customType(context, Common.LTR);
                                } else {
                                    Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    t.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                progressBar.setVisibility(View.INVISIBLE);
                                btnConfirm.setEnabled(true);
                                cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                });
        sweetPIN.setCanceledOnTouchOutside(false);
        sweetPIN.show();
        LinearLayout linearLayout = sweetPIN.findViewById(R.id.loading);
        Button confirm = sweetPIN.findViewById(R.id.confirm_button);
        confirm.setVisibility(View.GONE);
        int index = linearLayout.indexOfChild(linearLayout.findViewById(R.id.content_text));
        linearLayout.addView(view, index + 1);
    }

    public static void onLogout(Context context, UserDataSource userDataSource) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sign out!")
                .setContentText("Are you sure?")
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                })
                .setConfirmText("Yes")
                .setConfirmClickListener(sweetAlertDialog -> {
                    String uid = Preferences.getUID(context);
                    mService.signOut(uid).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            int code = response.body().getCode();
                            String message = response.body().getMessage();
                            switch (code) {
                                case 0:
                                    sweetAlertDialog.setTitleText("Oops!")
                                            .setContentText(message)
                                            .showCancelButton(false)
                                            .setConfirmText("Close")
                                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                                sweetAlertDialog.dismissWithAnimation();
                                            })
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    break;
                                case 1:
                                    sweetAlertDialog.dismissWithAnimation();
                                    userDataSource.removeAccount(uid);
                                    Preferences.clearAllPreferences(context);
                                    context.startActivity(new Intent(context, SignIn.class));
                                    ((Activity) context).finish();
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            String message = t.getMessage();
                            if (message.contains("10000ms"))
                                sweetAlertDialog.setTitleText("Oops!")
                                        .setContentText(t.getMessage())
                                        .showCancelButton(false)
                                        .setConfirmText("Close")
                                        .setConfirmClickListener(sweetAlertDialog1 -> {
                                            sweetAlertDialog.dismissWithAnimation();
                                        })
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            else
                                sweetAlertDialog.setTitleText("Oops!")
                                        .setContentText(t.getMessage())
                                        .showCancelButton(false)
                                        .setConfirmText("Close")
                                        .setConfirmClickListener(sweetAlertDialog1 -> {
                                            sweetAlertDialog.dismissWithAnimation();
                                        })
                                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        }
                    });
                })
                .show();
    }
}
