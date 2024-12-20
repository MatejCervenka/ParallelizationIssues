package cz.cervenka.parallelizationissues;

import cz.cervenka.parallelizationissues.util.SimulationTask;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class SimulationTaskTest {

    @Test
    void testAddAndStartThreads() {
        SimulationTask task = new SimulationTask();
        Thread thread = mock(Thread.class);

        task.addThread(thread);
        task.startAll();

        verify(thread, times(1)).start();
    }
}