package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import cz.cervenka.parallelizationissues.util.Agent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class SimulationService {

    private final SimulationWebSocketHandler webSocketHandler;

    public SimulationService(SimulationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        //this.webSocketHandler.setOnConnectionEstablishedCallback(this::startSimulation);
    }


    public void simulateDeadlock(SimulationTask task) {
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

    public void simulateStarvation(SimulationTask task) {
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

    public void simulateLivelock(SimulationTask task) {
        System.out.println("Livelock simulation started...");
        webSocketHandler.broadcast("/ws/problems/livelock", "Livelock simulation started...");

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (agent2.isActing()) {
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
                    if (agent1.isActing()) {
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

    private void startSimulation() {
        // Once the WebSocket connection is established, you can start the simulation
        // Example: Call the actual simulation methods here or trigger whatever you need
        simulateDeadlock(new SimulationTask());  // For example, starting the deadlock simulation
    }

}