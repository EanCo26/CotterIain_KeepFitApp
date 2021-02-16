package me.uos.cotteriain_keepfitapp;

import android.content.SharedPreferences;

public class SharedData {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SharedData(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public int getData(String key, int defaultValue){ return sharedPref.getInt(key, defaultValue); }
    public String getData(String key, String defaultValue){ return sharedPref.getString(key, defaultValue); }

    public void setData(String key, int value){
        editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
