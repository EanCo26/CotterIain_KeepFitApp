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

    @Query("SELECT * FROM goal")
    LiveData<List<GoalData>> loadAllGoals();

    @Insert
    void createGoal(GoalData goalData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void editGoal(GoalData goalData);

    @Delete
    void deleteGoal(GoalData goalData);

    @Query("SELECT * FROM goal WHERE id = :id")
    LiveData<GoalData> loadGoalById(int id);

    @Query("DELETE FROM goal")
    void clearGoalTable();
}
