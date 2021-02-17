package me.uos.cotteriain_keepfitapp.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GoalDao {

    @Query("SELECT * FROM goal")
    LiveData<List<GoalEntry>> loadAllGoals();

    @Insert
    void createGoal(GoalEntry goalEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void editGoal(GoalEntry goalEntry);

    @Delete
    void deleteGoal(GoalEntry goalEntry);

    @Query("SELECT * FROM goal WHERE id = :id")
    LiveData<GoalEntry> loadGoalById(int id);

    @Query("DELETE FROM goal")
    void clearGoalTable();
}
