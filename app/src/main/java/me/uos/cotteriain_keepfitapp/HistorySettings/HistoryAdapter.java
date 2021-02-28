package me.uos.cotteriain_keepfitapp.HistorySettings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryData> historyList;
    private HistoryClickListener historyClickListener;

    public HistoryAdapter(HistoryClickListener historyClickListener) {
        this.historyClickListener = historyClickListener;
    }

    public void setHistoryData(List<HistoryData> historyList){
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setHistoryData(historyList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView goalNameText;
        private TextView dateText;
        private TextView stepsTakenText;
        private TextView goalStepsText;
        private ProgressBar progressBar;
        private HistoryData historyData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goalNameText = (TextView)itemView.findViewById(R.id.name);
            dateText = (TextView)itemView.findViewById(R.id.date);
            stepsTakenText = (TextView)itemView.findViewById(R.id.steps_num);
            goalStepsText = (TextView)itemView.findViewById(R.id.goal_steps_num);
            progressBar = (ProgressBar) itemView.findViewById(R.id.completion_bar);
            progressBar.setProgress(0);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { historyClickListener.onHistoryClick(getAdapterPosition(), historyData); }
            });
        }

        private void setHistoryData(HistoryData historyData){
            this.historyData = historyData;
            goalNameText.setText(historyData.getGoalName());
            dateText.setText(historyData.getDate());
            stepsTakenText.setText(Integer.toString(historyData.getStepsTaken()));
            goalStepsText.setText(Integer.toString(historyData.getGoalSteps()));
            progressBar.setProgress(historyData.getPercentageToGoal());
        }

        private HistoryData getHistoryData(){ return historyData; }
    }

    public interface HistoryClickListener{
        void onHistoryClick(int itemIndex, HistoryData historyData);
    }
}
