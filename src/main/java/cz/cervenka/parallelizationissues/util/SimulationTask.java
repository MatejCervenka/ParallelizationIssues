package cz.cervenka.parallelizationissues.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The SimulationTask class manages a collection of threads.
 * It allows adding threads to a list, starting all threads simultaneously, and interrupting all threads.
 */
public class SimulationTask {
    private final List<Thread> threads = new ArrayList<>();

    /**
     * Adds a thread to the list of threads managed by the simulation task.
     *
     * @param thread The thread to be added to the task.
     */
    public void addThread(Thread thread) {
        threads.add(thread);
    }

    /**
     * Starts all threads in the list. Each thread is started one by one.
     */
    public void startAll() {
        for (Thread thread : threads) {
            thread.start();  // Starts the thread
        }
    }

    /**
     * Interrupts all threads that are alive.
     * If a thread is still running, it will be interrupted.
     */
    public void interruptAll() {
        threads.forEach(thread -> {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        });
    }
}