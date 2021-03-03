package me.uos.cotteriain_keepfitapp.History;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class HistoryData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;
    private String goalName;
    private int stepsTaken;
    private int goalSteps;
    private int percentageToGoal;

    @Ignore
    public HistoryData(String date, String goalName, int stepsTaken, int goalSteps, int percentageToGoal) {
        this.date = date;
        this.goalName = goalName;
        this.stepsTaken = stepsTaken;
        this.goalSteps = goalSteps;
        this.percentageToGoal = percentageToGoal;
    }

    @Ignore
    public HistoryData( String goalName, int stepsTaken, int goalSteps, int percentageToGoal) {
        this.goalName = goalName;
        this.stepsTaken = stepsTaken;
        this.goalSteps = goalSteps;
        this.percentageToGoal = percentageToGoal;
    }

    public HistoryData(int id, String date, String goalName, int stepsTaken, int goalSteps, int percentageToGoal) {
        this.id = id;
        this.date = date;
        this.goalName = goalName;
        this.stepsTaken = stepsTaken;
        this.goalSteps = goalSteps;
        this.percentageToGoal = percentageToGoal;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getGoalName() { return goalName; }
    public int getStepsTaken() { return stepsTaken; }
    public int getGoalSteps() { return goalSteps; }
    public int getPercentageToGoal() { return percentageToGoal; }

    public void setId(int id) { this.id = id; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public void setDate(String date) { this.date = date; }
    public void setStepsTaken(int stepsTaken) { this.stepsTaken = stepsTaken; }
    public void setGoalSteps(int goalSteps) { this.goalSteps = goalSteps; }
    public void setPercentageToGoal(int percentageToGoal) { this.percentageToGoal = percentageToGoal; }
}
