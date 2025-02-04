// DrawRoomActivity.java
package sk.tuke.zadanie_szabados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class DrawRoomActivity extends AppCompatActivity {
    private DrawView drawView;
    private ImageButton colorBlack, colorRed, colorBlue, colorEraser, clearCanvas;
    private Button buttonHomepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_room);

        drawView = findViewById(R.id.draw_view);

        // Initialize color buttons
        colorBlack = findViewById(R.id.color_black);
        colorRed = findViewById(R.id.color_red);
        colorBlue = findViewById(R.id.color_blue);
        colorEraser = findViewById(R.id.color_eraser);
        clearCanvas = findViewById(R.id.clear_canvas);

        // Set click listeners for colors
        colorBlack.setOnClickListener(v -> drawView.setPaintColor(0xFF000000));
        colorRed.setOnClickListener(v -> drawView.setPaintColor(0xFFFF0000));
        colorBlue.setOnClickListener(v -> drawView.setPaintColor(0xFF0000FF));
        colorEraser.setOnClickListener(v -> drawView.setEraserMode(true));

        // Clear canvas button
        clearCanvas.setOnClickListener(v -> drawView.clearCanvas());

        // Initialize and set click listener for Homepage button
        buttonHomepage = findViewById(R.id.button_homepage);
        buttonHomepage.setOnClickListener(v -> {
            Intent intent = new Intent(DrawRoomActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}