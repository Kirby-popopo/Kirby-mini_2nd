package com.example.Kirby_mini_2nd.config;

import com.example.Kirby_mini_2nd.service.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    // 메시지 브로커 - 중간에서 메시지를 처리해주는 중개역할

    private final MyWebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/{roomId}")
                .setAllowedOrigins("*");
    }
}
