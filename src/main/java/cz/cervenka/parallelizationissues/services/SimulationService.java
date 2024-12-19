package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.objects.SimulationTask;
import cz.cervenka.parallelizationissues.objects.Agent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {

    private final List<String> logs = new ArrayList<>();


    public void simulateDeadlock(SimulationTask task) {
        System.out.println("Deadlock simulation started...");

        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked Resource A.");
                try {
                    Thread.sleep(1000); // Simulate some work
                } catch (InterruptedException e) {
                    System.out.println("Thread 1 interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("Thread 1: Waiting to lock Resource B...");
                synchronized (resourceB) {
                    System.out.println("Thread 1: Locked Resource B.");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Thread 2: Locked Resource B.");
                try {
                    Thread.sleep(1000); // Simulate some work
                } catch (InterruptedException e) {
                    System.out.println("Thread 2 interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("Thread 2: Waiting to lock Resource A...");
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked Resource A.");
                }
            }
        });

        // Add threads to the task for later interruption
        task.addThread(thread1);
        task.addThread(thread2);

        // Start all threads
        task.startAll();
    }


    public void simulateStarvation(SimulationTask task) {
        Runnable highPriorityTask = getRunnable();

        Runnable lowPriorityTask = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Low-priority user: Waiting for a chance to book...");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("Low-priority user interrupted.");
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

    private static Runnable getRunnable() {
        Object reservationLock = new Object();

        Runnable highPriorityTask = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (reservationLock) {
                    System.out.println("High-priority user: Reserved a slot.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("High-priority user interrupted.");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        return highPriorityTask;
    }


    public void simulateLivelock(SimulationTask task) {
        System.out.println("Livelock simulation started...");

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (agent2.isActing()) {
                        agent1.act();
                        System.out.println("Thread 1: Adjusting...");
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 1 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (agent1.isActing()) {
                        agent2.act();
                        System.out.println("Thread 2: Adjusting...");
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 2 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        // Add threads to the task
        task.addThread(thread1);
        task.addThread(thread2);

        // Start all threads
        task.startAll();
    }


    public String getLogs() {
        return String.join("\n", logs);
    }
}