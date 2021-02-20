package me.uos.cotteriain_keepfitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {

    private ImageView activity, history, settings;
    private RecyclerView historyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyView = (RecyclerView)findViewById(R.id.history_list);
        historyView.setLayoutManager(layoutManager);

        List<String> tempList = new ArrayList<>();
        tempList.add("Hello");
        tempList.add("Hello");
        tempList.add("Hello");
        tempList.add("Hello");
        tempList.add("Hello");
        tempList.add("Hello");

        HistoryAdapter adapter = new HistoryAdapter(tempList);
        historyView.setAdapter(adapter);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tabs);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton daily = (RadioButton) findViewById(R.id.daily);
                daily.setBackgroundResource(R.drawable.start_grey_tab);
                daily.setChecked(false);

                RadioButton weekly = (RadioButton) findViewById(R.id.weekly);
                weekly.setBackgroundResource(R.drawable.middle_grey_tab);
                weekly.setChecked(false);

                RadioButton monthly = (RadioButton) findViewById(R.id.monthly);
                monthly.setBackgroundResource(R.drawable.end_grey_tab);
                monthly.setChecked(false);

                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                switch (checkedButton.getId()) {
                    case R.id.daily:
                        checkedButton.setChecked(true);
                        checkedButton.setBackgroundResource(R.drawable.start_red_tab);
                        break;
                    case R.id.weekly:
                        checkedButton.setChecked(true);
                        checkedButton.setBackgroundResource(R.drawable.middle_red_tab);
                        break;
                    case R.id.monthly:
                        checkedButton.setChecked(true);
                        checkedButton.setBackgroundResource(R.drawable.end_red_tab);
                        break;
                    default:
                        break;
                }
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
            case R.id.navi_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        if(intent != null) {
            startActivity(intent);
            overridePendingTransition(inAnim, outAnim);
        }
    }
}
