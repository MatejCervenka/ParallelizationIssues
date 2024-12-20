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

/**
 * Controller class for handling solution-related HTTP requests.
 * This class manages different types of concurrency problem solutions.
 */
@Controller
@RequestMapping("/simulate-solutions")
public class SolutionsController {

    private final ConcurrentHashMap<String, SimulationTask> currentTasks = new ConcurrentHashMap<>();
    private final SolutionService service;
    private final SimulationWebSocketHandler webSocketHandler;

    /**
     * Constructs a new SolutionsController with the given SolutionService.
     *
     * @param service The SolutionService to be used for running solutions.
     * @param webSocketHandler The SimulationWebSocketHandler for managing WebSocket interactions.
     */
    public SolutionsController(SolutionService service, SimulationWebSocketHandler webSocketHandler) {
        this.service = service;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Handles GET requests for the deadlock solution page.
     * Initiates a solution for the deadlock problem and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/deadlock-page")
    public String solveDeadlockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveDeadlock(task);

        model.addAttribute("simulation", "Deadlock solution is running. Please observe the behavior...");
        return "solution_simulation";
    }

    /**
     * Handles GET requests for the starvation solution page.
     * Initiates a solution for the starvation problem and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/starvation-page")
    public String solveStarvationPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveStarvation(task);

        model.addAttribute("simulation", "Starvation solution is running. Please observe the behavior...");
        return "solution_simulation";
    }

    /**
     * Handles GET requests for the livelock solution page.
     * Initiates a solution for the livelock problem and prepares the model for view rendering.
     *
     * @param model The Model object to be populated with attributes for the view.
     * @return A String representing the name of the view to be rendered.
     */
    @GetMapping("/livelock-page")
    public String solveLivelockPage(Model model) {
        SimulationTask task = new SimulationTask();
        currentTasks.put("current", task);

        service.solveLivelock(task);

        model.addAttribute("simulation", "Livelock solution is running. Please observe the behavior...");
        return "solution_simulation";
    }

    /**
     * Handles GET requests to stop the current solution.
     * Interrupts the current solution task and redirects to the solutions page.
     *
     * @param response The HttpServletResponse object used for redirection.
     * @throws IOException If an input or output exception occurs during redirection.
     */
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

    /**
     * Closes all active WebSocket sessions gracefully.
     * Iterates through the session map and closes each session if open.
     */
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
