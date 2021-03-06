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

    /**
     * A system that takes in the current date and last date recorded
     * - used within Main Activity
     * @param current
     * @param old
     */
    public DateSystem(String current, String old) {
        this.current = current;
        this.old = old;
    }

    /**
     * A check to see if the current date has already been recorded
     * @return true if dates are the same; false if dates are not
     */
    public Boolean datesMatch(){
        return current.equals(old);
    }

    public String getCurrentDate() { return current; }
    public String getOldDate() { return old; }

    public void setCurrentDate(String current) { this.current = current; }
    public void setOldDate(String old) { this.old = old; }
}
