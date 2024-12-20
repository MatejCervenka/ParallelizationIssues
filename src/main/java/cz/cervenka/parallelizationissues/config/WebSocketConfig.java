package cz.cervenka.parallelizationissues.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration class to register WebSocket handlers for different WebSocket endpoints.
 * It enables WebSocket support and sets up the WebSocketHandlerRegistry to register the necessary handlers
 * for handling WebSocket connections to specific endpoints.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * Registers the WebSocket handlers for the simulation problem and solution endpoints.
     * Each endpoint is mapped to a corresponding handler and allows connections from any origin.
     *
     * @param registry The WebSocketHandlerRegistry used to register the WebSocket handlers.
     */
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

    /**
     * Creates a bean for the SimulationWebSocketHandler to be used for handling WebSocket connections.
     *
     * @return A new instance of the SimulationWebSocketHandler.
     */
    @Bean
    public SimulationWebSocketHandler simulationWebSocketHandler() {
        return new SimulationWebSocketHandler();
    }
}
