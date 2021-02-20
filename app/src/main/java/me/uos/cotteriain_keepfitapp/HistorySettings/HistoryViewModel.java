package me.uos.cotteriain_keepfitapp.HistorySettings;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<List<HistoryData>> historyList;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        HistoryDatabase historyDatabase = HistoryDatabase.getsInstance(this.getApplication());
        historyList = historyDatabase.historyDAO().loadAllHistory();
    }

    public LiveData<List<HistoryData>> getHistoryList() { return historyList; }
}
