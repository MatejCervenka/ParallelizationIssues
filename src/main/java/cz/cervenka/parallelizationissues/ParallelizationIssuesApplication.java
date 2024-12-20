package cz.cervenka.parallelizationissues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Spring Boot application.
 * This class is responsible for launching the Spring Boot application and initializing the entire application context.
 * The class is annotated with @SpringBootApplication, indicating that it is the main class for a Spring Boot application.
 */
@SpringBootApplication
public class ParallelizationIssuesApplication {

    /**
     * The main method that serves as the entry point for the Spring Boot application.
     * It invokes the SpringApplication.run() method to launch the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ParallelizationIssuesApplication.class, args);
    }
}
