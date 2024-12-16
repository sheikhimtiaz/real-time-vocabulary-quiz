package com.sheikhimtiaz.realtimequiz.model;

public class Question {
    private final String text;
    private final String answer;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrectAnswer(String input) {
        return answer.equalsIgnoreCase(input);
    }
}