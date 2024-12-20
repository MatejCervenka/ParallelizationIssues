package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.Agent;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Service class for handling the simulation of concurrency issue solutions.
 * This class manages different types of solutions for deadlock, starvation, and livelock.
 */
@Service
@Component
public class SolutionService {

    private final SimulationWebSocketHandler webSocketHandler;
    private Runnable currentSimulation;

    /**
     * Constructs a new SolutionService with the given SimulationWebSocketHandler.
     *
     * @param webSocketHandler The SimulationWebSocketHandler to be used for broadcasting simulation updates.
     */
    public SolutionService(SimulationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketHandler.setOnConnectionEstablishedCallback(this::startSimulation);
    }

    /**
     * Solves the deadlock simulation by initiating two threads that simulate a deadlock scenario.
     * Broadcasts the progress of the simulation through WebSocket.
     *
     * @param task The SimulationTask to be used for managing threads and simulation progress.
     */
    public void solveDeadlock(SimulationTask task) {
        runSimulation(() -> runSolveDeadlock(task));
    }

    /**
     * Runs the deadlock simulation by creating two threads that lock resources in conflicting orders.
     * Simulates a deadlock scenario and broadcasts each step.
     *
     * @param task The SimulationTask to manage threads.
     */
    public void runSolveDeadlock(SimulationTask task) {
        System.out.println("Deadlock solutions simulation started...");
        webSocketHandler.broadcast("/ws/solutions/deadlock", "Deadlock solutions simulation started...");

        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread thread1 = new Thread(() -> {
            try {
                synchronized (resourceA) {
                    System.out.println("Thread 1: Locked Resource A.");
                    webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 1: Locked Resource A.");
                    Thread.sleep(1000);
                    synchronized (resourceB) {
                        System.out.println("Thread 1: Locked Resource B.");
                        webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 1: Locked Resource B.");
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 1 interrupted.");
                webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 1 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked Resource A.");
                    webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 2: Locked Resource A.");
                    Thread.sleep(1000);
                    synchronized (resourceB) {
                        System.out.println("Thread 2: Locked Resource B.");
                        webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 2: Locked Resource B.");
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 2 interrupted.");
                webSocketHandler.broadcast("/ws/solutions/deadlock", "Thread 2 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        task.addThread(thread1);
        task.addThread(thread2);

        task.startAll();
    }

    /**
     * Solves the starvation simulation by initiating threads with different priorities.
     * The threads attempt to lock a shared resource, with one having a higher priority.
     * Broadcasts the progress of the simulation through WebSocket.
     *
     * @param task The SimulationTask to manage threads and simulation progress.
     */
    public void solveStarvation(SimulationTask task) {
        runSimulation(() -> runSolveStarvation(task));
    }

    /**
     * Runs the starvation simulation by creating two threads with a fair lock mechanism.
     * Simulates a starvation scenario where a low-priority thread waits for a high-priority thread to release a lock.
     *
     * @param task The SimulationTask to manage threads.
     */
    private void runSolveStarvation(SimulationTask task) {
        System.out.println("Starvation solutions simulation started...");
        webSocketHandler.broadcast("/ws/solutions/starvation", "Starvation solutions simulation started...");

        Lock reservationLock = new ReentrantLock(true); // Fair lock

        Thread highPriorityThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    reservationLock.lock();
                    System.out.println("High-priority user: Reserved a slot.");
                    webSocketHandler.broadcast("/ws/solutions/starvation", "High-priority user: Reserved a slot.");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    reservationLock.unlock();
                }
            }
        });

        Thread lowPriorityThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    reservationLock.lock();
                    System.out.println("Low-priority user: Reserved a slot.");
                    webSocketHandler.broadcast("/ws/solutions/starvation", "Low-priority user: Reserved a slot.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    reservationLock.unlock();
                }
            }
        });

        task.addThread(highPriorityThread);
        task.addThread(lowPriorityThread);
        task.startAll();
    }

    /**
     * Solves the livelock simulation by initiating two agents that attempt to adjust their state.
     * Broadcasts the progress of the simulation through WebSocket.
     *
     * @param task The SimulationTask to manage threads and simulation progress.
     */
    public void solveLivelock(SimulationTask task) {
        runSimulation(() -> runSolveLivelock(task));
    }

    /**
     * Runs the livelock simulation by creating two agents that adjust their actions while avoiding a livelock.
     * The agents attempt to adjust their behavior to break the livelock after a set number of attempts.
     *
     * @param task The SimulationTask to manage threads.
     */
    public void runSolveLivelock(SimulationTask task) {
        System.out.println("Livelock solutions simulation started...");
        webSocketHandler.broadcast("/ws/solutions/livelock", "Livelock solutions simulation started...");

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            int attempts = 0;
            while (true) {
                if (!agent2.isActing()) {
                    agent1.act();
                    System.out.println("Thread 1: Adjusting...");
                    webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 1: Adjusting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 1 interrupted.");
                        webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 1 interrupted.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (++attempts > 5) {
                    System.out.println("Thread 1: Breaking livelock.");
                    webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 1: Breaking livelock.");
                    break;
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            int attempts = 0;
            while (true) {
                if (!agent1.isActing()) {
                    agent2.act();
                    System.out.println("Thread 2: Adjusting...");
                    webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 2: Adjusting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 2 interrupted.");
                        webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 2 interrupted.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (++attempts > 5) {
                    System.out.println("Thread 2: Breaking livelock.");
                    webSocketHandler.broadcast("/ws/solutions/livelock", "Thread 2: Breaking livelock.");
                    break;
                }
            }
        });

        task.addThread(thread1);
        task.addThread(thread2);

        task.startAll();
    }

    /**
     * Runs a simulation by setting the current simulation to the provided task and checking if a WebSocket connection is established.
     * If the connection is established, it starts the simulation immediately.
     *
     * @param simulation The Runnable representing the simulation task to be run.
     */
    private void runSimulation(Runnable simulation) {
        synchronized (this) {
            this.currentSimulation = simulation;
            System.out.println("Simulation set. Checking WebSocket connection status...");

            if (webSocketHandler.isConnectionEstablished()) {
                System.out.println("WebSocket connection is ready. Starting simulation immediately.");
                startSimulation();
            } else {
                System.out.println("WebSocket connection not yet established. Waiting...");
            }
        }
    }

    /**
     * Starts the simulation if a simulation task is set and the WebSocket connection is established.
     */
    private void startSimulation() {
        synchronized (this) {
            System.out.println("Checking if simulation can be started...");
            if (currentSimulation != null) {
                System.out.println("Starting simulation...");
                currentSimulation.run();
                currentSimulation = null;
            } else {
                System.out.println("No simulation to start.");
            }
        }
    }
}
