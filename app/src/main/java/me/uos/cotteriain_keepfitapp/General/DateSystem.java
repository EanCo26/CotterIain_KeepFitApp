package me.uos.cotteriain_keepfitapp.General;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.uos.cotteriain_keepfitapp.R;

public class DateSystem {
    private static final Object LOCK = new Object();
    private static DateSystem sInstance;
    private String current;
    private String old;

    private SharedData sharedData;

    public DateSystem(String current, String old) {
        this.current = current;
        this.old = old;
    }

    public Boolean datesMatch(){
//        return false;
        return current.equals(old);
    }

    public String getCurrentDate() { return current; }
    public String getOldDate() { return old; }

    public void setCurrentDate(String current) { this.current = current; }
    public void setOldDate(String old) { this.old = old; }
}
