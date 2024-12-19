package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.util.SimulationTask;
import cz.cervenka.parallelizationissues.services.SimulationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/simulate-problems")
public class SimulationController {

    private final ConcurrentHashMap<String, SimulationTask> currentTasks = new ConcurrentHashMap<>();
    private final SimulationService service;

    public SimulationController(SimulationService service) {
        this.service = service;
    }

    @GetMapping("/deadlock-page")
    public String simulateDeadlockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.simulateDeadlock(task);

        model.addAttribute("simulation", "Deadlock simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    @GetMapping("/starvation-page")
    public String simulateStarvationPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.simulateStarvation(task);

        model.addAttribute("simulation", "Starvation simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    @GetMapping("/livelock-page")
    public String simulateLivelockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.simulateLivelock(task);

        model.addAttribute("simulation", "Livelock simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    @GetMapping("/stop")
    public void stopSimulation(HttpServletResponse response) throws IOException {
        response.sendRedirect("/problems");
        SimulationTask task = currentTasks.get("current");
        if (task != null) {
            task.interruptAll();
            currentTasks.remove("current");
        }
    }
}