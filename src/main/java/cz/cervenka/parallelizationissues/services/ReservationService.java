package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.database.entities.Reservation;
import cz.cervenka.parallelizationissues.database.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository repository;

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    // Deadlock simulace
    public void simulateDeadlock() {
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lock2) {
                    Reservation reservation = new Reservation();
                    reservation.setUsername("User1");
                    reservation.setSlot("Slot1");
                    reservation.setStatus("Booked");
                    repository.save(reservation);
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lock1) {
                    Reservation reservation = new Reservation();
                    reservation.setUsername("User2");
                    reservation.setSlot("Slot2");
                    reservation.setStatus("Booked");
                    repository.save(reservation);
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    // Starvation simulace
    public void simulateStarvation() {
        ReentrantLock lock = new ReentrantLock(true); // Fair lock

        for (int i = 0; i < 5; i++) {
            int threadId = i;
            new Thread(() -> {
                lock.lock();
                try {
                    Reservation reservation = new Reservation();
                    reservation.setUsername("User" + threadId);
                    reservation.setSlot("Slot" + threadId);
                    reservation.setStatus("Processing");
                    repository.save(reservation);
                } finally {
                    lock.unlock();
                }
            }).start();
        }
    }

    // Livelock simulace
    public void simulateLivelock() {
        AtomicBoolean flag1 = new AtomicBoolean(false);
        AtomicBoolean flag2 = new AtomicBoolean(false);

        new Thread(() -> {
            while (!flag1.get()) {
                System.out.println("Thread1: Waiting...");
                flag2.set(true);
            }
            System.out.println("Thread1: Done!");
        }).start();

        new Thread(() -> {
            while (!flag2.get()) {
                System.out.println("Thread2: Waiting...");
                flag1.set(true);
            }
            System.out.println("Thread2: Done!");
        }).start();
    }
}
