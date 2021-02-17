package me.uos.cotteriain_keepfitapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.database.GoalEntry;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<GoalEntry> goalList;
    private List<ViewHolder> holders;
    private GoalClickListener goalClickListener;
    private int goalId;
    private int selected = Integer.MAX_VALUE;

    public GoalAdapter(LiveData<List<GoalEntry>> goalList, GoalClickListener goalClickListener, int goalId) {
        this.goalList = goalList.getValue() != null?goalList.getValue():new ArrayList<>();
        this.holders = new ArrayList<>();
        this.goalClickListener = goalClickListener;
        this.goalId = goalId;
    }

    @NonNull
    @Override
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int position) {
        String name = goalList.get(position).getName();
        int steps = goalList.get(position).getSteps();

        holder.setGoalData(goalList.get(position));
        boolean active = goalId == holder.getGoalEntryId();
        holder.setBorder(active);
        if(active){
            selected = position;
        }
        holders.add(holder);
    }

    public void selectGoal(int selected){
        if(this.selected == selected)
            return;

        goalId = holders.get(selected).getGoalEntryId();
        holders.get(this.selected).setBorder(false);
        this.selected = selected;
        holders.get(this.selected).setBorder(true);
    }

    public int getActiveGoalId(){ return goalId; }

    public void setGoalList(List<GoalEntry> goalList) {
        holders.clear();
        this.goalList = goalList;
        notifyDataSetChanged();
    }

    public GoalEntry getGoalEntryAt(int index){ return holders.get(index).getGoalEntry(); }
    @Override
    public int getItemCount() { return goalList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pencil;
        private TextView nameView;
        private TextView stepsView;
        private View borderView;
        private GoalEntry goalEntry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.goal_name);
            stepsView = itemView.findViewById(R.id.goal_steps);
            borderView = itemView;
            borderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onGoalClick(getAdapterPosition()); }
            });

            pencil = itemView.findViewById(R.id.edit);
            pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onEditClick(getAdapterPosition()); }
            });
        }

        private void setGoalData(GoalEntry goalEntry) {
            this.goalEntry = goalEntry;
            nameView.setText(goalEntry.getName());
            stepsView.setText(Integer.toString(goalEntry.getSteps()));
        }

        public GoalEntry getGoalEntry() { return goalEntry; }
        private int getGoalEntryId(){ return goalEntry.getId(); }

        private void setBorder(boolean isBorderActive){ borderView.setBackgroundResource(isBorderActive?R.drawable.selected_border:R.drawable.empty_border); }
    }

    public interface GoalClickListener{
        void onGoalClick(int itemIndex);
        void onEditClick(int itemIndex);
    }
}
