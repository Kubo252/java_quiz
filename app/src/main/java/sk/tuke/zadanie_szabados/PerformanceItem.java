package sk.tuke.zadanie_szabados;

public class PerformanceItem {
    private String category;
    private int progress;
    private int score;
    private int totalQuestions;

    public PerformanceItem(String category, int progress, int score, int totalQuestions) {
        this.category = category;
        this.progress = progress;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public String getCategory() {
        return category;
    }

    public int getProgress() {
        return progress;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }
}