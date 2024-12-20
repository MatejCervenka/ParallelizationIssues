package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import cz.cervenka.parallelizationissues.services.SolutionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/simulate-solutions")
public class SolutionsController {

    private final ConcurrentHashMap<String, SimulationTask> currentTasks = new ConcurrentHashMap<>();
    private final SolutionService service;
    private final SimulationWebSocketHandler webSocketHandler;

    public SolutionsController(SolutionService service, SimulationWebSocketHandler webSocketHandler) {
        this.service = service;
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/deadlock-page")
    public String solveDeadlockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveDeadlock(task);

        model.addAttribute("simulation", "Deadlock simulation is running. Please observe the behavior...");
        return "solution_simulation";
    }

    @GetMapping("/starvation-page")
    public String solveStarvationPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveStarvation(task);

        model.addAttribute("simulation", "Starvation simulation is running. Please observe the behavior...");
        return "solution_simulation";
    }

    @GetMapping("/livelock-page")
    public String solveLivelockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveLivelock(task);

        model.addAttribute("simulation", "Livelock simulation is running. Please observe the behavior...");
        return "solution_simulation";
    }

    @GetMapping("/stop")
    public void stopSimulation(HttpServletResponse response) throws IOException {
        response.sendRedirect("/solutions");
        SimulationTask task = currentTasks.get("current");
        if (task != null) {
            task.interruptAll();
            currentTasks.remove("current");
            webSocketHandler.broadcast("/ws/solutions/starvation", "Simulation stopped.");
            closeAllWebSocketSessions();
        }
    }

    private void closeAllWebSocketSessions() {
        webSocketHandler.getSessionMap().forEach((session, status) -> {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.NORMAL);
                }
            } catch (IOException e) {
                System.err.println("Failed to close WebSocket session: " + e.getMessage());
            }
        });
    }
}