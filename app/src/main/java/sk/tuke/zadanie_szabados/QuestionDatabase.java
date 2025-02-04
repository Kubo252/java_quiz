package sk.tuke.zadanie_szabados;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionDatabase {

    public static List<Question> getQuestions(String category, int totalQuestions) {
        List<Question> questions = new ArrayList<>();

        if (category.equals("Sport")) {
            questions.add(new Question("What is the national sport of Japan?", List.of("Sumo", "Baseball", "Soccer", "Basketball"), 0));
            questions.add(new Question("How many players are there in a soccer team?", List.of("9", "10", "11", "12"), 2));
            questions.add(new Question("Which country won the FIFA World Cup in 2018?", List.of("Brazil", "Germany", "France", "Argentina"), 2));
            questions.add(new Question("In which sport would you perform a slam dunk?", List.of("Tennis", "Basketball", "Baseball", "Soccer"), 1));
            questions.add(new Question("Which sport is known as the 'king of sports'?", List.of("Basketball", "Cricket", "Soccer", "Tennis"), 2));
            questions.add(new Question("How many rings are there on the Olympic flag?", List.of("3", "4", "5", "6"), 2));
            questions.add(new Question("Which country is known as the birthplace of cricket?", List.of("Australia", "India", "England", "South Africa"), 2));
            questions.add(new Question("What is the highest score in a single frame of bowling?", List.of("10", "20", "30", "40"), 2));
            questions.add(new Question("Which sport uses a shuttlecock?", List.of("Tennis", "Badminton", "Squash", "Table Tennis"), 1));
            questions.add(new Question("In which sport is the term 'home run' used?", List.of("Cricket", "Soccer", "Baseball", "Rugby"), 2));
        } else if (category.equals("Geography")) {
            questions.add(new Question("What is the capital of France?", List.of("Berlin", "Madrid", "Paris", "Rome"), 2));
            questions.add(new Question("Which is the largest continent by area?", List.of("Africa", "Asia", "Europe", "North America"), 1));
            questions.add(new Question("What is the longest river in the world?", List.of("Amazon", "Nile", "Yangtze", "Mississippi"), 1));
            questions.add(new Question("Which country has the most natural lakes?", List.of("Canada", "USA", "Russia", "Brazil"), 0));
            questions.add(new Question("What is the smallest country in the world?", List.of("Monaco", "Nauru", "San Marino", "Vatican City"), 3));
            questions.add(new Question("Which desert is the largest in the world?", List.of("Sahara", "Gobi", "Kalahari", "Arctic"), 0));
            questions.add(new Question("What is the capital of Australia?", List.of("Sydney", "Melbourne", "Canberra", "Brisbane"), 2));
            questions.add(new Question("Which ocean is the deepest?", List.of("Atlantic", "Indian", "Arctic", "Pacific"), 3));
            questions.add(new Question("What is the highest mountain in the world?", List.of("K2", "Kangchenjunga", "Lhotse", "Mount Everest"), 3));
            questions.add(new Question("Which country is known as the Land of the Rising Sun?", List.of("China", "Japan", "South Korea", "Thailand"), 1));
        }

        Collections.shuffle(questions);
        return questions.subList(0, totalQuestions);
    }
}