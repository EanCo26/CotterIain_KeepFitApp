package me.uos.cotteriain_keepfitapp.GoalSettings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.uos.cotteriain_keepfitapp.R;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<GoalData> goalList;
    private List<ViewHolder> holders;
    private GoalClickListener goalClickListener;

    private int goalId;
    private int selectedViewIndex = -1;

    private boolean editable = true;

    public GoalAdapter(GoalClickListener goalClickListener, boolean editable) {
        this.goalList = new ArrayList<>();
        this.holders = new ArrayList<>();
        this.goalClickListener = goalClickListener;
        this.editable = editable;
    }

    public void setGoalList(List<GoalData> goalList, int goalId) {
        holders.clear();
        this.goalList = goalList;
        this.goalId = goalId;
        this.selectedViewIndex = -1;
        notifyDataSetChanged();
    }

    public void setEditable(boolean editable){
        this.editable = editable;
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
        GoalData currentGoal = goalList.get(position);
        String name = currentGoal.getName();
        int steps = currentGoal.getSteps();

        holder.setGoalData(currentGoal);
        boolean active = goalId == currentGoal.getId();
        holder.setActive(active);
        if(active){
            selectedViewIndex = position;
        }
        holders.add(holder);
    }

    @Override
    public int getItemCount() { return goalList.size(); }

    public void setActiveGoal(int selectedViewIndex){
        if(this.selectedViewIndex == selectedViewIndex)
            return;

        if(this.selectedViewIndex >= 0 )
            holders.get(this.selectedViewIndex).setActive(false);

        goalId = holders.get(selectedViewIndex).getGoalData().getId();
        this.selectedViewIndex = selectedViewIndex;

        holders.get(this.selectedViewIndex).setActive(true);
    }

    public GoalData getGoalEntryAt(int index){ return holders.get(index).getGoalData(); }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView editIcon, deleteIcon;
        private TextView nameView, stepsView;
        private View borderView;
        private GoalData goal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.goal_name);
            stepsView = itemView.findViewById(R.id.goal_steps);

            borderView = itemView;
            borderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onGoalClick(getAdapterPosition(), goal); }
            });

            editIcon = itemView.findViewById(R.id.edit);
            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onEditClick(goal); }
            });
            editIcon.setVisibility(editable ? View.VISIBLE : View.INVISIBLE);

            deleteIcon = itemView.findViewById(R.id.delete);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onDeleteClick(goal); }
            });
        }

        private void setGoalData(GoalData goal) {
            this.goal = goal;
            nameView.setText(goal.getName());
            stepsView.setText(Integer.toString(goal.getSteps()));
        }

        private GoalData getGoalData() { return goal; }

        private void setActive(boolean isActive){
            borderView.setBackgroundResource(isActive?R.drawable.selected_border:R.drawable.empty_border);
            if(editable)
                editIcon.setVisibility(isActive ? View.INVISIBLE : View.VISIBLE);
            else
                editIcon.setVisibility(View.INVISIBLE);
            deleteIcon.setVisibility(isActive ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public interface GoalClickListener{
        void onGoalClick(int itemIndex, GoalData goal);
        void onEditClick(GoalData goal);
        void onDeleteClick(GoalData goal);
    }
}
