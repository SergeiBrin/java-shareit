package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}
}

	/*
		1. При for each листа запроса есть обращение к вложенной объекту, то для этого объекта
		нужен вложенный join fetch для этого объекта
		2. Transactional в методы
 	*/
