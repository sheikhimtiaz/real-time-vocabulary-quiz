package com.sheikhimtiaz.realtimequiz.model;

import com.sheikhimtiaz.realtimequiz.service.BroadcastService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketSession;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Quiz {

    private final String id;
    private final List<Question> questions;
    private final Map<WebSocketSession, String> participants = new ConcurrentHashMap<>();
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final BroadcastService broadcastService;

    private int currentQuestionIndex = 0;
    private boolean active = false;

    public Quiz(String id, ThreadPoolTaskScheduler scheduler, BroadcastService broadcastService) {
        this.id = id;
        this.scheduler = scheduler;
        this.broadcastService = broadcastService;
        this.questions = initializeQuestions();
    }

    public void addParticipant(String userName, WebSocketSession session) {
        participants.put(session, userName);
        scores.putIfAbsent(userName, 0);
        if (!active && participants.size() >= 1) {
            startQuiz();
        }
        broadcastService.sendMessageToParticipant(session, "You joined quiz: " + id);
    }

    public void removeParticipant(WebSocketSession session) {
        String userName = participants.remove(session);
        if (userName != null) {
            scores.remove(userName);
        }
    }

    private void startQuiz() {
        active = true;
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Quiz is starting in 10 seconds!");
        scheduler.schedule(this::broadcastNextQuestion, new Date(System.currentTimeMillis() + 10000));
    }

    private void broadcastNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            broadcastFinalRanking();
            scheduler.schedule(this::endQuiz, new Date(System.currentTimeMillis() + 10000));
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Question: " + question.getText());

        scheduler.schedule(this::closeCurrentQuestion, new Date(System.currentTimeMillis() + 10000));
    }

    private void closeCurrentQuestion() {
        broadcastCurrentRanking();
        currentQuestionIndex++;
        broadcastNextQuestion();
    }

    private void broadcastCurrentRanking() {
        String ranking = formatRanking();
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Current Ranking:\n" + ranking);
    }

    private void broadcastFinalRanking() {
        String ranking = formatRanking();
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Quiz Finished! Final Ranking:\n" + ranking);
    }

    private void endQuiz() {
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Quiz session ended. Goodbye!");
        participants.keySet().forEach(session -> {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        participants.clear();
    }

    private String formatRanking() {
        StringBuilder ranking = new StringBuilder();
        scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> ranking.append(entry.getKey()).append(": ").append(entry.getValue()).append(" points\n"));
        return ranking.toString();
    }

    public void submitAnswer(WebSocketSession session, String answer) {
        String userName = participants.get(session);
        if (userName == null || currentQuestionIndex >= questions.size()) {
            throw new IllegalArgumentException("You are not part of this quiz or the quiz has ended.");
        }

        Question question = questions.get(currentQuestionIndex);
        if (question.isCorrectAnswer(answer)) {
            scores.computeIfPresent(userName, (key, value) -> value + 1);
        }
    }

    private List<Question> initializeQuestions() {
        return List.of(
                new Question("What is the capital of France?", "Paris"),
                new Question("What is 2 + 2?", "4"),
                new Question("What is the color of the sky?", "Blue")
        );
    }
}
