package me.uos.cotteriain_keepfitapp.General;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.uos.cotteriain_keepfitapp.R;

public class DateSystem {
    private static String current;
    private String old;

    public DateSystem(String current, String old) {
        this.current = current;
        this.old = old;
    }

    public Boolean datesMatch(){
        //todo remove false return
        return false;
//        return current.equals(old);
    }

    public String getCurrentDate() { return current; }
    public String getOldDate() { return old; }

    public void setCurrentDate(String current) { this.current = current; }
    public void setOldDate(String old) { this.old = old; }
}
