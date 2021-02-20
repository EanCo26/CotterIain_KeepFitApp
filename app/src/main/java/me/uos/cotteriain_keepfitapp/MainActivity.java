package me.uos.cotteriain_keepfitapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.Database.GoalDatabase;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener{

    private final String TAG = "My/" + MainActivity.class.getSimpleName();

    private SharedData sharedData;
    private GoalDatabase goalDatabase;
    private HistoryDatabase historyDatabase;
    private GoalAdapter goalAdapter;
    final private GoalAdapter.GoalClickListener gCL = this;

    private RecyclerView recyclerView;

    private GoalData activeGoal = null;
    private int goalSteps = 0;
    private int steps = 0;

    private TextView goalText, headerText, stepsText;
    private ProgressBar progressBar;

    private String currentDate;
    private String oldDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assignActivityElements();

        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());
        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());

        Date calendar = Calendar.getInstance().getTime();
        currentDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar);
        oldDate = sharedData.getString(getString(R.string.activity_date), "no_date");
        oldDate = "Debug Date";

        if(!currentDate.equals(oldDate)){
            clearPreviousDayData();
        }

        goalAdapter = new GoalAdapter(gCL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        viewModelSetup();
    }

    private void assignActivityElements(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        goalText = (TextView) findViewById(R.id.goal_number);
        headerText = (TextView) findViewById(R.id.header);
        stepsText = (TextView) findViewById(R.id.steps);

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_goal);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGoalPopup();
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addStepsPopup(); }
        });
    }

    private void viewModelSetup(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        GoalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        viewModel.getGoalList().observe(this, new Observer<List<GoalData>>() {
            @Override
            public void onChanged(List<GoalData> goalDataList) {
                int id = sharedData.getInt(getString(R.string.current_activity), Integer.MAX_VALUE);
                goalAdapter.setGoalList(goalDataList, id);
                recyclerView.swapAdapter(goalAdapter, true);

                for (GoalData goal: goalDataList){
                    if(goal.getId() == id){
                        activeGoal = goal;
                        setActiveGoalUI();
                        return;
                    }
                }
            }
        });
    }

    private void setActiveGoalUI(){
        steps = sharedData.getInt(getString(R.string.current_steps), 0);
        stepsText.setText(Integer.toString(steps));
        if(activeGoal != null) {
            goalSteps = activeGoal.getSteps();
            int percent = steps * 100 / goalSteps;
            percent = percent <= 100 ? percent : 100;

            progressBar.setProgress(percent);
            headerText.setText(activeGoal.getName() + " - " + percent + "%");
            goalText.setText(Integer.toString(goalSteps));
        }
    }

    private void clearPreviousDayData(){
        if(!oldDate.equals("no_date")) {
            MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    historyDatabase.historyDAO().createHistory(new HistoryData(oldDate,
                            "Placeholder", 50, 100, 50));
                }
            });
        }

        sharedData.setInt(getString(R.string.current_steps), 0);
        sharedData.setInt(getString(R.string.current_activity), Integer.MAX_VALUE);
    }

    private void resetDisplay(){
        progressBar.setProgress(0);
        goalText.setText("0");
        headerText.setText("");
        stepsText.setText("0");
    }

    private void addStepsPopup() {
        View popupLayout = getLayoutInflater().inflate(R.layout.edit_active_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        TextView title = (TextView) popupLayout.findViewById(R.id.title);
        if(activeGoal != null) { title.setText(activeGoal.getName()); }
        EditText add_field = (EditText) popupLayout.findViewById(R.id.add_steps);
        Button popup_button = (Button) popupLayout.findViewById(R.id.add_button);
        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps = sharedData.getInt(getString(R.string.current_steps), 0);
                if(!add_field.getText().toString().isEmpty()) {
                    if(!currentDate.equals(oldDate)){
                        sharedData.setString(getString(R.string.activity_date), currentDate);
                    }
                    steps += Integer.parseInt(add_field.getText().toString());
                    sharedData.setInt(getString(R.string.current_steps), steps);
                    setActiveGoalUI();
                }
                popupWindow.closeWindow();
            }
        });
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
                    Boolean hasName= false, hasSteps = false;
                    if(!name_field.getText().toString().isEmpty()){
                        hasName = true;
                    }
                    if(!steps_field.getText().toString().isEmpty()){
                        hasSteps = true;
                    }
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
        if(activeGoal != goal) {
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

            boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), true);
            if (isGoalsEditable) {
                popup_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean hasName = false, hasSteps = false;
                        if (!name_field.getText().toString().isEmpty()) {
                            hasName = true;
                        }
                        if (!steps_field.getText().toString().isEmpty()) {
                            hasSteps = true;
                        }

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
            } else {
                TextView helper_msg = (TextView) popupLayout.findViewById(R.id.helper_text);
                helper_msg.setText("Make goals to editable in Settings");

                popup_button.setEnabled(false);
                popup_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.goal_grey)));

                name_field.setEnabled(false);
                name_field.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.goal_grey)));

                steps_field.setEnabled(false);
                steps_field.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.goal_grey)));
            }

            TextView delete_goal = (TextView) popupLayout.findViewById(R.id.delete);
            delete_goal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            goalDatabase.goalDao().deleteGoal(goalAdapter.getGoalEntryAt(itemIndex));
                        }
                    });
                    //todo check if necessary since user is not allowed to edit or delete active goals
//                    if (goal.getId() == activeGoal.getId()) {
//                        activeGoal = null;
//                        resetDisplay();
//                    }
                    popupWindow.closeWindow();
                }
            });
        }
    }

    public void changeActivity(View view){
        Intent intent = null;
        int inAnim = android.R.anim.slide_in_left;
        int outAnim = android.R.anim.slide_out_right;
        switch(view.getId()){
            case R.id.navi_history:
                intent = new Intent(this, HistoryActivity.class);
                break;
            case R.id.navi_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        if(intent != null) {
            startActivity(intent);
            overridePendingTransition(inAnim, outAnim);
        }
    }

    @Override
    public void onGoalClick(int itemIndex, GoalData goal){
        if(!currentDate.equals(oldDate)){
            sharedData.setString(getString(R.string.activity_date), currentDate);
        }

        int id = goal.getId();
        sharedData.setInt(getString(R.string.current_activity), id);
        goalAdapter.selectGoal(itemIndex);
        activeGoal=goal;
        setActiveGoalUI();
    }

    @Override
    public void onEditClick(int itemIndex, GoalData goal) {
        editGoalPopup(itemIndex, goal);
    }
}