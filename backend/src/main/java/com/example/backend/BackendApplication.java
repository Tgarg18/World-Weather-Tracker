package com.example.backend;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
		String envFilePath;

		if (workingDirectory.endsWith("backend"))
			envFilePath = workingDirectory + "/.env";
		else
			envFilePath = workingDirectory + "/backend/.env";

		Dotenv dotenv = Dotenv.configure()
				.directory(envFilePath)
				.load();

		System.setProperty("FRONTEND_BASE_URL", dotenv.get("FRONTEND_BASE_URL"));
		System.setProperty("DB", dotenv.get("DB"));
		System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
		System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));
		SpringApplication.run(BackendApplication.class, args);
	}

}
