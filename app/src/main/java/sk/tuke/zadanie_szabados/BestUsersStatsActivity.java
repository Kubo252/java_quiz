// BestUsersStatsActivity.java
package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BestUsersStatsActivity extends AppCompatActivity {

    private TextView bestUsersTextView;
    private TextView bestUserStatsTextView; // Add this TextView to your layout
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_users_stats);

        bestUsersTextView = findViewById(R.id.textView_best_users);
        bestUserStatsTextView = findViewById(R.id.textView_best_users); // Initialize the TextView
        db = FirebaseFirestore.getInstance();

        fetchBestUsers();
        displayBestUserStats(); // Call the method

        Button returnHomeButton = findViewById(R.id.button_homepage);
        returnHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BestUsersStatsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchBestUsers() {
        db.collection("users")
            .orderBy("correctAnswers", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> bestUsers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userName = document.getString("name");
                        Long correctAnswers = document.getLong("correctAnswers");
                        bestUsers.add(userName + ": " + correctAnswers);
                    }
                    bestUsersTextView.setText(String.join("\n", bestUsers));
                } else {
                    bestUsersTextView.setText("Error getting documents: " + task.getException());
                }
            });
    }

    private void displayBestUserStats() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            int correctAnswers = documentSnapshot.getLong("correctAnswers").intValue();
                            // Display the user's name and correct answers
                            //bestUserStatsTextView.setText("Best User: " + userName + " with " + correctAnswers + " correct answers");
                        } else {
                            Log.e("Firestore", "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error retrieving user document", e);
                    });
        }
    }
}