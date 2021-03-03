package me.uos.cotteriain_keepfitapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import me.uos.cotteriain_keepfitapp.General.PopupWindow;
import me.uos.cotteriain_keepfitapp.General.SharedData;
import me.uos.cotteriain_keepfitapp.History.HistoryAdapter;
import me.uos.cotteriain_keepfitapp.History.HistoryData;
import me.uos.cotteriain_keepfitapp.History.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryClickListener {

    private final String TAG = "MyTag/" + SettingsActivity.class.getSimpleName();

    private SharedData sharedData;
    private HistoryDatabase historyDatabase;

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryAdapter.HistoryClickListener hCl = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());

        TextView dateText = (TextView)findViewById(R.id.date);
        Date calendar = Calendar.getInstance().getTime();
        dateText.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView)findViewById(R.id.history_list);
        recyclerView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(hCl, sharedData.getBool(getString(R.string.setting_history_editable), getResources().getBoolean(R.bool.default_history_editable)));

        setupViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        boolean isHistoryEditable = sharedData.getBool(getString(R.string.setting_history_editable), getResources().getBoolean(R.bool.default_history_editable));
        historyAdapter.setEditable(isHistoryEditable);
    }

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

    private void editHistoryItem(HistoryData historyData){
        View popupLayout = getLayoutInflater().inflate(R.layout.history_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        EditText name_field = (EditText)popupLayout.findViewById(R.id.edit_name);
        name_field.setText(historyData.getGoalName());

        EditText steps_field = (EditText)popupLayout.findViewById(R.id.edit_steps);
        steps_field.setText(Integer.toString(historyData.getStepsTaken()));

        EditText goal_steps_field = (EditText)popupLayout.findViewById(R.id.edit_goal_steps);
        goal_steps_field.setText(Integer.toString(historyData.getGoalSteps()));

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
                if(stepsText.isEmpty())
                    steps_field.setError(none);
                if(goalStepsText.isEmpty())
                    goal_steps_field.setError(none);
                if(nameText.isEmpty() || stepsText.isEmpty() || goalStepsText.isEmpty())
                    return;

                int iSteps = Integer.parseInt(stepsText),
                        iGoalSteps = Integer.parseInt(goalStepsText);
                historyData.setGoalName(nameText);
                historyData.setStepsTaken(iSteps);
                historyData.setGoalSteps(iGoalSteps);
                int percent = iSteps * 100 / iGoalSteps;
                historyData.setPercentageToGoal(percent);

                MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() { historyDatabase.historyDAO().editHistory(historyData); }
                });
                popupWindow.closeWindow();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.toolbar, menu);return true; }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        Intent intent = null;
        switch(itemID){
            case R.id.activity:
//                finish();
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
}
