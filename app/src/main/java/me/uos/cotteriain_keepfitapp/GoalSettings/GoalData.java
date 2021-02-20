package me.uos.cotteriain_keepfitapp.GoalSettings;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal")
public class GoalData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int steps;

    @Ignore
    public GoalData(String name, int steps) {
        this.name = name;
        this.steps = steps;
    }

    public GoalData(int id, String name, int steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public int getSteps() { return steps; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSteps(int steps) { this.steps = steps; }
}