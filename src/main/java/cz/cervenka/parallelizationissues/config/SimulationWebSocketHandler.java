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

/**
 * WebSocket handler for managing WebSocket sessions and broadcasting messages.
 * This component manages the WebSocket connection and provides functionalities
 * like broadcasting messages to connected sessions and handling connection events.
 */
@Component
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessionMap = new ConcurrentHashMap<>();
    private final AtomicReference<Runnable> onConnectionEstablishedCallback = new AtomicReference<>();

    /**
     * Retrieves the session map of all active WebSocket sessions.
     *
     * @return The map of WebSocket sessions.
     */
    public Map<WebSocketSession, String> getSessionMap() {
        return sessionMap;
    }

    /**
     * Sets the callback to be executed when a WebSocket connection is established.
     *
     * @param callback The callback to be executed.
     */
    public void setOnConnectionEstablishedCallback(Runnable callback) {
        onConnectionEstablishedCallback.set(callback);
    }

    /**
     * Checks if the WebSocket connection has been established by verifying if there are any active sessions.
     *
     * @return True if a WebSocket connection is established, otherwise false.
     */
    public boolean isConnectionEstablished() {
        return !sessionMap.isEmpty();
    }

    /**
     * Called when a WebSocket connection is established.
     * Adds the session to the session map and executes the connection established callback if set.
     *
     * @param session The WebSocket session that has been established.
     * @throws Exception if any error occurs during connection setup.
     */
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

    /**
     * Handles incoming text messages from WebSocket sessions.
     * Currently, it just logs the received message.
     *
     * @param session The WebSocket session from which the message was received.
     * @param message The text message received.
     * @throws Exception if any error occurs during message handling.
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message from session " + session.getId() + ": " + message.getPayload());
    }

    /**
     * Broadcasts a message to all active WebSocket sessions.
     * The message is sent only to the sessions whose URI path matches the given endpoint.
     *
     * @param endpoint The endpoint path to which the message should be sent.
     * @param message  The message to be broadcasted.
     */
    public synchronized void broadcast(String endpoint, String message) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
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

    /**
     * Called when a WebSocket connection is closed.
     * Removes the session from the session map.
     *
     * @param session The WebSocket session that has been closed.
     * @param status  The status of the connection closure.
     * @throws Exception if any error occurs during connection closure.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    /**
     * Clears all active WebSocket sessions by closing them.
     * This is a synchronized operation to ensure that sessions are closed in a thread-safe manner.
     */
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
