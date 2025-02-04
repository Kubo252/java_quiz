package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestPerformanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PerformanceAdapter adapter;
    private List<PerformanceItem> performanceList = new ArrayList<>();
    private int totalQuestions = 5; // Total number of questions per quiz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_performance);

        recyclerView = findViewById(R.id.recyclerViewPerformance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button returnHomeButton = findViewById(R.id.button_return_home);
        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BestPerformanceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("results").child(userId);

            ArrayList<String> allCategories = getIntent().getStringArrayListExtra("allCategories");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    performanceList.clear();

                    for (String category : allCategories) {
                        List<Integer> scores = new ArrayList<>();
                        if (dataSnapshot.hasChild(category)) {
                            for (DataSnapshot scoreSnapshot : dataSnapshot.child(category).getChildren()) {
                                Integer score = scoreSnapshot.getValue(Integer.class);
                                if (score != null) {
                                    scores.add(score);
                                }
                            }
                        }
                        int percentage;
                        int bestScore = 0;
                        if (!scores.isEmpty()) {
                            bestScore = Collections.max(scores);
                            percentage = (bestScore * 100) / totalQuestions;
                        } else {
                            percentage = 0;
                        }
                        performanceList.add(new PerformanceItem(category, percentage, bestScore, totalQuestions));
                    }
                    adapter = new PerformanceAdapter(performanceList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }
}