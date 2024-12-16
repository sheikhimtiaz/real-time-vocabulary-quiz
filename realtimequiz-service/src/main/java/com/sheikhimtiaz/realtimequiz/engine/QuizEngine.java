package com.sheikhimtiaz.realtimequiz.engine;

import com.sheikhimtiaz.realtimequiz.model.Quiz;
import com.sheikhimtiaz.realtimequiz.service.BroadcastService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuizEngine {
    private final ConcurrentHashMap<String, Quiz> activeQuizzes;
    private final BroadcastService broadcastService;
    private final ThreadPoolTaskScheduler scheduler;

    public QuizEngine(BroadcastService broadcastService, ThreadPoolTaskScheduler scheduler) {
        this.broadcastService = broadcastService;
        this.scheduler = scheduler;
        this.activeQuizzes = new ConcurrentHashMap<>();
    }

    public Quiz createQuiz(String quizId) {
        Quiz quiz = new Quiz(quizId, scheduler, broadcastService);
        activeQuizzes.put(quizId, quiz);
        return quiz;
    }

    public Quiz getQuiz(String quizId) {
        return activeQuizzes.get(quizId);
    }

    public void addParticipantToQuiz(String quizId, String userName, WebSocketSession session) {
        Quiz quiz = activeQuizzes.get(quizId);
        if(Objects.isNull(quiz)) {
            quiz = createQuiz(quizId);
        }
        quiz.addParticipant(userName, session);
    }

    public void submitAnswer(String quizId, WebSocketSession session, String answer) {
        Quiz quiz = activeQuizzes.get(quizId);
        if (quiz != null) {
            quiz.submitAnswer(session, answer);
        }
    }

    public void removeParticipant(String quizId, WebSocketSession session) {
        Quiz quiz = activeQuizzes.get(quizId);
        if (quiz != null) {
            quiz.removeParticipant(session);
        }
    }
}
