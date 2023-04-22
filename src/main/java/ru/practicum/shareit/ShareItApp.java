package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {
	// userId перенести в service, чтоб было идентично с item?

	// поработать с Builder() и убрать из Item userId.

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

}
