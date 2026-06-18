package com.consorcio.projeto_consorcio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication()
@EnableScheduling
public class ProjetoConsorcioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoConsorcioApplication.class, args);
	}

}
