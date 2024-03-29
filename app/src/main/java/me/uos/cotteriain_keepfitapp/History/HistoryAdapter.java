package me.uos.cotteriain_keepfitapp.History;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryData> historyList;
    private HistoryClickListener historyClickListener;

    private boolean editable = true;

    /**
     *
     * @param historyClickListener - supplied historyClickListener initialized by HistoryActivity
     * @param editable - whether history are set to editable in settings
     */
    public HistoryAdapter(HistoryClickListener historyClickListener, boolean editable) {
        this.historyClickListener = historyClickListener;
        this.editable = editable;
    }

    /**
     * ViewModel in HistoryActivity observed changed in LiveData of history list
     * - rebinds all ViewHolders in supplied list
     * @param historyList
     */
    public void setHistoryList(List<HistoryData> historyList){
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    /**
     * used within onResume of HistoryActivity
     * - necessary since in manifest only single instance of activity can be created
     * @param editable
     */
    public void setEditable(boolean editable){
        this.editable = editable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_element, parent, false);
        return new ViewHolder(view);
    }

    /**
     * sets ViewHolder for each history element in list
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setHistoryData(historyList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView goalNameText, dateText, stepsTakenText, goalStepsText, percentageText;
        private ProgressBar progressBar;
        private ImageView editIcon;
        private HistoryData historyData;

        /**
         * Finds Views xml components inside of and sets click listeners for specific button
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goalNameText = (TextView)itemView.findViewById(R.id.name);
            dateText = (TextView)itemView.findViewById(R.id.date);
            stepsTakenText = (TextView)itemView.findViewById(R.id.steps_num);
            goalStepsText = (TextView)itemView.findViewById(R.id.goal_steps_num);
            percentageText = (TextView)itemView.findViewById(R.id.goal_percentage);

            progressBar = (ProgressBar) itemView.findViewById(R.id.completion_bar);
            progressBar.setProgress(0);

            editIcon = itemView.findViewById(R.id.edit);
            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { historyClickListener.onHistoryClick(historyData); }
            });
        }

        /**
         * sets specific attributes for each history view
         * @param historyData
         */
        private void setHistoryData(HistoryData historyData){
            this.historyData = historyData;
            goalNameText.setText(historyData.getGoalName());
            dateText.setText(historyData.getDate());
            stepsTakenText.setText(Integer.toString(historyData.getStepsTaken()));
            goalStepsText.setText(Integer.toString(historyData.getGoalSteps()));
            percentageText.setText(Integer.toString(historyData.getPercentageToGoal())+"%");

            progressBar.setProgress(historyData.getPercentageToGoal());
            editIcon.setVisibility(editable ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public interface HistoryClickListener{
        void onHistoryClick( HistoryData historyData);
    }
}
