package com.project.app.configuration;

import com.project.app.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private TokenService tokenService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for messages FROM server TO client
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages FROM client TO server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-endpoint")
                .setAllowedOrigins("http://localhost:3000",
                        "http://" + SecurityConfig.CURRENT_IP + ":8080",
                        "http://" + SecurityConfig.CURRENT_IP,
                        SecurityConfig.CURRENT_IP,
                        "https://lyriclabs.co.uk",
                        "https://cst.dev", "https://cst.dev/lyriclabs")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        URI uri = request.getURI();
                        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
                        String token = queryParams.getFirst("token");

                        return tokenService.extractPrincipalFromToken(token);
                    }})
                .withSockJS();
    }


}


