package me.uos.cotteriain_keepfitapp.Database;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import me.uos.cotteriain_keepfitapp.Goal.GoalData;

@androidx.room.Database(entities = {GoalData.class}, version = 1, exportSchema = false)
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
