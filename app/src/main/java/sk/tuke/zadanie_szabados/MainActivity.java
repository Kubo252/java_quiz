package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private boolean isLoginVisible = false;
    private FirebaseAuth mAuth;
    private TriviaApiService triviaApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button drawRoomButton = findViewById(R.id.draw_room);
        drawRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawRoomActivity.class);
                startActivity(intent);
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "quiz-database").build();
        recyclerView = findViewById(R.id.recyclerView_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (db.categoryDao().getAllCategories().isEmpty()) {
                    List<Category> categories = Arrays.asList(
                        new Category("Sport"),
                        new Category("Geography"),
                        new Category("History"),
                        new Category("Science")
                    );
                    for (Category category : categories) {
                        db.categoryDao().insert(category);
                    }
                }

                List<Category> allCategories = db.categoryDao().getAllCategories();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CategoryAdapter(allCategories, new CategoryAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Category category) {
                                startQuiz(category.getName());
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();

        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        triviaApiService = retrofit.create(TriviaApiService.class);

        Button loginButton = findViewById(R.id.button_login);
        EditText emailEditText = findViewById(R.id.editText_email);
        EditText passwordEditText = findViewById(R.id.editText_password);
        Button submitLoginButton = findViewById(R.id.button_submit_login);
        TextView registerTextView = findViewById(R.id.textView_register);
        TextView welcomeTextView = findViewById(R.id.textView_welcome);
        Button logoutButton = findViewById(R.id.button_logout);
        Button bestPerformanceButton = findViewById(R.id.button_best_performance);
        Button best_users_stats = findViewById(R.id.button_best_users_stats);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loginButton.setVisibility(View.GONE);
            welcomeTextView.setText("Welcome, " + currentUser.getDisplayName());
            welcomeTextView.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            bestPerformanceButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            welcomeTextView.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            bestPerformanceButton.setVisibility(View.GONE);
        }

        best_users_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BestUsersStatsActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginVisible) {
                    emailEditText.setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.GONE);
                    submitLoginButton.setVisibility(View.GONE);
                    registerTextView.setVisibility(View.GONE);
                } else {
                    emailEditText.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    submitLoginButton.setVisibility(View.VISIBLE);
                    registerTextView.setVisibility(View.VISIBLE);
                }
                isLoginVisible = !isLoginVisible;
            }
        });

        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    welcomeTextView.setText("Welcome, " + user.getDisplayName());
                                    welcomeTextView.setVisibility(View.VISIBLE);
                                    loginButton.setVisibility(View.GONE);
                                    logoutButton.setVisibility(View.VISIBLE);
                                    emailEditText.setVisibility(View.GONE);
                                    passwordEditText.setVisibility(View.GONE);
                                    submitLoginButton.setVisibility(View.GONE);
                                    registerTextView.setVisibility(View.GONE);
                                    bestPerformanceButton.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                loginButton.setVisibility(View.VISIBLE);
                welcomeTextView.setVisibility(View.GONE);
                logoutButton.setVisibility(View.GONE);
                bestPerformanceButton.setVisibility(View.GONE);
            }
        });

        if (currentUser != null) {
            bestPerformanceButton.setVisibility(View.VISIBLE);
            bestPerformanceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<Category> allCategories = db.categoryDao().getAllCategories();
                            ArrayList<String> categoryNames = new ArrayList<>();
                            for (Category category : allCategories) {
                                categoryNames.add(category.getName());
                            }
                            Intent intent = new Intent(MainActivity.this, BestPerformanceActivity.class);
                            intent.putStringArrayListExtra("allCategories", categoryNames);
                            startActivity(intent);
                        }
                    }).start();
                }
            });
        } else {
            bestPerformanceButton.setVisibility(View.GONE);
        }
    }

    private void startQuiz(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            int categoryId = getCategoryID(category);
            fetchQuestions(5, categoryId, "medium", "multiple", category);
        } else {
            Toast.makeText(MainActivity.this, "Please log in to start the quiz", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCategoryID(String category) {
        switch (category) {
            case "Sport":
                return 21; // ID for Sports category in Trivia API
            case "Geography":
                return 22; // ID for Geography category in Trivia API
            case "History":
                return 23; // ID for History category in Trivia API
            case "Science":
                return 17; // ID for Science & Nature category in Trivia API
            default:
                return 9; // Default to General Knowledge
        }
    }

    private void fetchQuestions(int amount, int category, String difficulty, String type, String quizCategory) {
        // No manual encoding needed; Retrofit handles it.
        triviaApiService.getQuestions(amount, category, difficulty, type, "url3986")
                .enqueue(new Callback<TriviaResponse>() {
                    @Override
                    public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<TriviaQuestion> questions = response.body().getResults();
                            // Optionally decode the returned strings if needed:
                            for (TriviaQuestion q : questions) {
                                try {
                                    q.setQuestion(URLDecoder.decode(q.getQuestion(), "UTF-8"));
                                    q.setCorrect_answer(URLDecoder.decode(q.getCorrect_answer(), "UTF-8"));
                                    List<String> decodedIncorrect = new ArrayList<>();
                                    for (String wrong : q.getIncorrect_answers()) {
                                        decodedIncorrect.add(URLDecoder.decode(wrong, "UTF-8"));
                                    }
                                    q.setIncorrect_answers(decodedIncorrect);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                            intent.putExtra("category", quizCategory);
                            intent.putExtra("questions", new ArrayList<>(questions));
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to fetch questions", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TriviaResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}