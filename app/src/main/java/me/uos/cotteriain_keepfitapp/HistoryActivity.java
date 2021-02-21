package me.uos.cotteriain_keepfitapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.General.SharedData;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryAdapter;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryData;
import me.uos.cotteriain_keepfitapp.HistorySettings.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryClickListener {

    private final String TAG = "My/" + SettingsActivity.class.getSimpleName();

    private SharedData sharedData;

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryAdapter.HistoryClickListener hCl = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedData = new SharedData(this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE));

        TextView dateText = (TextView)findViewById(R.id.date);
        Date calendar = Calendar.getInstance().getTime();
        dateText.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView)findViewById(R.id.history_list);
        recyclerView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(hCl);

        setupViewModel();
    }

    private void setupViewModel(){
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        HistoryViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(HistoryViewModel.class);
        viewModel.getHistoryList().observe(this, new Observer<List<HistoryData>>() {
            @Override
            public void onChanged(List<HistoryData> historyDataList) {
                historyAdapter.setHistoryData(historyDataList);
                recyclerView.swapAdapter(historyAdapter, true);
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

    private void editHistoryItem(int itemIndex, HistoryData historyData){
        Log.d(TAG, "onHistoryClick: " + historyData.getGoalName());
    }

    @Override
    public void onHistoryClick(int itemIndex, HistoryData historyData) {
        Boolean isHistoryEditable = sharedData.getBool(getString(R.string.setting_history_editable), true);
        if(isHistoryEditable){
            editHistoryItem(itemIndex, historyData);
        }
    }
}
