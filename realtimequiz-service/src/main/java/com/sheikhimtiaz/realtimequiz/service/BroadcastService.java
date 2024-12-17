package com.sheikhimtiaz.realtimequiz.service;

import com.sheikhimtiaz.realtimequiz.model.Quiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Service
public class BroadcastService {
    private static final Logger log = LoggerFactory.getLogger(BroadcastService.class);

    public void broadcastToAllParticipants(Set<WebSocketSession> sessions, String message) {
        for (WebSocketSession session : sessions) {
            sendMessageToParticipant(session, message);
        }
    }

    public void sendMessageToParticipant(WebSocketSession session, String message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("Exception while sending message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}