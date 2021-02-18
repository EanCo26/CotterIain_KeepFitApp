package me.uos.cotteriain_keepfitapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.database.GoalEntry;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<GoalEntry> goalList;
    private List<ViewHolder> holders;
    private GoalClickListener goalClickListener;
    private int goalId;
    private int selectedViewIndex = Integer.MAX_VALUE;

    public GoalAdapter(GoalClickListener goalClickListener) {
        this.goalList = new ArrayList<>();
        this.holders = new ArrayList<>();
        this.goalClickListener = goalClickListener;
    }

    public void setGoalList(List<GoalEntry> goalList, int goalId) {
        holders.clear();
        this.goalList = goalList;
        this.goalId = goalId;
        this.selectedViewIndex = Integer.MAX_VALUE;
        notifyDataSetChanged();
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
        boolean active = goalId == holder.getGoalId();
        holder.setBorder(active);
        if(active){
            selectedViewIndex = position;
        }
        holders.add(holder);
    }

    public void selectGoal(int selectedViewIndex){
        if(this.selectedViewIndex == selectedViewIndex)
            return;

        if(this.selectedViewIndex != Integer.MAX_VALUE)
            holders.get(this.selectedViewIndex).setBorder(false);

        goalId = holders.get(selectedViewIndex).getGoalId();
        this.selectedViewIndex = selectedViewIndex;

        holders.get(this.selectedViewIndex).setBorder(true);
    }

    public int getSelectedViewIndex() { return selectedViewIndex; }
    public void setSelectedViewIndex(int selectedViewIndex) { this.selectedViewIndex = selectedViewIndex; }

    public GoalEntry getGoalEntryAt(int index){ return holders.get(index).getGoal(); }
    @Override
    public int getItemCount() { return goalList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pencil;
        private TextView nameView;
        private TextView stepsView;
        private View borderView;
        private GoalEntry goal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.goal_name);
            stepsView = itemView.findViewById(R.id.goal_steps);
            borderView = itemView;
            borderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onGoalClick(getAdapterPosition(), goal); }
            });

            pencil = itemView.findViewById(R.id.edit);
            pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onEditClick(getAdapterPosition(), goal); }
            });
        }

        private void setGoalData(GoalEntry goal) {
            this.goal = goal;
            nameView.setText(goal.getName());
            stepsView.setText(Integer.toString(goal.getSteps()));
        }

        public GoalEntry getGoal() { return goal; }
        private int getGoalId(){ return goal.getId(); }

        private void setBorder(boolean isBorderActive){ borderView.setBackgroundResource(isBorderActive?R.drawable.selected_border:R.drawable.empty_border); }
    }

    public interface GoalClickListener{
        void onGoalClick(int itemIndex, GoalEntry goal);
        void onEditClick(int itemIndex, GoalEntry goal);
    }
}
