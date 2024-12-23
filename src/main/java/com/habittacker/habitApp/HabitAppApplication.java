package com.habittacker.habitapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HabitAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HabitAppApplication.class, args);
	}
	
}
