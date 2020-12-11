package com.sabo.sabostorev2.Common;

import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.API.RetrofitAPI;
import com.sabo.sabostorev2.Model.UserModel;

import retrofit2.http.PUT;

public class Common {
//    public static final String BASE_URL = "http://10.0.2.2/Android/SaboStore/functions/";
//    public static final String USER_IMAGE_URL = "http://10.0.2.2/Android/SaboStore/users/";
//    public static final String ITEMS_URL = "http://10.0.2.2/Android/SaboStore/items/";

    private static final String BASE_URL = "http://192.168.1.6/Android/SaboStore/functions/";
    public static final String USER_IMAGE_URL = "http://192.168.1.6/Android/SaboStore/users/";
    public static final String ITEMS_URL = "http://192.168.1.6/Android/SaboStore/items/";

    public static APIRequestData getAPI() {
        return RetrofitAPI.getAPI(BASE_URL).create(APIRequestData.class);
    }

    public static final int MAX_SIZE_UPLOAD = (1024 * 1024) * 2; // 2 MB
    public static final int REQUEST_PERMISSION_UPLOAD = 100;
    public static UserModel userModel;

    public static final String SF_KEY_UID = "UID";
    //    public static final String SF_KEY_USERNAME = "USERNAME";
//    public static final String SF_KEY_EMAIL = "EMAIL";
//    public static final String SF_KEY_IMAGE = "IMAGE";
//    public static final String SF_KEY_PHONE = "PHONE";
//    public static final String SF_KEY_GENDER = "GENDER";
    public static final String SF_KEY_LOG_IN = "LOG-IN";

    public static final String LTR = "left-to-right";
    public static final String RTL = "right-to-left";
    public static final String FINFOUT = "fadein-to-fadeout";
}
