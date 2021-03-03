package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;
import me.uos.cotteriain_keepfitapp.General.MyExecutor;
import me.uos.cotteriain_keepfitapp.General.PopupWindow;
import me.uos.cotteriain_keepfitapp.General.SharedData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "My/" + SettingsActivity.class.getSimpleName();

    //todo - do the same for push notification as goals editable

    private Switch goalSetting, historySetting;
    private Button clearHistoryButton;

    private HistoryDatabase historyDatabase;

    private SharedData sharedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        historyDatabase = HistoryDatabase.getsInstance(getApplicationContext());
        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));

        goalSetting = (Switch)findViewById(R.id.goals_setting);
        goalSetting.setChecked(sharedData.getBool(getString(R.string.setting_goals_editable), getResources().getBoolean(R.bool.default_goal_editable)));
        goalSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { sharedData.setBool(getString(R.string.setting_goals_editable), isChecked); }
        });

        historySetting = (Switch)findViewById(R.id.history_rec_setting);
        historySetting.setChecked(sharedData.getBool(getString(R.string.setting_history_editable), getResources().getBoolean(R.bool.default_history_editable)));
        historySetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { sharedData.setBool(getString(R.string.setting_history_editable), isChecked); }
        });

        clearHistoryButton = (Button)findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { clearHistory(); }
        });
    }

    private void clearHistory() {
        View popupLayout = getLayoutInflater().inflate(R.layout.clear_popup, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupLayout);
        PopupWindow popupWindow = new PopupWindow(dialogBuilder, dialogBuilder.create());
        popupWindow.showWindow();

        Button yesButton = (Button)popupLayout.findViewById(R.id.yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyExecutor.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        historyDatabase.historyDAO().clearHistoryTable();
                    }
                });
                popupWindow.closeWindow();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        MenuItem activity =  menu.findItem(R.id.activity);
        activity.setVisible(false);
        MenuItem history =  menu.findItem(R.id.history);
        history.setVisible(false);

        MenuItem settingsItem =  menu.findItem(R.id.settings);
        settingsItem.setIcon(R.drawable.baseline_settings_white_48);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        switch(itemID){
            case R.id.settings:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}