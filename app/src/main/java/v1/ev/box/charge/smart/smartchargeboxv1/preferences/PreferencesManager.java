package v1.ev.box.charge.smart.smartchargeboxv1.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Deividas on 2017-04-15.
 */

public class PreferencesManager {

    private static PreferencesManager instance;
    private static SharedPreferences preferences;
    private static final String NAME = "USER_PREF";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String IMG_URL = "IMG_URL";
    public static final String USER_ID = "USER_ID";
    private static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String TOKEN = "TOKEN";
    public static final String STATION_ID = "STATION_ID";
    public static final String RADIUS = "RADIUS";
    public static final String BATTERLY_LEVEL = "BATTERLY_LEVEL";

    private PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if(instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void writeString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeFloat(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void writeLoginType(int type) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(LOGIN_TYPE, type);
        editor.apply();
    }

    public String getPrefValue(String key) {
        String restoredText = preferences.getString(key, null);
        if (restoredText != null) {
            return restoredText;
        }
        return "";
    }
    public float getRadius() {
        return preferences.getFloat(RADIUS, 0);
    }

    public float getBatteryLevel() {
        return preferences.getFloat(BATTERLY_LEVEL, 0);
    }

    public int getLoginType() {
        return preferences.getInt(LOGIN_TYPE, -1);
    }

    public void clearData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME, null);
        editor.putString(USER_NAME, null);
        editor.putString(USER_EMAIL, null);
        editor.putString(IMG_URL, null);
        editor.putString(USER_ID, null);
        editor.putInt(LOGIN_TYPE, -1);
        editor.putFloat(RADIUS, 0);
        editor.putString(TOKEN, null);
        editor.putString(STATION_ID, null);
        editor.putFloat(BATTERLY_LEVEL, 0);
        editor.apply();
        /*
            private static final String NAME = "USER_PREF";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String IMG_URL = "IMG_URL";
    public static final String USER_ID = "USER_ID";
    private static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String TOKEN = "TOKEN";
    public static final String STATION_ID = "STATION_ID";
    public static final String RADIUS = "RADIUS";
    public static final String BATTERLY_LEVEL = "BATTERLY_LEVEL";
         */
    }

    public void clearStationId() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(STATION_ID, null);
        editor.apply();
    }
}
