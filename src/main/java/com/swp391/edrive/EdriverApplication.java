package com.swp391.edrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("com.swp391.edrive.entity")
@ComponentScan("com.swp391.edrive")
public class EdriverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdriverApplication.class, args);
	}

}
