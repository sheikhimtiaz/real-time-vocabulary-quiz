package com.sheikhimtiaz.realtimequiz.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    public WebSocketMessageHandler() {
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(message.getPayload());
        session.sendMessage(new TextMessage("Received: " + message.getPayload()));
    }
}
