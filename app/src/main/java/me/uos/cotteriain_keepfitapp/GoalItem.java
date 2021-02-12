package me.uos.cotteriain_keepfitapp;

public class GoalItem {

    private String name;
    private int steps;

    public GoalItem(String name, int steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() { return name; }
    public int getSteps() { return steps; }
}
