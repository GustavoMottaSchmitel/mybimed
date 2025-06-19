package motta.dev.MyBimed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configurações
    private static final long[] HEARTBEAT = {10000, 10000}; // 10 segundos
    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:*",
            "https://mybimed.com",
            "https://*.mybimed.com"
    };

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configuração do broker de mensagens
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(HEARTBEAT)
                .setTaskScheduler(heartbeatScheduler());

        // Prefixo para mensagens direcionadas a @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Prefixo para mensagens de usuário específico
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(ALLOWED_ORIGINS)
                .withSockJS()
                .setHeartbeatTime(HEARTBEAT[0]);

        // Endpoint alternativo sem SockJS para clientes nativos
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns(ALLOWED_ORIGINS);
    }

    @Bean
    public TaskScheduler heartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        return scheduler;
    }
}