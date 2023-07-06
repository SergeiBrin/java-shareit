package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItGateway {

	public static void main(String[] args) {
		SpringApplication.run(ShareItGateway.class, args);
	}

	/*
		1. Переделать тесты под контроллер.
		2. Подумать, нужен ли UserBookingDto
		3. Сделать валидацию значений. В Server убрать ту валидацию, которая будет повторяться в Gateway.
		4. Убрать лишние аннотации.
		5. Убрать лишние Handler
		6. Закоментил в Booking server switch, if для PageRequest, и 3 теста, которые проверяют это.
		7. Убрать валидацию на стороне server - в контроллере и dto.
	 */
}

