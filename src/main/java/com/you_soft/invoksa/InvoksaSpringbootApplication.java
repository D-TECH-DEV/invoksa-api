package com.you_soft.invoksa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class InvoksaSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoksaSpringbootApplication.class, args);
	}

}
