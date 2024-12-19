package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.objects.SimulationTask;
import cz.cervenka.parallelizationissues.services.SolutionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/simulate-solutions")
public class SolutionsController {

    private final ConcurrentHashMap<String, SimulationTask> currentTasks = new ConcurrentHashMap<>();
    private final SolutionService service;

    public SolutionsController(SolutionService service) {
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
        response.sendRedirect("/solutions");
        SimulationTask task = currentTasks.get("current");
        if (task != null) {
            task.interruptAll();
            currentTasks.remove("current");
        }
    }
}