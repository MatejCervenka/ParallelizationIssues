package cz.cervenka.parallelizationissues.util;

import java.util.ArrayList;
import java.util.List;

public class SimulationTask {
    private final List<Thread> threads = new ArrayList<>();

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    public void startAll() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void interruptAll() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}