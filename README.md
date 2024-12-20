# Parallelization Issues Simulation

This project demonstrates solutions to common parallelization issues such as **deadlock**, **starvation**, and **livelock**. It includes simulations of these problems and their solutions, displayed in real-time via WebSocket connections.

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Parallelization Issues](#parallelization-issues)
   - [Deadlock](#deadlock)
   - [Starvation](#starvation)
   - [Livelock](#livelock)
5. [How to Run the Project](#how-to-run-the-project)
6. [Documentation](#documentation)
7. [Future Improvements](#future-improvements)

---

## Introduction

Parallelization issues arise when multiple processes or threads operate concurrently but face obstacles such as contention for shared resources. This project simulates these problems and provides insight into their solutions through visual and interactive demonstrations.

---

## Features

- **Real-time WebSocket communication** to broadcast simulation status and events.
- Simulations of three major parallelization issues:
  - Deadlock
  - Starvation
  - Livelock
- Example implementations of solutions for these issues.
- Detailed logging for better understanding of the underlying processes.

---

## Technologies Used

- **Java Spring Boot**: Backend logic for simulations and WebSocket integration.
- **JavaScript**: Frontend for rendering WebSocket events.
- **WebSockets**: Real-time communication between server and client.
- **Java Concurrency Utilities**: Handling locks and threads for simulations.

---

## Parallelization Issues

### Deadlock
**Problem**: A deadlock occurs when a group of processes becomes stuck because each process is waiting for a resource that another process in the group is holding. No process can proceed.

**Solution**: Deadlock can be prevented using techniques like resource ordering, deadlock detection and recovery, or ensuring resources are acquired in a specific sequence.

**Implementation in this Application**: 
- The solution enforces consistent resource locking order (Resource A, then Resource B). 
- Two threads simulate the problem by attempting to acquire locks on these resources. Deadlock is showcased and resolved using this ordering.

---

### Starvation
**Problem**: Starvation occurs when a process is perpetually denied access to necessary resources because higher-priority processes continuously preempt it.

**Solution**: Starvation can be mitigated by using fair scheduling algorithms, such as round-robin, or by setting maximum wait times for resources.

**Implementation in this Application**:
- A `ReentrantLock` with fairness enabled ensures threads acquire locks in a fair sequence.
- Threads simulate high- and low-priority processes trying to reserve shared resources, demonstrating the resolution of starvation.

---

### Livelock
**Problem**: A livelock occurs when processes continuously change their state in response to each other without making any progress.

**Solution**: Livelock can be avoided by implementing mechanisms like retry limits or prioritization rules to break the cycle.

**Implementation in this Application**:
- The solution uses agents with retry limits to resolve livelock.
- Threads simulate agents continuously adjusting in response to each other. The livelock is resolved after a set number of retries.

---

## How to Run the Project

1. **Clone the Repository**
2. **If needed download proper JDK**

## Sources of informations
- Spring WebSocket Documentation
- Wikipedia on Deadlock
- Wikipedia on Starvation
- Wikipedia on Livelock
- Oracle Java Threads Documentation
- Java Thread Synchronization
- Baeldung
- Stack Overflow
