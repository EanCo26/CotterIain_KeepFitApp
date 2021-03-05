package me.uos.cotteriain_keepfitapp.History;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.uos.cotteriain_keepfitapp.Database.HistoryDatabase;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<List<HistoryData>> historyList;

    /**
     * HistoryViewModel allows the HistoryActivity to observe changes in HistoryDatabase
     * @param application
     */
    public HistoryViewModel(@NonNull Application application) {
        super(application);

        HistoryDatabase historyDatabase = HistoryDatabase.getsInstance(this.getApplication());
        historyList = historyDatabase.historyDAO().loadAllHistory();
    }

    public LiveData<List<HistoryData>> getHistoryList() { return historyList; }
}
