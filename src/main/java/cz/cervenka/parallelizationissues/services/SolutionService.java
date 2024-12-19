package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.Agent;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class SolutionService {

    private final SimulationWebSocketHandler webSocketHandler;

    public SolutionService(SimulationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        //this.webSocketHandler.setOnConnectionEstablishedCallback(this::startSimulation);
    }


    public void solveDeadlock(SimulationTask task) {
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


    public void solveStarvation(SimulationTask task) {
        System.out.println("Starvation solutions simulation started...");
        webSocketHandler.broadcast("/ws/solutions/starvation", "Starvation solutions simulation started...");

        Lock reservationLock = new ReentrantLock(true); // Enable fairness

        Thread highPriorityThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    reservationLock.lock();
                    System.out.println("High-priority user: Reserved a slot.");
                    webSocketHandler.broadcast("/ws/solutions/starvation", "High-priority user: Reserved a slot.");
                    Thread.sleep(500); // Simulate work
                } catch (InterruptedException e) {
                    System.out.println("High-priority user interrupted.");
                    webSocketHandler.broadcast("/ws/solutions/starvation", "High-priority user interrupted.");
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
                    Thread.sleep(1000); // Simulate work
                } catch (InterruptedException e) {
                    System.out.println("Low-priority user interrupted.");
                    webSocketHandler.broadcast("/ws/solutions/starvation", "Low-priority user interrupted.");
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


    public void solveLivelock(SimulationTask task) {
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

    private void startSimulation() {
        // Once the WebSocket connection is established, you can start the simulation
        // Example: Call the actual simulation methods here or trigger whatever you need
        solveDeadlock(new SimulationTask());  // For example, starting the deadlock simulation
    }
}