package me.uos.cotteriain_keepfitapp.Goal;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal")
public class GoalData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int steps;

    /**
     * creates a new goal entry that can be supplied to goal database
     * @param name
     * @param steps
     */
    @Ignore
    public GoalData(String name, int steps) {
        this.name = name;
        this.steps = steps;
    }

    /**
     * creates a goal that is used exclusively by the goal database
     * @param id
     * @param name
     * @param steps
     */
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
