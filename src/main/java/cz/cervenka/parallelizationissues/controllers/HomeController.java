package cz.cervenka.parallelizationissues.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/problems")
    public String showProblems() {
        return "problems";
    }

    @GetMapping("/solutions")
    public String showSolutions() {
        return "solutions";
    }
}