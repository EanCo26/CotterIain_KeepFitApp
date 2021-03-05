package me.uos.cotteriain_keepfitapp.Goal;

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

    /**
     * Used to setup Goals to be displayed in RecyclerView for goal management
     * @param goalClickListener - supplied goalCLickListener initialized by MainActivity
     * @param editable - whether goals are set to editable in settings
     */
    public GoalAdapter(GoalClickListener goalClickListener, boolean editable) {
        this.goalList = new ArrayList<>();
        this.holders = new ArrayList<>();
        this.goalClickListener = goalClickListener;
        this.editable = editable;
    }

    /**
     * ViewModel in MainActivity observed changed in LiveData of goal list
     * - rebinds all ViewHolders in supplied list
     * - resets selected ViewHolder
     * @param goalList
     * @param goalId - the currently active goal's id in database (selected by user)
     */
    public void setGoalList(List<GoalData> goalList, int goalId) {
        holders.clear();
        this.goalList = goalList;
        this.goalId = goalId;
        this.selectedViewIndex = -1;
        notifyDataSetChanged();
    }

    /**
     * used within onResume of MainActivity
     * - necessary since in manifest only single instance of activity can be created
     * @param editable
     */
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

    /**
     * every ViewHolder in adapter given specific values - name, steps and whether it is active or not (id of goal match active id)
     * - ViewHolders added to list of ViewHolders to allow future manipulation of editable and acive attributes as they affect UI of ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int position) {
        GoalData currentGoal = goalList.get(position);
        String name = currentGoal.getName();
        int steps = currentGoal.getSteps();

        holder.setGoalData(currentGoal);
        boolean active = goalId == currentGoal.getId();
        holder.setActive(active);
        if(active)
            selectedViewIndex = position;

        holders.add(holder);
    }

    @Override
    public int getItemCount() { return goalList.size(); }

    /**
     * triggered in MainActivity in OnGoalClick - essentially set the active border UI around active goal
     * - makes change to previously selected goal if the previous exists in list - i.e not set to -1
     * @param selectedViewIndex
     */
    public void setActiveGoal(int selectedViewIndex){
        if(this.selectedViewIndex == selectedViewIndex)
            return;

        if(this.selectedViewIndex >= 0 )
            holders.get(this.selectedViewIndex).setActive(false);

        goalId = holders.get(selectedViewIndex).getGoalData().getId();
        this.selectedViewIndex = selectedViewIndex;

        holders.get(this.selectedViewIndex).setActive(true);
    }

    //todo remove method if app works properly
//    public GoalData getGoalEntryAt(int index){ return holders.get(index).getGoalData(); }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView editIcon, deleteIcon;
        private TextView nameView, stepsView;
        private View borderView;
        private GoalData goal;

        /**
         * Finds Views xml components inside of and sets click listeners for specific buttons
         * @param itemView
         */
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

        /**
         * sets specific attributes for each goal view
         * @param goal
         */
        private void setGoalData(GoalData goal) {
            this.goal = goal;
            nameView.setText(goal.getName());
            stepsView.setText(Integer.toString(goal.getSteps()));
        }

        /**
         * @return return the goal assigned to this ViewHolder
         */
        private GoalData getGoalData() { return goal; }

        /**
         * sets whether this ViewHolder is the active goal and decide whick UI elements to display
         * @param isActive
         */
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
