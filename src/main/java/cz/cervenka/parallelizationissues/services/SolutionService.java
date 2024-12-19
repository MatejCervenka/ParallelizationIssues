package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.objects.Agent;
import cz.cervenka.parallelizationissues.objects.SimulationTask;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolutionService {

    private final List<String> logs = new ArrayList<>();


    public void solveDeadlock(SimulationTask task) {
        System.out.println("Deadlock solution simulation started...");

        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread thread1 = new Thread(() -> {
            try {
                synchronized (resourceA) {
                    System.out.println("Thread 1: Locked Resource A.");
                    Thread.sleep(1000); // Simulate work
                    synchronized (resourceB) {
                        System.out.println("Thread 1: Locked Resource B.");
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 1 interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                synchronized (resourceA) { // Lock resources in the same order
                    System.out.println("Thread 2: Locked Resource A.");
                    Thread.sleep(1000); // Simulate work
                    synchronized (resourceB) {
                        System.out.println("Thread 2: Locked Resource B.");
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



    public void solveStarvation(SimulationTask task) {
        System.out.println("Starvation solution simulation started...");

        Lock reservationLock = new ReentrantLock(true); // Enable fairness

        Thread highPriorityThread = new Thread(() -> {
            while (true) {
                try {
                    reservationLock.lock();
                    System.out.println("High-priority user: Reserved a slot.");
                    Thread.sleep(500); // Simulate work
                } catch (InterruptedException e) {
                    System.out.println("High-priority user interrupted.");
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    reservationLock.unlock();
                }
            }
        });

        Thread lowPriorityThread = new Thread(() -> {
            while (true) {
                try {
                    reservationLock.lock();
                    System.out.println("Low-priority user: Reserved a slot.");
                    Thread.sleep(1000); // Simulate work
                } catch (InterruptedException e) {
                    System.out.println("Low-priority user interrupted.");
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    reservationLock.unlock();
                }
            }
        });

        // Add threads to the task
        task.addThread(highPriorityThread);
        task.addThread(lowPriorityThread);

        // Start all threads
        task.startAll();
    }



    public void solveLivelock(SimulationTask task) {
        System.out.println("Livelock solution simulation started...");

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            int attempts = 0;
            while (true) {
                if (!agent2.isActing()) {
                    agent1.act();
                    System.out.println("Thread 1: Adjusting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 1 interrupted.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (++attempts > 5) {
                    System.out.println("Thread 1: Breaking livelock.");
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
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 2 interrupted.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (++attempts > 5) {
                    System.out.println("Thread 2: Breaking livelock.");
                    break;
                }
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
