package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.database.GoalDatabase;
import me.uos.cotteriain_keepfitapp.database.GoalEntry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalClickListener{

    private final String TAG = "UI/" + MainActivity.class.getSimpleName();

    private SharedData sharedData;

    private GoalDatabase goalDatabase;
    private GoalAdapter goalAdapter;
    final private GoalAdapter.GoalClickListener gCL = this;

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;

    private ImageView activity, history, settings;
    private FloatingActionButton floatingActionButton;

    private TextView goalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assignActivityElements();

        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        goalDatabase = GoalDatabase.getsInstance(getApplicationContext());

        goalAdapter = new GoalAdapter(gCL);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        viewModelSetup();
    }

    private void assignActivityElements(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_goal);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup();
            }
        });

        goalText = (TextView) findViewById(R.id.goal_number);
    }

    private void viewModelSetup(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        GoalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        viewModel.getGoalList().observe(this, new Observer<List<GoalEntry>>() {
            @Override
            public void onChanged(List<GoalEntry> goalEntryList) {
                int id = sharedData.getData(getString(R.string.current_activity), Integer.MAX_VALUE);
                goalAdapter.setGoalList(goalEntryList, id);
                recyclerView.swapAdapter(goalAdapter, true);

                for (GoalEntry goal:goalEntryList){
                    if(goal.getId() == id){
                        setActiveGoalUI(goal);
                        return;
                    }
                }
            }
        });
    }

    public void setActiveGoalUI(GoalEntry goal){
        goalText.setText(Integer.toString(goal.getSteps()));
    }

    private void createPopup(){
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
                                goalDatabase.goalDao().createGoal(new GoalEntry(name_field.getText().toString(),
                                                Integer.parseInt(steps_field.getText().toString())));
                            }
                        });

                        popupWindow.closeWindow();
                    }
                }
        });
    }

    private void editPopup(int itemIndex, GoalEntry goal){
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
                Boolean hasName= false, hasSteps = false;
                if(!name_field.getText().toString().isEmpty()){
                    hasName = true;
                }
                if(!steps_field.getText().toString().isEmpty()){
                    hasSteps = true;
                }

                if(hasName&&hasSteps) {
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
                popupWindow.closeWindow();
            }
        });
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
    public void onGoalClick(int itemIndex, GoalEntry goal){
        int id = goal.getId();
        sharedData.setData(getString(R.string.current_activity), id);
        goalAdapter.selectGoal(itemIndex);
        setActiveGoalUI(goal);
    }

    @Override
    public void onEditClick(int itemIndex, GoalEntry goal) {
        editPopup(itemIndex, goal);
    }
}