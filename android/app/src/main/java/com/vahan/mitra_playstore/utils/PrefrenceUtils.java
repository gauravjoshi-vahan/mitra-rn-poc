package com.vahan.mitra_playstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.freshchat.consumer.sdk.Freshchat;
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity;

import java.util.Set;

public class PrefrenceUtils {

    static String masterKeyAlias;
    static SharedPreferences sharedPreferences, sharedPreferencesLang;
    private static final String PREF_NAME = "saved_user_data";
    private static final String PREF_NAME_LANGUAGE = "language_data_saved";

    // use the shared preferences and editor as you normally would
    static SharedPreferences.Editor editor, editorLang;

    //    public static SharedPreferences getPrefs(Context context) {
//
//        {
//            try {
//                masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
//                sharedPreferences = EncryptedSharedPreferences.create(
//                        "secret_shared_prefs",
//                        masterKeyAlias,
//                        context,
//                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                );
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return sharedPreferences;
//    }
    public static SharedPreferences getPrefs(Context context) {
        try {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
            editor = sharedPreferences.edit();
        } catch (Exception exception) {
            exception.getMessage();
        }
        return sharedPreferences;
    }

    public static SharedPreferences getPrefslanguage(Context context) {
        try {
            sharedPreferencesLang = context.getSharedPreferences(PREF_NAME_LANGUAGE, 0);
            editorLang = sharedPreferences.edit();
        } catch (Exception exception) {
            exception.getMessage();
        }
        return sharedPreferencesLang;
    }

    public static void insertData(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void insertDataLang(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPrefslanguage(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void insertLongData(Context context, String key, long value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void insertDataInBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void insertDataInBooleanIsUpdate(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPrefslanguage(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static void insertStringSet(Context context, String key, Set<String> value) {
        getPrefs(context).edit().putStringSet(key, value).apply();
    }

    public static String retriveData(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }

    public static String retriveLangData(Context context, String key) {
        return getPrefslanguage(context).getString(key, "");
    }

    public static boolean retriveDataInBoolean(Context context, String key) {
        return getPrefs(context).getBoolean(key, false);
    }

    public static boolean retriveDataInBooleanIsUpdate(Context context, String key) {
        return getPrefslanguage(context).getBoolean(key, false);
    }

    public static long retriveLongData(Context context, String key) {
        return getPrefs(context).getLong(key, 0);
    }

    public static Set<String> getStringSet(Context context, String key) {
        return getPrefs(context).getStringSet(key, null);
    }

    public static void deleteData(Context context, String key) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean checkContains(Context ctx, String key) {
        boolean isContain = getPrefs(ctx).contains(key);
        return isContain;
    }

    public static void logoutUser(Context mcontext) {
        // Clearing all data from Shared Preferences
        SharedPreferences preferences = mcontext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        Freshchat.resetUser(mcontext);
        mcontext.startActivity(new Intent(mcontext, EnterNumberActivity.class)
        .putExtra(Constants.DEVICE_ID_CHECK,"0"));

    }


}
