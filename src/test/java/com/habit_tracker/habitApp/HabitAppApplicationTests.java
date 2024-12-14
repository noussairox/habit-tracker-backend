package com.habit_tracker.habitApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Active le fichier application-test.properties
public class HabitAppApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Create a TestBranch");
    }
}
