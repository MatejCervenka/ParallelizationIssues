package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import cz.cervenka.parallelizationissues.util.Agent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * Service class responsible for managing concurrency problem simulations.
 * This class handles the simulation of deadlock, starvation, and livelock issues and interacts with the WebSocketHandler
 * to broadcast simulation updates.
 */
@Service
@Component
public class SimulationService {

    private final SimulationWebSocketHandler webSocketHandler;
    private Runnable currentSimulation;

    /**
     * Constructs a new SimulationService with the given SimulationWebSocketHandler.
     * Initializes the WebSocket connection callback to start simulations when the connection is established.
     *
     * @param webSocketHandler The SimulationWebSocketHandler for managing WebSocket interactions.
     */
    public SimulationService(SimulationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketHandler.setOnConnectionEstablishedCallback(this::startSimulation);
    }

    /**
     * Starts a deadlock simulation and broadcasts messages to WebSocket clients.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void simulateDeadlock(SimulationTask task) {
        runSimulation(() -> runSimulateDeadlock(task));
    }

    /**
     * Executes the deadlock simulation logic.
     * It creates two threads that attempt to lock two resources in a way that causes a deadlock.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void runSimulateDeadlock(SimulationTask task) {
        System.out.println("Deadlock simulation started...");
        webSocketHandler.broadcast("/ws/problems/deadlock", "Deadlock simulation started...");

        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked Resource A.");
                webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 1: Locked Resource A.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread 1 interrupted.");
                    webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 1 interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("Thread 1: Waiting to lock Resource B...");
                webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 1: Waiting to lock Resource B...");
                synchronized (resourceB) {
                    System.out.println("Thread 1: Locked Resource B.");
                    webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 1: Locked Resource B.");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Thread 2: Locked Resource B.");
                webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 2: Locked Resource B.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread 2 interrupted.");
                    webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 2 interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("Thread 2: Waiting to lock Resource A...");
                webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 2: Waiting to lock Resource A...");
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked Resource A.");
                    webSocketHandler.broadcast("/ws/problems/deadlock", "Thread 2: Locked Resource A.");
                }
            }
        });

        task.addThread(thread1);
        task.addThread(thread2);
        task.startAll();
    }

    /**
     * Starts a starvation simulation and broadcasts messages to WebSocket clients.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void simulateStarvation(SimulationTask task) {
        runSimulation(() -> runSimulateStarvation(task));
    }

    /**
     * Executes the starvation simulation logic.
     * It creates two threads: one for a high-priority task and one for a low-priority task.
     * The low-priority task simulates starvation by waiting for the high-priority task to complete.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void runSimulateStarvation(SimulationTask task) {
        System.out.println("Starvation simulation started...");
        webSocketHandler.broadcast("/ws/problems/starvation", "Starvation simulation started...");

        Runnable highPriorityTask = getRunnable(webSocketHandler);

        Runnable lowPriorityTask = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Low-priority user: Waiting for a chance to book...");
                webSocketHandler.broadcast("/ws/problems/starvation", "Low-priority user: Waiting for a chance to book...");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("Low-priority user interrupted.");
                    webSocketHandler.broadcast("/ws/problems/starvation", "Low-priority user interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread highPriorityThread = new Thread(highPriorityTask);
        Thread lowPriorityThread = new Thread(lowPriorityTask);

        task.addThread(highPriorityThread);
        task.addThread(lowPriorityThread);
        task.startAll();
    }

    /**
     * Creates and returns a runnable task for a high-priority user.
     * The task simulates the high-priority user booking a slot periodically.
     *
     * @param webSocketHandler The SimulationWebSocketHandler for broadcasting simulation updates.
     * @return The runnable task for the high-priority user.
     */
    private static Runnable getRunnable(SimulationWebSocketHandler webSocketHandler) {
        Object reservationLock = new Object();

        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (reservationLock) {
                    System.out.println("High-priority user: Reserved a slot.");
                    webSocketHandler.broadcast("/ws/problems/starvation", "High-priority user: Reserved a slot.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("High-priority user interrupted.");
                        webSocketHandler.broadcast("/ws/problems/starvation", "High-priority user interrupted.");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }

    /**
     * Starts a livelock simulation and broadcasts messages to WebSocket clients.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void simulateLivelock(SimulationTask task) {
        runSimulation(() -> runSimulateLivelock(task));
    }

    /**
     * Executes the livelock simulation logic.
     * Two agents act in a way that causes them to keep adjusting without making progress,
     * resulting in a livelock.
     *
     * @param task The SimulationTask that manages the simulation threads.
     */
    public void runSimulateLivelock(SimulationTask task) {
        System.out.println("Livelock simulation started...");
        webSocketHandler.broadcast("/ws/problems/livelock", "Livelock simulation started...");

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (!agent2.isActing()) {
                        agent1.act();
                        System.out.println("Thread 1: Adjusting...");
                        webSocketHandler.broadcast("/ws/problems/livelock", "Thread 1: Adjusting...");
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 1 interrupted.");
                webSocketHandler.broadcast("/ws/problems/livelock", "Thread 1 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (!agent1.isActing()) {
                        agent2.act();
                        System.out.println("Thread 2: Adjusting...");
                        webSocketHandler.broadcast("/ws/problems/livelock", "Thread 2: Adjusting...");
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 2 interrupted.");
                webSocketHandler.broadcast("/ws/problems/livelock", "Thread 2 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        task.addThread(thread1);
        task.addThread(thread2);
        task.startAll();
    }

    /**
     * Runs the provided simulation in a synchronized manner.
     * It checks the WebSocket connection status before starting the simulation.
     *
     * @param simulation The simulation to be executed.
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
     * Starts the simulation if it has been set and the WebSocket connection is established.
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