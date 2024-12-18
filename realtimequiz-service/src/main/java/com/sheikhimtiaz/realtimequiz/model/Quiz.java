package com.sheikhimtiaz.realtimequiz.model;

import com.sheikhimtiaz.realtimequiz.service.BroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketSession;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Quiz {
    private static final Logger log = LoggerFactory.getLogger(Quiz.class);

    private final String id;
    private final List<Question> questions;
    private final Map<WebSocketSession, String> participants = new ConcurrentHashMap<>();
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final BroadcastService broadcastService;

    private int currentQuestionIndex = 0;
    private boolean active = false;
    private boolean hasCountdownStarted = false;
    private boolean isQuizRunning = false;

    public Quiz(String id, ThreadPoolTaskScheduler scheduler, BroadcastService broadcastService) {
        this.id = id;
        this.scheduler = scheduler;
        this.broadcastService = broadcastService;
        this.questions = initializeQuestions();
    }

    public void addParticipant(String userName, WebSocketSession session) {
        if(isQuizRunning) {
            broadcastService.sendMessageToParticipant(session, "Quiz already started! You cannot play this quiz!");
            throw new IllegalArgumentException(MessageFormat.format("Cannot add {0} to quiz {1}. Quiz already started!", userName, id));
        }
        String oldUsername = participants.get(session);
        if(Objects.nonNull(oldUsername)) {
            scores.remove(oldUsername);
        }
        participants.put(session, userName);
        scores.putIfAbsent(userName, 0);
        broadcastService.sendMessageToParticipant(session, "You joined quiz: " + id);
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Current Ranking #\n" + formatRanking());
        if (!active && !hasCountdownStarted && !participants.isEmpty()) {
            startCountdown();
        }
    }

    public void removeParticipant(WebSocketSession session) {
        String userName = participants.remove(session);
        if (userName != null) {
            scores.remove(userName);
        }
    }

    private void startCountdown() {
        hasCountdownStarted = true;
        for(int seconds=10;seconds>=0;seconds--){
            broadcastService.broadcastToAllParticipants(participants.keySet(),
                    MessageFormat.format("Countdown:Quiz is starting in {0} seconds!", seconds));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("InterruptedException while sleeping: " + e.getMessage());
                throw new RuntimeException(e);
            }
            if(participants.size() == 2) {
                broadcastService.broadcastToAllParticipants(participants.keySet(), "Starting Quiz!");
                break;
            }
        }
        startQuiz();
    }

    private void startQuiz() {
        active = true;
        isQuizRunning = true;
        log.info("Starting Quiz: " + this.id);
        broadcastNextQuestion();
    }

    private void broadcastNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            broadcastFinalRanking();
            scheduler.schedule(this::endQuiz, new Date(System.currentTimeMillis() + 10000));
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        broadcastService.broadcastToAllParticipants(participants.keySet(), formatQuestionWithOptions(question));

        scheduler.schedule(this::closeCurrentQuestion, new Date(System.currentTimeMillis() + 10000));
    }

    private String formatQuestionWithOptions(Question question) {
        return "Question: "+question.getQuestion()+"; " + String.join(":",question.getOptions());
    }

    private void closeCurrentQuestion() {
        broadcastCurrentRanking();
        currentQuestionIndex++;
        broadcastNextQuestion();
    }

    private void broadcastCurrentRanking() {
        String ranking = formatRanking();
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Current Ranking #\n" + ranking);
    }

    private void broadcastFinalRanking() {
        String ranking = formatRanking();
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Quiz Finished! Final Ranking #\n" + ranking);
    }

    private void endQuiz() {
        active = false;
        broadcastService.broadcastToAllParticipants(participants.keySet(), "Quiz session ended. Goodbye!");
        participants.keySet().forEach(session -> {
            try {
                session.close();
            } catch (Exception e) {
                log.error("Exception while closing the session: " + e.getMessage());
                e.printStackTrace();
            }
        });
        participants.clear();
    }

    private String formatRanking() {
        StringBuilder ranking = new StringBuilder();
        scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> ranking.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        return ranking.toString();
    }

    public void submitAnswer(WebSocketSession session, String answer) {
        String userName = participants.get(session);
        if (userName == null || currentQuestionIndex >= questions.size()) {
            broadcastService.sendMessageToParticipant(session, "You are not part of this quiz or the quiz has ended.");
            throw new IllegalArgumentException("You are not part of this quiz or the quiz has ended.");
        }

        Question question = questions.get(currentQuestionIndex);
        if (question.isCorrectAnswer(answer)) {
            scores.computeIfPresent(userName, (key, value) -> value + 1);
            broadcastService.broadcastToAllParticipants(participants.keySet(), "Current Ranking #\n" + formatRanking());
        }
    }

    private List<Question> initializeQuestions() {
        return List.of(
                new Question("What is the synonym of the word \"Happy\"?",
                        new String[]{"Angry", "Sad", "Joyful", "Tired"}, "Joyful"),
                new Question("What does the word \"Big\" mean?",
                        new String[]{"Large", "Small", "Thin", "Fast"}, "Large"),
                new Question("Fill in the blank: \"She is a very _______ girl who always smiles.\"?",
                        new String[]{"Rude", "Kind", "Sad", "Lazy"}, "Kind"),
                new Question("What is the meaning of the word \"Obfuscate\"?",
                        new String[]{"Simplify", "Complicate or confuse", "Clarify", "Justify"}, "Complicate or confuse"),
                new Question("What is the synonym of the word \"Eloquent\"?",
                        new String[]{"Hesitant", "Articulate", "Quiet", "Fast"}, "Articulate")
        );
    }
}
