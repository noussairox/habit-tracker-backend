package com.habitracker.habitapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.habittacker.habitapp.HabitAppApplication;

@SpringBootTest(classes = HabitAppApplication.class) // Spécifiez explicitement la classe de configuration principale
@ActiveProfiles("test") // Utilise application-test.properties
public class HabitAppApplicationTests {

    @Test
    void contextLoads() {
        // Vérifie si le contexte Spring Boot se charge correctement
    }
}
