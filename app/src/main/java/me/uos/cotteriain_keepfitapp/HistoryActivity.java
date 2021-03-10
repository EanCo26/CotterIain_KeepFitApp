package me.uos.cotteriain_keepfitapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;
import me.uos.cotteriain_keepfitapp.General.MyExecutor;
import me.uos.cotteriain_keepfitapp.History.HistoryAdapter;
import me.uos.cotteriain_keepfitapp.History.HistoryData;
import me.uos.cotteriain_keepfitapp.History.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "MyTag/" + SettingsActivity.class.getSimpleName();

    private SharedPreferences settingsPref;
    private HistoryDatabase historyDatabase;

    private boolean isHistoryRecordable;

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryAdapter.HistoryClickListener hCl = this;

    private FloatingActionButton floatingActionButton;

    /**
     * gets current Date and RecyclerView to display to user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        settingsPref = PreferenceManager.getDefaultSharedPreferences(this);
        settingsPref.registerOnSharedPreferenceChangeListener(this);

        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());

        TextView dateText = (TextView)findViewById(R.id.date);
        Date calendar = Calendar.getInstance().getTime();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar);
        dateText.setText(date);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView)findViewById(R.id.history_list);
        recyclerView.setLayoutManager(layoutManager);

        isHistoryRecordable = settingsPref.getBoolean(getString(R.string.setting_history_recordable),
                getResources().getBoolean(R.bool.default_history_recordable));
        historyAdapter = new HistoryAdapter(hCl, isHistoryRecordable);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_history);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() { historyDatabase.historyDAO().createHistory(new HistoryData(date, "New", 0, 0, 0)); }
                });
            }
        });
        if(!isHistoryRecordable)
            floatingActionButton.setVisibility(View.INVISIBLE);

        setupViewModel();
    }

    /**
     * If observed change to LiveData of list of history items in database then change views in Adapter
     */
    private void setupViewModel(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        HistoryViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(HistoryViewModel.class);
        viewModel.getHistoryList().observe(this, new Observer<List<HistoryData>>() {
            @Override
            public void onChanged(List<HistoryData> historyDataList) {
                historyAdapter.setHistoryList(historyDataList);
                recyclerView.swapAdapter(historyAdapter, true);
            }
        });
    }

    /**
     * AlertDialog to edit history element appears when edit icon pressed
     * - set all fields with history data and if no mistakes in input then edit history item in database
     * @param historyData
     */
    private void editHistoryItem(HistoryData historyData){
        View popupLayout = getLayoutInflater().inflate(R.layout.history_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        EditText name_field = (EditText)popupLayout.findViewById(R.id.edit_name);
        name_field.setText(historyData.getGoalName());

        EditText goal_steps_field = (EditText)popupLayout.findViewById(R.id.edit_goal_steps);
        goal_steps_field.setText(Integer.toString(historyData.getGoalSteps()));

        EditText steps_field = (EditText)popupLayout.findViewById(R.id.edit_steps);

        Button edit_button = (Button)popupLayout.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name_field.getText().toString(),
                    stepsText = steps_field.getText().toString(),
                    goalStepsText = goal_steps_field.getText().toString();

                String none = "Requires Input";
                if(nameText.isEmpty())
                    name_field.setError(none);
                if(goalStepsText.isEmpty())
                    goal_steps_field.setError(none);
                if(nameText.isEmpty() || goalStepsText.isEmpty())
                    return;

                int stepsTaken = historyData.getStepsTaken();
                if(!stepsText.isEmpty()){
                    stepsTaken += Integer.parseInt(stepsText);
                }
                int goalSteps = Integer.parseInt(goalStepsText);
                historyData.setGoalName(nameText);
                historyData.setStepsTaken(stepsTaken);
                historyData.setGoalSteps(goalSteps);
                int percent = goalSteps > 0 ? stepsTaken * 100 / goalSteps : 0;
                historyData.setPercentageToGoal(percent);

                MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() { historyDatabase.historyDAO().editHistory(historyData); }
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
    public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.toolbar, menu);return true; }

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
        if(intent != null) {
            if (!intent.getComponent().getClassName().equals(this.getClass().getName())) {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHistoryClick(HistoryData historyData) { editHistoryItem(historyData); }

    /**
     * if change occurs in preference fragment of settings activity
     * @param sharedPreferences
     * @param key - the setting to evaluate if allowed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.setting_history_recordable))){
            isHistoryRecordable = sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.default_history_recordable));
            historyAdapter.setEditable(isHistoryRecordable);
            if(!isHistoryRecordable)
                floatingActionButton.setVisibility(View.INVISIBLE);
            else
                floatingActionButton.setVisibility(View.VISIBLE);
        }
    }
}
