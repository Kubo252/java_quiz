package sk.tuke.zadanie_szabados;

import java.util.List;

public class TriviaResponse {
    private List<TriviaQuestion> results;

    // Getters and setters
    public List<TriviaQuestion> getResults() {
        return results;
    }

    public void setResults(List<TriviaQuestion> results) {
        this.results = results;
    }
}