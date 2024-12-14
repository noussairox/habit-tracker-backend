package com.habitracker.habitApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.habittacker.habitApp.HabitAppApplication;

@SpringBootTest(classes = HabitAppApplication.class) // Spécifiez explicitement la classe de configuration principale
public class HabitAppApplicationTests {

    @Test
    void contextLoads() {
        // Vérifie si le contexte Spring Boot se charge correctement
    }
}
