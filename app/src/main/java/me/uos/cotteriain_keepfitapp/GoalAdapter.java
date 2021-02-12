package me.uos.cotteriain_keepfitapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<GoalItem> goalList;
    final private GoalClickListener goalClickListener;

    public GoalAdapter(List<GoalItem> goalList, GoalClickListener goalClickListener) {
        this.goalList = goalList;
        this.goalClickListener = goalClickListener;
    }

    public GoalAdapter(List<GoalItem> goalList) {
        this.goalList = goalList;
        goalClickListener = null;
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
        holder.setGoalData(name, steps);
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView goalName;
        private TextView goalSteps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goalName = itemView.findViewById(R.id.goal_name);
            goalSteps = itemView.findViewById(R.id.goal_steps);

            itemView.setOnClickListener(this);
        }

        private void setGoalData(String name, int steps){
            goalName.setText(name);
            goalSteps.setText(Integer.toString(steps));
        }

        @Override
        public void onClick(View view) {
            goalClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface GoalClickListener{
        void onItemClick(int itemIndex);
    }
}
