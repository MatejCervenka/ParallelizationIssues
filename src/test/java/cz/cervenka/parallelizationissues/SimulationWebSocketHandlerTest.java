package cz.cervenka.parallelizationissues;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SimulationWebSocketHandlerTest {

    private SimulationWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        handler = new SimulationWebSocketHandler();
        session = mock(WebSocketSession.class);
    }

    @Test
    void testAfterConnectionEstablished() throws Exception {
        handler.afterConnectionEstablished(session);

        assertTrue(handler.isConnectionEstablished());
        verify(session, times(1)).getId();
    }


    @Test
    void testHandleTextMessage() throws Exception {
        TextMessage message = new TextMessage("Test message");
        handler.handleTextMessage(session, message);

        verify(session, times(1)).getId();
    }
}
