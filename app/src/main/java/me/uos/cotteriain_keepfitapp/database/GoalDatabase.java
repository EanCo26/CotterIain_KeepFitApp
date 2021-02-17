package me.uos.cotteriain_keepfitapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GoalEntry.class}, version = 1, exportSchema = false)
public abstract class GoalDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DB_NAME = "goalDatabase";
    private static GoalDatabase sInstance;
    public abstract GoalDao goalDao();

    public static GoalDatabase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(), GoalDatabase.class, GoalDatabase.DB_NAME).build();
            }
        }
        return sInstance;
    }
}
