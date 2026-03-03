package br.com.srcsoftware.todoscontratodos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodosContraTodosSportApplication {

	public static void main(String[] args) {
		// Define o modo headless programaticamente
        System.setProperty("java.awt.headless", "true");
		SpringApplication.run(TodosContraTodosSportApplication.class, args);
	}

}
