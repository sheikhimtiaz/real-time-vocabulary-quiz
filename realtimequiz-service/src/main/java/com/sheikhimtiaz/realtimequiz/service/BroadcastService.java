package com.sheikhimtiaz.realtimequiz.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Service
public class BroadcastService {

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
                e.printStackTrace();
            }
        }
    }
}