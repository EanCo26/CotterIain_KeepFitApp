package me.uos.cotteriain_keepfitapp.Database;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import me.uos.cotteriain_keepfitapp.History.HistoryData;

@androidx.room.Database(entities = {HistoryData.class}, version = 2, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "historyDB";
    private static HistoryDatabase sInstance;
    public abstract HistoryDAO historyDAO();

    public static HistoryDatabase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class, HistoryDatabase.DB_NAME).build();
            }
        }
        return sInstance;
    }
}
