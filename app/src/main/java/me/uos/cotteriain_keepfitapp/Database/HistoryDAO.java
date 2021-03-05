package me.uos.cotteriain_keepfitapp.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import me.uos.cotteriain_keepfitapp.History.HistoryData;

@Dao
public interface HistoryDAO {
    /**
     * SQL queries to access history database
     */

    /**
     * Find all instances of history elements saved currently to database
     * @return list wrapped in LiveData to allow ViewModel to observe changes and then update UI
     */
    @Query("SELECT * FROM history ORDER BY id DESC")
    LiveData<List<HistoryData>> loadAllHistory();

    /**
     * Create, edit a specific history element to database
     */
    @Insert
    void createHistory(HistoryData historyData);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void editHistory(HistoryData historyData);

    /**
     * Clear all instances of history elements saved currently to database
     */
    @Query("DELETE FROM history")
    void clearHistoryTable();
}
