// DrawView.java
package sk.tuke.zadanie_szabados;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private Paint currentPaint;
    private Path currentPath;
    private List<DrawPath> drawPaths;
    private boolean eraserMode;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.BLACK); // Default black color
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeWidth(8f);

        currentPath = new Path();
        drawPaths = new ArrayList<>();
        eraserMode = false;
    }

    public void setPaintColor(int color) {
        if (eraserMode) {
            eraserMode = false;
        }
        currentPaint = new Paint(currentPaint);
        currentPaint.setColor(color);
    }

    public void setEraserMode(boolean enabled) {
        eraserMode = enabled;
        if (enabled) {
            currentPaint = new Paint(currentPaint);
            currentPaint.setColor(Color.WHITE); // Match canvas background for erasing
        }
    }

    public void clearCanvas() {
        drawPaths.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (DrawPath drawPath : drawPaths) {
            canvas.drawPath(drawPath.path, drawPath.paint);
        }
        canvas.drawPath(currentPath, currentPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                drawPaths.add(new DrawPath(currentPath, currentPaint));
                currentPath = new Path();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private static class DrawPath {
        Path path;
        Paint paint;

        DrawPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }
}