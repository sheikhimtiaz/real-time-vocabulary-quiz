package com.sheikhimtiaz.realtimequiz.config;

import com.sheikhimtiaz.realtimequiz.service.QuizService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private final QuizService quizService;

    public WebSocketMessageHandler(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        quizService.processMessage(session, payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        quizService.handleDisconnect(session);
    }
}