package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/simulate")
public class SimulationController {

    @Autowired
    private ReservationService service;
    
    private final ConcurrentHashMap<String, Thread> currentThreads = new ConcurrentHashMap<>();


    /*@GetMapping("/deadlock-page")
    public String simulateDeadlockPage(Model model) {
        service.simulateDeadlock();
        model.addAttribute("simulation", "Deadlock simulation is running. Please observe the behavior...");
        return "simulation";
    }

    @GetMapping("/starvation-page")
    public String simulateStarvationPage(Model model) {
        service.simulateStarvation();
        model.addAttribute("simulation", "Starvation simulation is running. Please observe the behavior...");
        return "simulation";
    }

    @GetMapping("/livelock-page")
    public String simulateLivelockPage(Model model) {
        service.simulateLivelock();
        model.addAttribute("simulation", "Livelock simulation is running. Please observe the behavior...");
        return "simulation";
    }*/

    @GetMapping("/deadlock-page")
    public String simulateDeadlockPage(Model model) {
        Thread simulationThread = new Thread(() -> service.simulateDeadlock());
        currentThreads.put("current", simulationThread);
        simulationThread.start();

        model.addAttribute("simulation", "Deadlock simulation is running. Please observe the behavior...");
        return "simulation";
    }

    @GetMapping("/starvation-page")
    public String simulateStarvationPage(Model model) {
        Thread simulationThread = new Thread(() -> service.simulateStarvation());
        currentThreads.put("current", simulationThread);
        simulationThread.start();

        model.addAttribute("simulation", "Starvation simulation is running. Please observe the behavior...");
        return "simulation";
    }

    @GetMapping("/livelock-page")
    public String simulateLivelockPage(Model model) {
        Thread simulationThread = new Thread(() -> service.simulateLivelock());
        currentThreads.put("current", simulationThread);
        simulationThread.start();

        model.addAttribute("simulation", "Livelock simulation is running. Please observe the behavior...");
        return "simulation";
    }

    @GetMapping("/simulate/stop")
    public String stopSimulation() {
        Thread simulationThread = currentThreads.get("current");
        if (simulationThread != null) {
            simulationThread.interrupt();
            currentThreads.remove("current");
        }
        return "home";
    }

    @GetMapping("/logs")
    public String getLogs() {
        return service.getLogs();
    }
}