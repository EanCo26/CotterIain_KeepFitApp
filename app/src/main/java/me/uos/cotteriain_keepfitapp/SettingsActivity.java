package me.uos.cotteriain_keepfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "UI/" + SettingsActivity.class.getSimpleName();

    private boolean isGoalsEditable;
    private Switch goalSetting;

    //todo - do the same for push notification as goals editable

    //todo - allow user to press button and dialog appears to comfirm clear history

    private SharedData sharedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));
        goalSetting = (Switch)findViewById(R.id.goals_setting);

        isGoalsEditable = sharedData.getData(getString(R.string.setting_goals_editable), goalSetting.isChecked());
        goalSetting.setChecked(isGoalsEditable);
        goalSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedData.setData(getString(R.string.setting_goals_editable), isChecked);
            }
        });
    }

    public void changeActivity(View view){
        Intent intent = null;
        int inAnim = android.R.anim.slide_in_left;
        int outAnim = android.R.anim.slide_out_right;
        switch(view.getId()){
            case R.id.navi_activity:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.navi_history:
                intent = new Intent(this, HistoryActivity.class);
                break;
        }
        if(intent != null) {
            startActivity(intent);
            overridePendingTransition(inAnim, outAnim);
        }
    }
}