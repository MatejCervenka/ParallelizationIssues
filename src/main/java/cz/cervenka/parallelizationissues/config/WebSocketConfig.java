package cz.cervenka.parallelizationissues.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simulationWebSocketHandler(), "/ws/problems/deadlock")
                .setAllowedOrigins("*");
        registry.addHandler(simulationWebSocketHandler(), "/ws/problems/livelock")
                .setAllowedOrigins("*");
        registry.addHandler(simulationWebSocketHandler(), "/ws/problems/starvation")
                .setAllowedOrigins("*");
        registry.addHandler(simulationWebSocketHandler(), "/ws/solutions/deadlock")
                .setAllowedOrigins("*");
        registry.addHandler(simulationWebSocketHandler(), "/ws/solutions/livelock")
                .setAllowedOrigins("*");
        registry.addHandler(simulationWebSocketHandler(), "/ws/solutions/starvation")
                .setAllowedOrigins("*");
    }

    @Bean
    public SimulationWebSocketHandler simulationWebSocketHandler() {
        return new SimulationWebSocketHandler();
    }
}
