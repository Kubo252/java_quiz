package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.Html;

public class QuizActivity extends AppCompatActivity {
    private TextView questionTextView, timerTextView, progressTextView;
    private Button option1Button, option2Button, option3Button, option4Button, cancelButton;
    private List<TriviaQuestion> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions = 5;
    private boolean isResultShown = false;
    private CountDownTimer countDownTimer;
    private boolean isQuizCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questions = (List<TriviaQuestion>) getIntent().getSerializableExtra("questions");

        questionTextView = findViewById(R.id.textView_question);
        timerTextView = findViewById(R.id.textView_timer);
        progressTextView = findViewById(R.id.textView_progress);
        option1Button = findViewById(R.id.button_option1);
        option2Button = findViewById(R.id.button_option2);
        option3Button = findViewById(R.id.button_option3);
        option4Button = findViewById(R.id.button_option4);
        cancelButton = findViewById(R.id.button_cancel);

        showNextQuestion();

        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                checkAnswer(clickedButton.getText().toString());
            }
        };

        option1Button.setOnClickListener(answerClickListener);
        option2Button.setOnClickListener(answerClickListener);
        option3Button.setOnClickListener(answerClickListener);
        option4Button.setOnClickListener(answerClickListener);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQuizCancelled = true;
                finish();
            }
        });
    }

    private void showNextQuestion() {
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "No questions available for this category.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentQuestionIndex < questions.size()) {
            TriviaQuestion question = questions.get(currentQuestionIndex);
            questionTextView.setText(Html.fromHtml(question.getQuestion())); // Decode HTML entities
            List<String> options = question.getIncorrect_answers();
            options.add(question.getCorrect_answer());
            option1Button.setText(options.get(0));
            option2Button.setText(options.get(1));
            option3Button.setText(options.get(2));
            option4Button.setText(options.get(3));

            progressTextView.setText("Correct answers: " + correctAnswers + "/" + totalQuestions);

            startTimer();
        } else {
            showResult();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTextView.setText("Time's up!");
                if (!isQuizCancelled) {
                    showAnswer(false);
                }
            }
        }.start();
    }

    private void checkAnswer(String selectedAnswer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (!isQuizCancelled) {
            TriviaQuestion currentQuestion = questions.get(currentQuestionIndex);
            boolean isCorrect = currentQuestion.getCorrect_answer().equals(selectedAnswer);
            if (isCorrect) {
                correctAnswers++;
            }
            showAnswer(isCorrect);
        }
    }

    private void showAnswer(boolean isCorrect) {
        if (!isQuizCancelled) {
            TriviaQuestion currentQuestion = questions.get(currentQuestionIndex);
            String correctAnswer = currentQuestion.getCorrect_answer();

            Intent intent = new Intent(QuizActivity.this, AnswerActivity.class);
            intent.putExtra("correctAnswer", correctAnswer);
            intent.putExtra("isCorrect", isCorrect);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            currentQuestionIndex++;
            showNextQuestion();
        }
    }

    private void showResult() {
        if (!isResultShown && !isQuizCancelled) {
            isResultShown = true;
            saveResultToDatabase();
            updateCorrectAnswers(correctAnswers); // Update correct answers in Firestore
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("correctAnswers", correctAnswers);
            intent.putExtra("totalQuestions", totalQuestions);
            startActivity(intent);
            finish();
        }
    }

    private void saveResultToDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String category = getIntent().getStringExtra("category");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("results").child(userId).child(category);
            databaseReference.push().setValue(correctAnswers);
        }
    }

    private void updateCorrectAnswers(int correctAnswers) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userName = currentUser.getDisplayName(); // Get the user's name
            FirebaseApp.initializeApp(this);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            db.collection("users").document(userId)
                                    .update("correctAnswers", FieldValue.increment(correctAnswers))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Successfully updated correct answers");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error updating correct answers", e);
                                    });
                        } else {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("correctAnswers", correctAnswers);
                            userData.put("name", userName); // Store the user's name
                            db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Successfully created user document");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error creating user document", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error checking user document", e);
                    });
        }
    }
}