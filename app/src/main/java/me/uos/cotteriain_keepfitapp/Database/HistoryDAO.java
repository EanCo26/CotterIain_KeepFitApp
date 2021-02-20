package me.uos.cotteriain_keepfitapp.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import me.uos.cotteriain_keepfitapp.GoalSettings.GoalData;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryData;

@Dao
public interface HistoryDAO {

    @Query("SELECT * FROM history")
    LiveData<List<HistoryData>> loadAllHistory();

    @Insert
    void createHistory(HistoryData historyData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void editHistory(HistoryData historyData);

    @Query("DELETE FROM history")
    void clearHistoryTable();
}
