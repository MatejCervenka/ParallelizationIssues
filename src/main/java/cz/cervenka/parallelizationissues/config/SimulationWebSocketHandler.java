package cz.cervenka.parallelizationissues.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessionMap = new ConcurrentHashMap<>();
    private final AtomicReference<Runnable> onConnectionEstablishedCallback = new AtomicReference<>();

    public Map<WebSocketSession, String> getSessionMap() {
        return sessionMap;
    }

    public void setOnConnectionEstablishedCallback(Runnable callback) {
        onConnectionEstablishedCallback.set(callback); // Use AtomicReference for thread safety
    }

    public boolean isConnectionEstablished() {
        return !sessionMap.isEmpty();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(session, "connected");
        System.out.println("WebSocket connection established: " + session.getId());

        Runnable callback = onConnectionEstablishedCallback.get();
        if (callback != null) {
            System.out.println("Executing onConnectionEstablishedCallback...");
            callback.run(); // Execute the latest callback
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message from session " + session.getId() + ": " + message.getPayload());
    }

    public synchronized void broadcast(String endpoint, String message) {
        sessionMap.forEach((session, status) -> {
            if (session.isOpen() && session.getUri() != null && session.getUri().getPath().equals(endpoint)) {
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

    public synchronized void clearAllSessions() {
        sessionMap.forEach((session, status) -> {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.NORMAL);
                }
            } catch (IOException e) {
                System.err.println("Failed to close WebSocket session: " + e.getMessage());
            }
        });
        sessionMap.clear();
    }
}
