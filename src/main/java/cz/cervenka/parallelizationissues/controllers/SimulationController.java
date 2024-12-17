package cz.cervenka.parallelizationissues.controllers;

import cz.cervenka.parallelizationissues.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulate")
public class SimulationController {
    @Autowired
    private ReservationService service;

    @GetMapping("/deadlock")
    public String simulateDeadlock() {
        service.simulateDeadlock();
        return "Deadlock simulation started!";
    }

    @GetMapping("/starvation")
    public String simulateStarvation() {
        service.simulateStarvation();
        return "Starvation simulation started!";
    }

    @GetMapping("/livelock")
    public String simulateLivelock() {
        service.simulateLivelock();
        return "Livelock simulation started!";
    }
}