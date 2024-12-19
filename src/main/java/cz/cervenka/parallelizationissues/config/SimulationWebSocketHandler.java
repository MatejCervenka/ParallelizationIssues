package cz.cervenka.parallelizationissues.config;

import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessionMap = new ConcurrentHashMap<>();

    // Set the callback to be called once the WebSocket connection is established
    @Setter
    private Runnable onConnectionEstablishedCallback;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(session, "connected");
        System.out.println("WebSocket connection established: " + session.getId());

        // Trigger callback if set
        if (onConnectionEstablishedCallback != null) {
            onConnectionEstablishedCallback.run();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
    }

    public synchronized void broadcast(String endpoint, String message) {
        sessionMap.forEach((session, status) -> {
            if (session.isOpen() && session.getUri().getPath().equals(endpoint)) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("WebSocket send failed for session " + session.getId() + ": " + e.getMessage());
                    try {
                        session.close(CloseStatus.SERVER_ERROR);
                    } catch (IOException closeEx) {
                        System.err.println("Failed to close WebSocket session: " + closeEx.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

}
