package me.uos.cotteriain_keepfitapp.GoalSettings;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.uos.cotteriain_keepfitapp.Database.GoalDatabase;

public class GoalViewModel extends AndroidViewModel {

    private LiveData<List<GoalData>> goalList;

    public GoalViewModel(@NonNull Application application) {
        super(application);

        GoalDatabase goalDatabase = GoalDatabase.getsInstance(this.getApplication());
        goalList = goalDatabase.goalDao().loadAllGoals();
    }

    public LiveData<List<GoalData>> getGoalList() { return goalList; }
}
