package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.Database.GoalDatabase;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;
import me.uos.cotteriain_keepfitapp.General.DateSystem;
import me.uos.cotteriain_keepfitapp.General.MyExecutor;
import me.uos.cotteriain_keepfitapp.General.PopupWindow;
import me.uos.cotteriain_keepfitapp.General.SharedData;
import me.uos.cotteriain_keepfitapp.GoalSettings.GoalAdapter;
import me.uos.cotteriain_keepfitapp.GoalSettings.GoalData;
import me.uos.cotteriain_keepfitapp.GoalSettings.GoalViewModel;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryData;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener{

    private final String TAG = "My/" + MainActivity.class.getSimpleName();

    private DateSystem dateSystem;
    private SharedData sharedData;
    private GoalDatabase goalDatabase;
    private HistoryDatabase historyDatabase;

    private RecyclerView recyclerView;
    private GoalAdapter goalAdapter;
    final private GoalAdapter.GoalClickListener gCL = this;

    private GoalData activeGoal = null;
    private int steps = 0;

    private TextView goalText, headerText;
    private EditText stepsEdit;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPersistentData();
        assignActivityElements();
        setRecyclerView();
        viewModelSetup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), true);
        goalAdapter.setEditable(isGoalsEditable);
    }

    private void assignActivityElements(){
        headerText = (TextView) findViewById(R.id.header);
        goalText = (TextView) findViewById(R.id.goal_number);
        stepsEdit = (EditText) findViewById(R.id.steps);
        steps = sharedData.getInt(getString(R.string.current_steps), 0);
        stepsEdit.setText(Integer.toString(steps));

        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setProgress(0);

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_goal);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createGoalPopup(); }
        });
    }

    private void setStepsEdit(){
        //todo error appeared when number was exceedinly high - (java.lang.NumberFormatException: For input string: "6666000000")
        steps = sharedData.getInt(getString(R.string.current_steps), 0);
        stepsEdit.setText(Integer.toString(steps));
        stepsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                int stepsChanged = 0;
                if(stepsEdit.length()>0)
                    stepsChanged = Integer.parseInt(stepsEdit.getText().toString());
                changeToSteps(stepsChanged);
            }
        });
    }

    private void setPersistentData(){
        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        Date calendar = Calendar.getInstance().getTime();
        dateSystem = new DateSystem(new SimpleDateFormat("dd/MM/yyyy").format(calendar), sharedData.getString(getString(R.string.activity_date), "no_date"));
        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());
        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());
    }

    private void setRecyclerView(){
        boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), true);
        goalAdapter = new GoalAdapter(gCL, isGoalsEditable);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void viewModelSetup(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        GoalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        viewModel.getGoalList().observe(this, new Observer<List<GoalData>>() {
            @Override
            public void onChanged(List<GoalData> goalDataList) {
                int id = sharedData.getInt(getString(R.string.current_activity), Integer.MAX_VALUE);

                int activeGoalIndex = searchGoalList(goalDataList, id);
                if(activeGoalIndex != -1){
                    activeGoal = goalDataList.get(activeGoalIndex);
                }

                if(dateSystem.datesMatch())
                {
                    setGoalProgressUI();
                }
                else{
                    recordHistory();
                    resetActiveGoalData();
                }
                id = sharedData.getInt(getString(R.string.current_activity), Integer.MAX_VALUE);
                goalAdapter.setGoalList(goalDataList, id);
                recyclerView.swapAdapter(goalAdapter, true);

                setStepsEdit();
            }
        });
    }

    private int searchGoalList(List<GoalData> goalDataList, int id){
        int leftmost = 0;
        int rightmost = goalDataList.size()-1;

        while (leftmost <= rightmost){
            int midpoint = leftmost + ((rightmost - leftmost) /2);
            if (goalDataList.get(midpoint).getId() == id){
                return midpoint;
            }
            else if(id < goalDataList.get(midpoint).getId()){
                rightmost = midpoint -1;
            }
            else if(id > goalDataList.get(midpoint).getId()){
                leftmost = midpoint + 1;
            }
        }
        return -1;
    }

    private void resetActiveGoalData(){
        dateSystem.setOldDate(dateSystem.getCurrentDate());
        sharedData.setString(getString(R.string.activity_date), dateSystem.getCurrentDate());
        sharedData.setInt(getString(R.string.current_steps), 0);
        sharedData.setInt(getString(R.string.current_activity), Integer.MAX_VALUE);
    }

    private void recordHistory(){
        if(activeGoal!= null) {
            String goalName = activeGoal.getName();
            steps = sharedData.getInt(getString(R.string.current_steps), 0);
            int goalSteps = activeGoal.getSteps();
            int percent = (steps * 100 / goalSteps <= 100) ? steps * 100 / goalSteps : 100;
            HistoryData newHistoryData = new HistoryData(dateSystem.getOldDate(), goalName, steps, goalSteps, percent);

            MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    historyDatabase.historyDAO().createHistory(newHistoryData);
                }
            });
        }
    }

    private void setGoalProgressUI(){
        if(activeGoal != null) {
            steps = sharedData.getInt(getString(R.string.current_steps), 0);
            int goalSteps = activeGoal.getSteps();;
//            int percent = (steps * 100 / goalSteps <= 100)? steps * 100 / goalSteps : 100;
            int percent = steps * 100 / goalSteps;

            progressBar.setProgress(percent);
            headerText.setText(activeGoal.getName() + " - " + percent + "%");
            goalText.setText(Integer.toString(goalSteps));
        }
    }

    private void changeToSteps(int stepsChanged){
        steps = stepsChanged;
        sharedData.setInt(getString(R.string.current_steps), steps);
        setGoalProgressUI();
    }

    private void createGoalPopup(){
        View popupLayout = getLayoutInflater().inflate(R.layout.create_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        EditText name_field = (EditText) popupLayout.findViewById(R.id.create_name);
        EditText steps_field = (EditText) popupLayout.findViewById(R.id.create_steps);
        Button popup_button = (Button) popupLayout.findViewById(R.id.create_button);
        popup_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean hasName = !name_field.getText().toString().isEmpty(),
                            hasSteps = !steps_field.getText().toString().isEmpty();
                    if(hasName&&hasSteps){
                        MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                goalDatabase.goalDao().createGoal(new GoalData(name_field.getText().toString(),
                                                Integer.parseInt(steps_field.getText().toString())));
                            }
                        });
                        popupWindow.closeWindow();
                    }
                }
        });
    }

    private void editGoalPopup(int itemIndex, GoalData goal){
        View popupLayout = getLayoutInflater().inflate(R.layout.edit_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        EditText name_field = (EditText) popupLayout.findViewById(R.id.edit_name);
        name_field.setText(goal.getName());

        EditText steps_field = (EditText) popupLayout.findViewById(R.id.edit_steps);
        steps_field.setText(Integer.toString(goal.getSteps()));

        Button popup_button = (Button) popupLayout.findViewById(R.id.edit_button);
        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean hasName = !name_field.getText().toString().isEmpty(),
                        hasSteps = !steps_field.getText().toString().isEmpty();

                if (hasName && hasSteps) {
                    goal.setName(name_field.getText().toString());
                    goal.setSteps(Integer.parseInt(steps_field.getText().toString()));
                    MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            goalDatabase.goalDao().editGoal(goal);
                        }
                    });
                    popupWindow.closeWindow();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        Intent intent = null;
        int inAnim = android.R.anim.slide_in_left;
        int outAnim = android.R.anim.slide_out_right;
        switch(itemID){
            case R.id.history:
                intent = new Intent(this, HistoryActivity.class);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        if(intent != null) {
            startActivity(intent);
            overridePendingTransition(inAnim, outAnim);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGoalClick(int itemIndex, GoalData goal){
        int id = goal.getId();
        sharedData.setInt(getString(R.string.current_activity), id);
        activeGoal=goal;
        goalAdapter.setActiveGoal(itemIndex);
        setGoalProgressUI();
    }

    @Override
    public void onEditClick(int itemIndex, GoalData goal) {
        editGoalPopup(itemIndex, goal);
    }

    @Override
    public void onDeleteClick(int itemIndex, GoalData goal) {
        MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                goalDatabase.goalDao().deleteGoal(goal);
            }
        });
    }
}