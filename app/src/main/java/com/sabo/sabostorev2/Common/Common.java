package com.sabo.sabostorev2.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sabo.sabostorev2.API.API;
import com.sabo.sabostorev2.API.RetrofitAPI;
import com.sabo.sabostorev2.Model.UserModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Common {
//    public static final String BASE_URL = "http://10.0.2.2/Android/SaboStore/functions/";
//    public static final String USER_IMAGE_URL = "http://10.0.2.2/Android/SaboStore/users/";
//    public static final String ITEMS_URL = "http://10.0.2.2/Android/SaboStore/items/";

    private static final String BASE_URL = "http://192.168.1.3/Android/SaboStore/functions/";
    public static final String USER_IMAGE_URL = "http://192.168.1.3/Android/SaboStore/users/";
    public static final String ITEMS_URL = "http://192.168.1.3/Android/SaboStore/items/";

    public static API getAPI() {
        return RetrofitAPI.getAPI(BASE_URL).create(API.class);
    }

    public static final int MAX_SIZE_UPLOAD = (1024 * 1024) * 2; // 2 MB
    public static final int REQUEST_PERMISSION_CHANGE_PHOTO = 100;
    public static UserModel currentUser;

    public static final String SF_KEY_UID = "UID";
    //    public static final String SF_KEY_USERNAME = "USERNAME";
//    public static final String SF_KEY_EMAIL = "EMAIL";
//    public static final String SF_KEY_IMAGE = "PHONE";
//    public static final String SF_KEY_PHONE = "PHONE";
//    public static final String SF_KEY_GENDER = "GENDER";
    public static final String SF_KEY_LOG_IN = "LOG-IN";
    public static final String INTENT_IMG = "IMAGE";

    public static final String LTR = "left-to-right";
    public static final String RTL = "right-to-left";
    public static final String FINFOUT = "fadein-to-fadeout";

    @SuppressLint("NewApi")
    @Nullable
    public static String formatPhoneNumber(@NotNull Context context, @Nullable String phone) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String defaultCountryIso = countryISO.toUpperCase();
        Log.d("ISO", countryISO.toUpperCase());

        return PhoneNumberUtils.formatNumber(phone, defaultCountryIso);
    }

    @NotNull
    public static String gender2String(@Nullable Integer gender) {
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
    public static int gender2Integer(@Nullable String resultGender) {
        switch (resultGender) {
            case "Male":
                return 1;
            case "Female":
                return 0;
            default:
                return 99;
        }
    }
}
