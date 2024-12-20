package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.config.SimulationWebSocketHandler;
import cz.cervenka.parallelizationissues.util.SimulationTask;
import cz.cervenka.parallelizationissues.services.SimulationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller class for handling simulation-related HTTP requests.
 * This class manages different types of concurrency problem simulations.
 */
@Controller
@RequestMapping("/simulate-problems")
public class SimulationController {

    private final ConcurrentHashMap<String, SimulationTask> currentTasks = new ConcurrentHashMap<>();
    private final SimulationService service;
    private final SimulationWebSocketHandler webSocketHandler;

    /**
     * Constructs a new SimulationController with the given SimulationService.
     *
     * @param service The SimulationService to be used for running simulations.
     */
    public SimulationController(SimulationService service, SimulationWebSocketHandler webSocketHandler) {
        this.service = service;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Handles GET requests for the deadlock simulation page.
     * Initiates a deadlock simulation and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/deadlock-page")
    public String simulateDeadlockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.runSimulateDeadlock(task);

        model.addAttribute("simulation", "Deadlock simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    /**
     * Handles GET requests for the starvation simulation page.
     * Initiates a starvation simulation and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/starvation-page")
    public String simulateStarvationPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.runSimulateStarvation(task);

        model.addAttribute("simulation", "Starvation simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    /**
     * Handles GET requests for the livelock simulation page.
     * Initiates a livelock simulation and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/livelock-page")
    public String simulateLivelockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.runSimulateLivelock(task);

        model.addAttribute("simulation", "Livelock simulation is running. Please observe the behavior...");
        return "problem_simulation";
    }

    /**
     * Handles GET requests to stop the current simulation.
     * Interrupts the current simulation task and redirects to the problems page.
     *
     * @param response The HttpServletResponse object used for redirection.
     * @throws IOException If an input or output exception occurs during redirection.
     */
    @GetMapping("/stop")
    public void stopSimulation(HttpServletResponse response) throws IOException {
        response.sendRedirect("/problems");
        SimulationTask task = currentTasks.get("current");
        if (task != null) {
            task.interruptAll();
            currentTasks.remove("current");
            webSocketHandler.broadcast("/ws/problems/starvation", "Simulation stopped.");
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