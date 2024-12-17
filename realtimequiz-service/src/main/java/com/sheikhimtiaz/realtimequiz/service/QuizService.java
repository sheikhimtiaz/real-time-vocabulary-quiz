package com.sheikhimtiaz.realtimequiz.service;

import com.sheikhimtiaz.realtimequiz.engine.QuizEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.text.MessageFormat;

@Service
public class QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizEngine quizEngine;
    private final BroadcastService broadcastService;

    public QuizService(QuizEngine quizEngine, BroadcastService broadcastService) {
        this.quizEngine = quizEngine;
        this.broadcastService = broadcastService;
    }

    public void processMessage(WebSocketSession session, String payload) {
        String[] parts = payload.split(" ", 2);
        if (parts.length < 2) {
            log.error("Invalid message format: " + payload);
            broadcastService.sendMessageToParticipant(session, "Invalid message format. Expected: <command> <data>");
        }

        String command = parts[0];
        String data = parts[1];
        String quizId = extractQuizId(session);
        log.info(MessageFormat.format("QuizId: {0} Command {1} Data {2}",quizId, command, data));
        switch (command) {
            case "JOIN":
                handleJoinCommand(quizId, session, data);
                return;
            case "ANSWER":
                handleAnswerCommand(quizId, session, data);
                return;
            default:
                handleUnknownCommand(session, payload);
        }
    }

    private String extractQuizId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public void handleDisconnect(WebSocketSession session) {
        String quizId = extractQuizIdFromSession(session);
        quizEngine.removeParticipant(quizId, session);
    }

    private String extractQuizIdFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void handleJoinCommand(String quizId, WebSocketSession session, String username) {
        quizEngine.addParticipantToQuiz(quizId, username, session);
    }

    private void handleAnswerCommand(String quizId, WebSocketSession session, String answer) {
        quizEngine.submitAnswer(quizId, session, answer);
    }

    private void handleUnknownCommand(WebSocketSession session, String payload) {
        broadcastService.sendMessageToParticipant(session, "Unrecognized command: " + payload);
    }
}
