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
import me.uos.cotteriain_keepfitapp.Goal.GoalAdapter;
import me.uos.cotteriain_keepfitapp.Goal.GoalData;
import me.uos.cotteriain_keepfitapp.Goal.GoalViewModel;
import me.uos.cotteriain_keepfitapp.History.HistoryData;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private final String TAG = "MyTag/" + this.getClass().getSimpleName();

    private DateSystem dateSystem;
    private SharedData sharedData;
    private GoalDatabase goalDatabase;
    private HistoryDatabase historyDatabase;

    private RecyclerView recyclerView;
    private GoalAdapter goalAdapter;
    final private GoalAdapter.GoalClickListener gCL = this;

    private GoalData activeGoal = null;
    private int steps = 0;

    private TextView goalText, headerText, headerPercent;
    private EditText stepsEdit;
    private ProgressBar progressBar;

    private List<String> goalNames = new ArrayList<>();
    
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
        boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), getResources().getBoolean(R.bool.default_goal_editable));
        goalAdapter.setEditable(isGoalsEditable);
    }

    private void setPersistentData(){
        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        Date calendar = Calendar.getInstance().getTime();
        dateSystem = new DateSystem(new SimpleDateFormat("dd/MM/yyyy").format(calendar),
                sharedData.getString(getString(R.string.activity_date), getString(R.string.activity_date)));
        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());
        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());
    }

    private void assignActivityElements(){
        headerText = (TextView) findViewById(R.id.header_name);
        headerPercent = (TextView) findViewById(R.id.header_percent);
        goalText = (TextView) findViewById(R.id.goal_number);
        stepsEdit = (EditText) findViewById(R.id.steps);
        steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
        stepsEdit.setText(Integer.toString(steps));

        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setProgress(0);

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_goal);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { goalPopup(null); }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
    }

    private void setRecyclerView(){
        boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), getResources().getBoolean(R.bool.default_goal_editable));
        goalAdapter = new GoalAdapter(gCL, isGoalsEditable);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        else{
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private void viewModelSetup(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        GoalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        viewModel.getGoalList().observe(this, new Observer<List<GoalData>>() {
            @Override
            public void onChanged(List<GoalData> goalDataList) {
                int id = sharedData.getInt(getString(R.string.current_activity), getResources().getInteger(R.integer.default_activity));
                goalNames.clear();
                for (GoalData goal: goalDataList){
                    goalNames.add(goal.getName());
                    if(goal.getId() == id)
                        activeGoal = goal;
                }

                if(dateSystem.datesMatch())
                    setGoalProgressUI();
                else{
                    recordHistory();
                    resetActiveGoalData();
                }

                id = sharedData.getInt(getString(R.string.current_activity), getResources().getInteger(R.integer.default_activity));
                goalAdapter.setGoalList(goalDataList, id);
                recyclerView.swapAdapter(goalAdapter, true);

                setStepsEdit();
            }
        });
    }

    private void setStepsEdit(){
        steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));

        stepsEdit.setText(Integer.toString(steps));
        stepsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { stepsEdit.setCursorVisible(true); }
        });

        stepsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(stepsEdit.hasFocus())
                {
                    String input = stepsEdit.getText().toString();

                    if (input.length() > 0) {
                        steps = Integer.parseInt(input);
                        sharedData.setInt(getString(R.string.current_steps), steps);
                    }
                    else{
                        stepsEdit.setText("0");
                        sharedData.setInt(getString(R.string.current_steps), 0);
                    }
                }
                stepsEdit.setCursorVisible(false);
                setGoalProgressUI();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void recordHistory(){
        if(activeGoal!= null) {
            String goalName = activeGoal.getName();
            steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
            int goalSteps = activeGoal.getSteps();
            int percent = steps * 100 / goalSteps;
            HistoryData newHistoryData = new HistoryData(dateSystem.getOldDate(), goalName, steps, goalSteps, percent);

            MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    historyDatabase.historyDAO().createHistory(newHistoryData);
                }
            });
        }
    }

    private void resetActiveGoalData(){
        dateSystem.setOldDate(dateSystem.getCurrentDate());
        sharedData.setString(getString(R.string.activity_date), dateSystem.getCurrentDate());
        sharedData.setInt(getString(R.string.current_steps), 0);
        sharedData.setInt(getString(R.string.current_activity), -1);
    }

    private void setGoalProgressUI(){
        if(activeGoal != null) {
            steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
            int goalSteps = activeGoal.getSteps();
            int percent = steps * 100 / goalSteps;

            progressBar.setProgress(percent);
            headerText.setText(activeGoal.getName());
            headerPercent.setText(Integer.toString(percent) + "%");
            goalText.setText(Integer.toString(goalSteps));
        }
    }

    private void goalPopup(GoalData goal){
        boolean isCreating = goal == null;

        View popupLayout = getLayoutInflater().inflate(R.layout.goal_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        EditText name_field = (EditText) popupLayout.findViewById(R.id.name);
        EditText goal_steps_field = (EditText) popupLayout.findViewById(R.id.steps);
        Button popup_button = (Button) popupLayout.findViewById(R.id.button);

        if(!isCreating){
            TextView title = (TextView) popupLayout.findViewById(R.id.title);
            title.setText("Edit Goal");

            name_field.setText(goal.getName());
            goal_steps_field.setText(Integer.toString(goal.getSteps()));

            popup_button.setText("Edit");
        }

        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name_field.getText().toString(),
                        goalStepsText = goal_steps_field.getText().toString();

                boolean isAllowed = true;
                String none = "Requires Input", match = "Names cannot Match";
                if(nameText.isEmpty()) { name_field.setError(none); isAllowed = false;}
                if(goalStepsText.isEmpty()){ goal_steps_field.setError(none); isAllowed = false; }
                if(goalNames.contains(nameText)){
                    if(isCreating){ name_field.setError(match); isAllowed = false; }
                    else if (!isCreating && !nameText.equals(goal.getName())){ name_field.setError(match); isAllowed = false; }
                }
                if(!isAllowed)
                    return;

                if(isCreating) {
                    MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            goalDatabase.goalDao().createGoal(new GoalData(nameText,
                                    Integer.parseInt(goalStepsText)));
                        }
                    });
                }else{
                    goal.setName(nameText);
                    goal.setSteps(Integer.parseInt(goalStepsText));
                    MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            goalDatabase.goalDao().editGoal(goal);
                        }
                    });
                }
                popupWindow.closeWindow();
            }
        });
    }

    private void deleteGoalPopup(GoalData goal) {
        View popupLayout = getLayoutInflater().inflate(R.layout.clear_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        TextView title = (TextView) popupLayout.findViewById(R.id.title);
        title.setText("Please Confirm Deleting of Goal");

        Button popup_button = (Button) popupLayout.findViewById(R.id.yes);
        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        goalDatabase.goalDao().deleteGoal(goal);
                    }
                });
                popupWindow.closeWindow();
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
        switch(itemID){
            case R.id.activity:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.history:
                intent = new Intent(this, HistoryActivity.class);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        if(!intent.getComponent().getClassName().equals(this.getClass().getName())){
            if(intent != null)
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGoalClick(int itemIndex, GoalData goal){
        int id = goal.getId();
        activeGoal=goal;
        sharedData.setInt(getString(R.string.current_activity), id);

        goalAdapter.setActiveGoal(itemIndex);
        setGoalProgressUI();
    }

    @Override
    public void onEditClick(GoalData goal) { goalPopup(goal); }
    @Override
    public void onDeleteClick(GoalData goal) { deleteGoalPopup(goal); }
}