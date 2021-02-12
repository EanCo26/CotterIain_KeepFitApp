package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener{

    private final String TAG = "UI/" + MainActivity.class.getSimpleName();

    private LinearLayoutManager layoutManager;
    private RelativeLayout selectedGoal = null;
    private List<GoalItem> goalList = new ArrayList<>();

    RecyclerView recyclerView;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText goal_name, goal_steps;
    private Button create_button;

    private int goalId = Integer.MAX_VALUE;
    private int goalSteps = 0;

    private TextView header;
    private ProgressBar circleProgress;
    private TextView stepsText;
    private TextView goalStepsText;
    private SeekBar updateActivity;

    enum Screen{
        MAIN,
        HISTORY,
        SETTINGS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        goalList.add(new GoalItem("Morning", 20000));
        goalList.add(new GoalItem("Afternoon", 10000));
        goalList.add(new GoalItem("Night", 5000));
        goalList.add(new GoalItem("Early Morning", 500000000));

        GoalAdapter goalAdapter = new GoalAdapter(goalList, this);
        recyclerView.setAdapter(goalAdapter);

        header = (TextView)findViewById(R.id.header);
        circleProgress = (ProgressBar) findViewById(R.id.circle);
        stepsText = (TextView)findViewById(R.id.steps);
        goalStepsText = (TextView)findViewById(R.id.goal_number);
        updateActivity = (SeekBar) findViewById(R.id.update_activity);

        updateUI();

        updateActivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void createPopup(View view){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.create_popup, null);
        goal_name = (EditText) popup.findViewById(R.id.create_name);
        goal_steps = (EditText) popup.findViewById(R.id.create_steps);
        create_button = (Button) popup.findViewById(R.id.create_button);

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalList.add(new GoalItem(goal_name.getText().toString(), 10));
                GoalAdapter goalAdapter = new GoalAdapter(goalList);
                recyclerView.setAdapter(goalAdapter);
                dialog.dismiss();
            }
        });
    }

    public void editPopup(View view){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.edit_popup, null);
        goal_name = (EditText) popup.findViewById(R.id.edit_name);
        goal_steps = (EditText) popup.findViewById(R.id.edit_steps);
        create_button = (Button) popup.findViewById(R.id.edit_button);

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        create_button.setOnClickListener(new View.OnClickListener() {
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
            selectedGoal.setBackgroundResource(R.drawable.empty_border);
        }

        goalId = itemIndex;
        selectedGoal = (RelativeLayout)layoutManager.findViewByPosition(goalId);
        goalSteps = goalList.get(goalId).getSteps();
        selectedGoal.setBackgroundResource(R.drawable.selected_border);

        updateUI();
    }

    private void updateUI(){
        header.setText("Steps - " + updateActivity.getProgress() + "%");
        circleProgress.setProgress(updateActivity.getProgress());
        int count = Math.round((goalSteps /100f)*updateActivity.getProgress());
        stepsText.setText(Integer.toString(count));
        goalStepsText.setText(Integer.toString(goalSteps));
    }
}