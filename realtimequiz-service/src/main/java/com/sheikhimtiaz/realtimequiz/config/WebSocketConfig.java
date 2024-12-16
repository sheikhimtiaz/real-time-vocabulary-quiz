package com.sheikhimtiaz.realtimequiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketQuizMessageHandler webSocketQuizMessageHandler;
    private final WebSocketMessageHandler webSocketMessageHandler;

    public WebSocketConfig(WebSocketQuizMessageHandler webSocketQuizMessageHandler,
                           WebSocketMessageHandler webSocketMessageHandler) {
        this.webSocketQuizMessageHandler = webSocketQuizMessageHandler;
        this.webSocketMessageHandler = webSocketMessageHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketMessageHandler, "/quiz")
                .addHandler(webSocketQuizMessageHandler, "/quiz/{sessionId}")
                .setAllowedOrigins("*");
    }
}


