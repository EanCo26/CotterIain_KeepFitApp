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
    private GoalClickListener goalClickListener;
    private int goalIndex;
    List<ViewHolder> holders = new ArrayList<>();

    public GoalAdapter(List<GoalEntry> goalList, GoalClickListener goalClickListener, int goalIndex) {
        this.goalList = goalList;
        this.goalClickListener = goalClickListener;
        this.goalIndex = goalIndex;
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
        holder.setGoalData(name, steps, goalIndex == position?true:false);
        holders.add(holder);
    }

    public void selectHolder(int selected){
        if(goalIndex < getItemCount())
            holders.get(goalIndex).setBorder(false);
        goalIndex = selected;

        if(goalIndex < getItemCount())
            holders.get(selected).setBorder(true);
    }

    public void changeGoals(List<GoalEntry> goalList) {
        this.goalList = goalList;
        holders.clear();
        notifyDataSetChanged();
    }

    public GoalEntry getGoalEntryAt(int index){
        goalIndex = goalIndex == index ? Integer.MAX_VALUE:goalIndex;
        return goalList.get(index);
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pencil;
        private TextView nameView;
        private TextView stepsView;
        private View borderView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.goal_name);
            stepsView = itemView.findViewById(R.id.goal_steps);
            borderView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { goalClickListener.onGoalClick(getAdapterPosition()); }
            });

            pencil = itemView.findViewById(R.id.edit);
            pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goalClickListener.onEditClick(getAdapterPosition());
                }
            });
        }

        private void setGoalData(String name, int steps, boolean isBorderActive){
            nameView.setText(name);
            stepsView.setText(Integer.toString(steps));
            borderView.setBackgroundResource(isBorderActive?R.drawable.selected_border:R.drawable.empty_border);
        }

        private void setBorder( boolean isBorderActive){
            borderView.setBackgroundResource(isBorderActive?R.drawable.selected_border:R.drawable.empty_border);
        }
    }

    public interface GoalClickListener{
        void onGoalClick(int itemIndex);
        void onEditClick(int itemIndex);
    }
}
