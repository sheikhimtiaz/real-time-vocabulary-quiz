package com.sheikhimtiaz.realtimequiz.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionId(session);

        sessions.computeIfAbsent(sessionId, k -> new CopyOnWriteArraySet<>()).add(session);

        System.out.println("Client connected to session: " + sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = getSessionId(session);

        System.out.println("Received message: " + payload + " in session: " + sessionId);

        for (WebSocketSession client : sessions.get(sessionId)) {
            if (client.isOpen()) {
                client.sendMessage(new TextMessage("Broadcast: " + payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = getSessionId(session);

        CopyOnWriteArraySet<WebSocketSession> clients = sessions.get(sessionId);
        if (clients != null) {
            clients.remove(session);

            if (clients.isEmpty()) {
                sessions.remove(sessionId);
                System.out.println("Session " + sessionId + " is empty and removed.");
            }
        }

        System.out.println("Client disconnected from session: " + sessionId);
    }

    private String getSessionId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
