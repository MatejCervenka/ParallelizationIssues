package cz.cervenka.parallelizationissues;

import cz.cervenka.parallelizationissues.util.Agent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    @Test
    void testAct() {
        Agent agent = new Agent();
        assertFalse(agent.isActing());

        agent.act();
        assertTrue(agent.isActing());

        agent.act();
        assertFalse(agent.isActing());
    }

    @Test
    void testIsActing() {
        Agent agent = new Agent();
        assertFalse(agent.isActing());

        agent.act();
        assertTrue(agent.isActing());

        agent.act();
        assertFalse(agent.isActing());
    }

}
