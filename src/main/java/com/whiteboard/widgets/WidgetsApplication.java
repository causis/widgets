package com.whiteboard.widgets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.whiteboard.widgets")
public class WidgetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WidgetsApplication.class, args);
	}

}
