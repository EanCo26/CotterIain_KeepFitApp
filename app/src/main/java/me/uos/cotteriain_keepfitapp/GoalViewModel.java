package me.uos.cotteriain_keepfitapp;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.uos.cotteriain_keepfitapp.database.GoalDatabase;
import me.uos.cotteriain_keepfitapp.database.GoalEntry;

public class GoalViewModel extends AndroidViewModel {

    private LiveData<List<GoalEntry>> goalList;

    public GoalViewModel(@NonNull Application application) {
        super(application);

        GoalDatabase goalDatabase = GoalDatabase.getsInstance(this.getApplication());
        goalList = goalDatabase.goalDao().loadAllGoals();
    }

    public LiveData<List<GoalEntry>> getGoalList() { return goalList; }
}
