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
import me.uos.cotteriain_keepfitapp.General.MyNotification;
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

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener {

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

    /**
     * The onCreate for the MainActivity is the launching point of the App
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPersistentData();
        assignActivityElements();
        setRecyclerView();
        viewModelSetup();
        setStepsEdit();
    }

    /**
     * onResume resets whether goals editable in RecyclerView - i.e. allow user to select edit if allowed in settings
     * - since there can only be single instance of this activity running then necessary to use onResume after returning from settings screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        boolean isGoalsEditable = sharedData.getBool(getString(R.string.setting_goals_editable), getResources().getBoolean(R.bool.default_goal_editable));
        goalAdapter.setEditable(isGoalsEditable);
    }

    /**
     * Internal data is set here - SharedPreferences, Database and a class to determine whether new day
     */
    private void setPersistentData(){
        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        Date calendar = Calendar.getInstance().getTime();
        dateSystem = new DateSystem(new SimpleDateFormat("dd/MM/yyyy").format(calendar),
                sharedData.getString(getString(R.string.activity_date), getString(R.string.activity_date)));
        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());
        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());
    }

    /**
     * Assigns XML components in layout here to objects of that type, as well as apply click listener to allow adding of goal
     */
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

    /**
     * Sets up RecyclerView with a LinearLayoutManager
     * - depending on phone orientation then layout will make scrolling horizontal or vertical
     */
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

    /**
     * Observes the goal LiveData list for changes and if so then make change to the UI display
     */
    private void viewModelSetup(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        GoalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        viewModel.getGoalList().observe(this, new Observer<List<GoalData>>() {
            @Override
            public void onChanged(List<GoalData> goalDataList) {
                /**
                 * list of names for all goals returned and active goal found
                 */
                int id = sharedData.getInt(getString(R.string.current_activity), getResources().getInteger(R.integer.default_activity));
                goalNames.clear();
                for (GoalData goal: goalDataList){
                    goalNames.add(goal.getName());
                    if(goal.getId() == id)
                        activeGoal = goal;
                }

                /**
                 * if dates of current and last active are the same then update UI of goal progression
                 * - if not then reset all data and record the current active goal values to the history database
                 */
                if(dateSystem.datesMatch())
                    setGoalProgressUI(false);
                else{
                    recordHistory();
                    id = resetActiveGoalData();
                }

                /**
                 * goalAdapter given new list and the active id - if reset then no goal will match the active goal
                 */
                goalAdapter.setGoalList(goalDataList, id);
                recyclerView.swapAdapter(goalAdapter, true);
            }
        });
    }

    /**
     * sets value of EditText in ProgressBar based on data saved in SharedPreferences
     * - if change occurs to text, while user is editing, then update UI of goal progression
     */
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
                setGoalProgressUI(true);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Make new history item to be added to history database using the previous active goal, number of steps recorded, and date of activity
     * then add to database using MyExecutor class that will perform operation off main thread
     * - user is sent notification of change and can travel to history screen if they tap on notification
     */
    private void recordHistory(){
        if(activeGoal!= null) {
            String goalName = activeGoal.getName();
            steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
            int goalSteps = activeGoal.getSteps();
            int percent = steps * 100 / goalSteps;
            HistoryData newHistoryData = new HistoryData(dateSystem.getOldDate(), goalName, steps, goalSteps, percent);

            if (sharedData.getBool(getString(R.string.setting_notifications), getResources().getBoolean(R.bool.default_notification)))
                new MyNotification(this);

            MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    historyDatabase.historyDAO().createHistory(newHistoryData);
                }
            });
        }
    }

    /**
     * resets persistent data of previously active day
     * @return - the new id of active goal is returned in ViewModel since id variable inside of observer is still active
     */
    private int resetActiveGoalData(){
        activeGoal = null;
        dateSystem.setOldDate(dateSystem.getCurrentDate());
        sharedData.setString(getString(R.string.activity_date), dateSystem.getCurrentDate());
        sharedData.setInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
        sharedData.setInt(getString(R.string.current_activity), getResources().getInteger(R.integer.default_activity));
        sharedData.setInt(getString(R.string.progress_achieved),  getResources().getInteger(R.integer.default_progress));
        return getResources().getInteger(R.integer.default_activity);
    }

    /**
     * Gets the the active goal data and steps recorded and displays progress to user
     * @param isNotified - whether to send notification of change to goal progression - only true in setStepsEdit and onGoalClick
     */
    private void setGoalProgressUI(boolean isNotified){
        if(activeGoal != null) {
            steps = sharedData.getInt(getString(R.string.current_steps), getResources().getInteger(R.integer.default_steps));
            int goalSteps = activeGoal.getSteps();
            int percent = steps * 100 / goalSteps;

            if(isNotified) {
                int progressAchieved = sharedData.getInt(getString(R.string.progress_achieved),  getResources().getInteger(R.integer.default_progress));
                progressAchieved = progressAchieved < 1 && percent >=25 ? 1 : progressAchieved;
                progressAchieved = progressAchieved < 2 && percent >=50 ? 2 : progressAchieved;
                progressAchieved = progressAchieved < 3 && percent >=75 ? 3 : progressAchieved;
                progressAchieved = progressAchieved < 4 && percent >=100 ? 4 : progressAchieved;

                boolean update = false;
                if(progressAchieved != sharedData.getInt(getString(R.string.progress_achieved),  getResources().getInteger(R.integer.default_progress))){
                    update = true;
                    sharedData.setInt(getString(R.string.progress_achieved),  progressAchieved);
                }

                if (update && sharedData.getBool(getString(R.string.setting_notifications), getResources().getBoolean(R.bool.default_notification)))
                    new MyNotification(this, "You have achieved " + percent + "% of your goal");
            }

            progressBar.setProgress(percent, true);
            headerText.setText(activeGoal.getName());
            headerPercent.setText(Integer.toString(percent) + "%");
            goalText.setText(Integer.toString(goalSteps));
        }
    }

    /**
     * AlertDialog for creating and editing goals - if editing then title of dialog changed as well as has EditTexts set to selected goals values
     * @param goal - if null then create new goal is true - only null when floating action button clicked
     */
    private void goalPopup(GoalData goal){
        boolean isCreating = goal == null;

        View popupLayout = getLayoutInflater().inflate(R.layout.goal_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

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

                /**
                 * error messages displayed to user if they enter unsuitable inputs
                 * - no input given or name of goal matches another
                 */
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

                /**
                 * if creating then on separate thread add new goal to database
                 * if editing then change selected goals data and pass it back into to database for previous data to be overwritten
                 */
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
                dialog.dismiss();
            }
        });
    }

    /**
     * Confirmation AlertDialog that will ask user to confirm deletion of goal
     * - deletion of selected goal happens off main thread
     * @param goal
     */
    private void deleteGoalPopup(GoalData goal) {
        View popupLayout = getLayoutInflater().inflate(R.layout.clear_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

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
                dialog.dismiss();
            }
        });
    }

    /**
     * creates menu in toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * @param item - item id used to determine which activity to load
     * @return
     */
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

    /**
     * set the new active goal, as well as save active goal id for future instances of load app
     * - goal passed to GoalAdapter, finds previous active (if exists) and new active and set ViewHolders with specific UI elements
     * @param itemIndex
     * @param goal
     */
    @Override
    public void onGoalClick(int itemIndex, GoalData goal){
        int id = goal.getId();
        activeGoal=goal;
        sharedData.setInt(getString(R.string.current_activity), id);

        sharedData.setInt(getString(R.string.progress_achieved),  getResources().getInteger(R.integer.default_progress));
        goalAdapter.setActiveGoal(itemIndex);
        setGoalProgressUI(true);
    }

    @Override
    public void onEditClick(GoalData goal) { goalPopup(goal); }
    @Override
    public void onDeleteClick(GoalData goal) { deleteGoalPopup(goal); }
}