package me.uos.cotteriain_keepfitapp.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import me.uos.cotteriain_keepfitapp.Goal.GoalData;

@Dao
public interface GoalDao {
    /**
     * SQL queries to access goal database
     */

    /**
     * Find all instances of goals saved currently to database
     * @return list wrapped in LiveData to allow ViewModel to observe changes and then update UI
     */
    @Query("SELECT * FROM goal")
    LiveData<List<GoalData>> loadAllGoals();

    /**
     * Create, edit, delete and load specific goal to database
     */
    @Insert
    void createGoal(GoalData goalData);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void editGoal(GoalData goalData);
    @Delete
    void deleteGoal(GoalData goalData);
    @Query("SELECT * FROM goal WHERE id = :id")
    LiveData<GoalData> loadGoalById(int id);

    /**
     * Clear all instances of goals saved currently to database
     */
    @Query("DELETE FROM goal")
    void clearGoalTable();
}
