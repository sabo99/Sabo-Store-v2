package com.sabo.sabostorev2.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /** UID
     * @param context
     * @param uid
     */
    public static void setUID(Context context, String uid){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(Common.SF_KEY_UID, uid).apply();
    }

    public static String getUID(Context context){
        return getSharedPreference(context).getString(Common.SF_KEY_UID, "");
    }

    /** CountryCode
     * @param context
     * @param cc
     */
    public static void setCountryCode(Context context, String cc){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(Common.SF_KEY_CC, cc).apply();
    }

    public static String getCountryCode(Context context){
        return getSharedPreference(context).getString(Common.SF_KEY_CC, "");
    }

    /** LOGGED IN
     * @param context
     * @param isLogIn
     */
    public static void setIsLogIn(Context context, boolean isLogIn){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(Common.SF_KEY_LOG_IN, isLogIn).apply();
    }

    public static boolean getIsLogIn(Context context){
        return getSharedPreference(context).getBoolean(Common.SF_KEY_LOG_IN, false);
    }

    /** Clear All SharedPreferences
     * @param context
     */
    public static void clearAllPreferences(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear().apply();
    }
}
