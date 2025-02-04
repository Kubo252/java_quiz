package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        TextView correctAnswerTextView = findViewById(R.id.textView_correct_answer);
        Button continueButton = findViewById(R.id.button_continue);

        String correctAnswer = getIntent().getStringExtra("correctAnswer");
        boolean isCorrect = getIntent().getBooleanExtra("isCorrect", false);

        if (isCorrect) {
            correctAnswerTextView.setText("Your answer was right! Well done!");
        } else {
            correctAnswerTextView.setText("Correct Answer: " + correctAnswer);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}