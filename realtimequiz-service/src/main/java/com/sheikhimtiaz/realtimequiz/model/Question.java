package com.sheikhimtiaz.realtimequiz.model;

public class Question {
    private final String question;
    private final String[] options;
    private final String answer;

    public Question(String text, String[] options, String answer) {
        this.question = text;
        this.options = options;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isCorrectAnswer(String input) {
        return answer.equalsIgnoreCase(input);
    }
}