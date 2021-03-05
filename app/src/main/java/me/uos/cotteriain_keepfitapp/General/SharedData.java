package me.uos.cotteriain_keepfitapp.General;

import android.content.SharedPreferences;

public class SharedData {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    /**
     * Activities access this to save or load to internal storage
     * @param sharedPref
     */
    public SharedData(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        editor = sharedPref.edit();
    }

    public int getInt(String key, int defaultValue){ return sharedPref.getInt(key, defaultValue); }
    public String getString(String key, String defaultValue){ return sharedPref.getString(key, defaultValue); }
    public Boolean getBool(String key, boolean defaultValue){ return sharedPref.getBoolean(key, defaultValue); }

    public void setInt(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }
    public void setString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }
    public void setBool(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }
}
