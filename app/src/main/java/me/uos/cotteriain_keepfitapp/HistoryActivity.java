package me.uos.cotteriain_keepfitapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryAdapter;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryData;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryClickListener {

    private final String TAG = "My/" + SettingsActivity.class.getSimpleName();

    private SharedData sharedData;
    private HistoryDatabase historyDatabase;

    private List<String> historyNames = new ArrayList<>();

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
        historyAdapter.setEditable(sharedData.getBool(getString(R.string.setting_history_editable), getResources().getBoolean(R.bool.default_history_editable)));
    }

    private void setupViewModel(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        HistoryViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(HistoryViewModel.class);
        viewModel.getHistoryList().observe(this, new Observer<List<HistoryData>>() {
            @Override
            public void onChanged(List<HistoryData> historyDataList) {
                for (HistoryData historyData: historyDataList) {
                    historyNames.add(historyData.getGoalName());
                }
                historyAdapter.setHistoryList(historyDataList);
                recyclerView.swapAdapter(historyAdapter, true);
            }
        });
    }

//    public void changeActivity(View view){
//        Intent intent = null;
//        int inAnim = android.R.anim.slide_in_left;
//        int outAnim = android.R.anim.slide_out_right;
//        switch(view.getId()){
//            case R.id.navi_activity:
//                intent = new Intent(this, MainActivity.class);
//                break;
//            case R.id.navi_settings:
//                intent = new Intent(this, SettingsActivity.class);
//                break;
//        }
//        if(intent != null) {
//            startActivity(intent);
//            overridePendingTransition(inAnim, outAnim);
//        }
//    }

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

                if(nameText.isEmpty())
                    name_field.setError("Name needs input");
                if(stepsText.isEmpty())
                    steps_field.setError("Steps needs input");
                if(goalStepsText.isEmpty())
                    goal_steps_field.setError("Goal needs input");
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
    public void onHistoryClick(HistoryData historyData) { editHistoryItem(historyData); }
}
