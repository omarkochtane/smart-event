package tn.iset.smartevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Point d'entrée du backend SMART EVENT.
// Démarre le serveur Spring Boot sur le port défini dans application.properties (8081).
@SpringBootApplication
public class SmartEventApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartEventApplication.class, args);
    }
}
