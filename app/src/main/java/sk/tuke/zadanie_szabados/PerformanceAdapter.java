package sk.tuke.zadanie_szabados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.ViewHolder> {

    private List<PerformanceItem> performanceList;

    public PerformanceAdapter(List<PerformanceItem> performanceList) {
        this.performanceList = performanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.performance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PerformanceItem item = performanceList.get(position);
        holder.categoryName.setText(item.getCategory());
        holder.progressBar.setProgress(item.getProgress());
        holder.scoreText.setText(item.getScore() + "/" + item.getTotalQuestions());
    }

    @Override
    public int getItemCount() {
        return performanceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ProgressBar progressBar;
        TextView scoreText;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            progressBar = itemView.findViewById(R.id.progressBar);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }
}
