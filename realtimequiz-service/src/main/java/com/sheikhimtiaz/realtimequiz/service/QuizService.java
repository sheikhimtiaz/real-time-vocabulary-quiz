package com.sheikhimtiaz.realtimequiz.service;

import com.sheikhimtiaz.realtimequiz.engine.QuizEngine;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class QuizService {

    private final QuizEngine quizEngine;
    private final BroadcastService broadcastService;

    public QuizService(QuizEngine quizEngine, BroadcastService broadcastService) {
        this.quizEngine = quizEngine;
        this.broadcastService = broadcastService;
    }

    public void processMessage(WebSocketSession session, String payload) {

        String[] parts = payload.split(" ");
        if (parts.length < 3) {
            broadcastService.sendMessageToParticipant(session, "Invalid message format. Expected: <command> <data>");
        }

        String command = parts[0];
        String quizId = parts[1];
        String data = parts[2];
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
