package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.database.GoalDatabase;
import me.uos.cotteriain_keepfitapp.database.GoalEntry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener{

    private final String TAG = "UI/" + MainActivity.class.getSimpleName();

    private LinearLayoutManager layoutManager;
    private ConstraintLayout selectedGoal = null;
    private List<GoalEntry> goalList = new ArrayList<>();
    private RecyclerView recyclerView;

    private FloatingActionButton floatingActionButton;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText goal_name, goal_steps;
    private Button popup_button;

    private GoalDatabase goalDatabase;

    private int goalId = Integer.MAX_VALUE;
    private int goalSteps = 0;

    private TextView header, stepsText, goalStepsText;
    private ProgressBar circleProgress;
    private SeekBar updateActivity;

    final private GoalAdapter.GoalClickListener goalClickListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assignActivityElements();

        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        goalList = goalDatabase.goalDao().loadAllGoals();
        GoalAdapter goalAdapter = new GoalAdapter(goalList, goalClickListener);
        recyclerView.setAdapter(goalAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup(v);
            }
        });

        updateActivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                updateUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void assignActivityElements(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_goal);

        header = (TextView)findViewById(R.id.header);
        circleProgress = (ProgressBar) findViewById(R.id.circle);
        stepsText = (TextView)findViewById(R.id.steps);
        goalStepsText = (TextView)findViewById(R.id.goal_number);
        updateActivity = (SeekBar) findViewById(R.id.update_activity);
    }

    public void createPopup(View view){
        dialogBuilder = new AlertDialog.Builder(this);
        View popup = getLayoutInflater().inflate(R.layout.create_popup, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        goal_name = (EditText) popup.findViewById(R.id.create_name);
        goal_steps = (EditText) popup.findViewById(R.id.create_steps);
        popup_button = (Button) popup.findViewById(R.id.create_button);
        popup_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(goal_name.getText().toString()) && !TextUtils.isEmpty(goal_steps.getText().toString())) {
                        String name = goal_name.getText().toString();
                        Integer steps = Integer.parseInt(goal_steps.getText().toString());
                        GoalEntry goalEntry = new GoalEntry(name, steps);

                        goalDatabase.goalDao().createGoal(goalEntry);
                        goalList = goalDatabase.goalDao().loadAllGoals();
                        GoalAdapter goalAdapter = new GoalAdapter(goalList, goalClickListener);
                        recyclerView.setAdapter(goalAdapter);
                        dialog.dismiss();
                    }
                }
        });
    }

    public void editPopup(View view){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.edit_popup, null);
        goal_name = (EditText) popup.findViewById(R.id.edit_name);
        goal_steps = (EditText) popup.findViewById(R.id.edit_steps);
        popup_button = (Button) popup.findViewById(R.id.edit_button);

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        Intent intent = null;
        int inAnim = android.R.anim.slide_in_left;
        int outAnim = android.R.anim.slide_out_right;

        switch (itemID){
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
    public void onItemClick(int itemIndex){
        if(goalId != Integer.MAX_VALUE) {
            if (goalId == itemIndex) {
                Log.d(TAG, "The Goal Item was not updated");
                return;
            }
//            goalList.get(goalId).setActive(false);
            selectedGoal.setBackgroundResource(R.drawable.empty_border);
        }
        goalId = itemIndex;
//        goalList.get(goalId).setActive(true);

        setActiveGoalUI();
    }

    private void setActiveGoalUI(){
        selectedGoal = (ConstraintLayout) layoutManager.findViewByPosition(goalId);
        goalSteps = goalList.get(goalId).getSteps();
        selectedGoal.setBackgroundResource(R.drawable.selected_border);
    }

//    private void updateUI(){
//        header.setText("Steps - " + updateActivity.getProgress() + "%");
//        circleProgress.setProgress(updateActivity.getProgress());
//        int count = Math.round((goalSteps /100f)*updateActivity.getProgress());
//        stepsText.setText(Integer.toString(count));
//        goalStepsText.setText(Integer.toString(goalSteps));
//    }
}