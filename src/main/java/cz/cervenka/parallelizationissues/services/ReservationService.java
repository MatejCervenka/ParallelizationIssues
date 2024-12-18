package cz.cervenka.parallelizationissues.services;

import cz.cervenka.parallelizationissues.database.entities.Reservation;
import cz.cervenka.parallelizationissues.database.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository repository;

    private final List<String> logs = new ArrayList<>();


    /*private final Object lock1 = new Object();
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
    }*/

    /*public void simulateDeadlock() {
        logs.clear();
        logs.add("Deadlock simulation started...");

        Reservation slot1 = new Reservation();
        slot1.setSlot("Slot A");
        Reservation slot2 = new Reservation();
        slot2.setSlot("Slot B");

        Thread user1 = new Thread(() -> {
            synchronized (slot1) {
                logs.add("User 1: Locked Slot A.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logs.add("User 1 interrupted.");
                }
                logs.add("User 1: Waiting to lock Slot B...");
                synchronized (slot2) {
                    logs.add("User 1: Booked Slot A and Slot B.");
                }
            }
        });

        Thread user2 = new Thread(() -> {
            synchronized (slot2) {
                logs.add("User 2: Locked Slot B.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logs.add("User 2 interrupted.");
                }
                logs.add("User 2: Waiting to lock Slot A...");
                synchronized (slot1) {
                    logs.add("User 2: Booked Slot B and Slot A.");
                }
            }
        });

        user1.start();
        user2.start();

        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            logs.add("Simulation interrupted.");
        }

        logs.add("Deadlock detected! Both users are waiting indefinitely.");
    }


    public void simulateStarvation() {
        logs.clear();
        logs.add("Starvation simulation started...");

        Object reservationLock = new Object();

        Runnable highPriorityUser = () -> {
            while (true) {
                synchronized (reservationLock) {
                    logs.add("High-priority user: Reserved a slot.");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        logs.add("High-priority user interrupted.");
                    }
                }
            }
        };

        Runnable lowPriorityUser = () -> {
            while (true) {
                if (!Thread.holdsLock(reservationLock)) {
                    logs.add("Low-priority user: Waiting for a chance to book...");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logs.add("Low-priority user interrupted.");
                }
            }
        };

        Thread highPriorityThread = new Thread(highPriorityUser);
        Thread lowPriorityThread = new Thread(lowPriorityUser);

        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);

        highPriorityThread.start();
        lowPriorityThread.start();

        try {
            highPriorityThread.join(1000);
            lowPriorityThread.join(1000);
        } catch (InterruptedException e) {
            logs.add("Simulation interrupted.");
        }

        logs.add("Starvation simulation complete.");
    }


    public void simulateLivelock() {
        logs.clear();
        logs.add("Livelock simulation started...");

        class ReservationAttempt {
            private boolean available;

            synchronized void tryBooking() {
                this.available = !this.available;
            }

            synchronized boolean isAvailable() {
                return this.available;
            }
        }

        ReservationAttempt slot1 = new ReservationAttempt();
        slot1.tryBooking(); // Initially available

        Thread user1 = new Thread(() -> {
            while (true) {
                if (slot1.isAvailable()) {
                    slot1.tryBooking();
                    logs.add("User 1: Tried to book, but deferred.");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logs.add("User 1 interrupted.");
                    }
                }
            }
        });

        Thread user2 = new Thread(() -> {
            while (true) {
                if (!slot1.isAvailable()) {
                    slot1.tryBooking();
                    logs.add("User 2: Tried to book, but deferred.");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logs.add("User 2 interrupted.");
                    }
                }
            }
        });

        user1.start();
        user2.start();

        try {
            user1.join(1000);
            user2.join(1000);
        } catch (InterruptedException e) {
            logs.add("Simulation interrupted.");
        }

        logs.add("Livelock simulation complete.");
    }*/


    public void simulateDeadlock() {
        System.out.println("Deadlock simulation started...");

        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked Resource A.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread 1 interrupted.");
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
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread 2 interrupted.");
                }
                System.out.println("Thread 2: Waiting to lock Resource A...");
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked Resource A.");
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }

        System.out.println("Deadlock detected! Both threads are waiting indefinitely.");
    }

    public void simulateStarvation() {
        System.out.println("Starvation simulation started...");

        Object reservationLock = new Object();

        Runnable highPriorityTask = () -> {
            while (true) {
                synchronized (reservationLock) {
                    System.out.println("High-priority user: Reserved a slot.");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.out.println("High-priority user interrupted.");
                    }
                }
            }
        };

        Runnable lowPriorityTask = () -> {
            while (true) {
                System.out.println("Low-priority user: Waiting for a chance to book...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Low-priority user interrupted.");
                }
            }
        };

        Thread highPriorityThread = new Thread(highPriorityTask);
        Thread lowPriorityThread = new Thread(lowPriorityTask);

        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);

        highPriorityThread.start();
        lowPriorityThread.start();

        try {
            highPriorityThread.join(1000);
            lowPriorityThread.join(1000);
        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }

        System.out.println("Starvation simulation complete.");
    }


    public void simulateLivelock() {
        System.out.println("Livelock simulation started...");

        class Agent {
            private boolean action;

            synchronized void act() {
                this.action = !this.action;
            }

            synchronized boolean isActing() {
                return this.action;
            }
        }

        Agent agent1 = new Agent();
        Agent agent2 = new Agent();

        Thread thread1 = new Thread(() -> {
            while (true) {
                if (!agent2.isActing()) {
                    agent1.act();
                    System.out.println("Thread 1: Adjusting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 1 interrupted.");
                    }
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (true) {
                if (!agent1.isActing()) {
                    agent2.act();
                    System.out.println("Thread 2: Adjusting...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Thread 2 interrupted.");
                    }
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join(1000);
            thread2.join(1000);
        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }

        System.out.println("Livelock simulation complete.");
    }




    public String getLogs() {
        return String.join("\n", logs);
    }
}