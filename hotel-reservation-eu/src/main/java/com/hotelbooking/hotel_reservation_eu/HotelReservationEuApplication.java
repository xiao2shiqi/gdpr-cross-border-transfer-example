package com.hotelbooking.hotel_reservation_eu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hotelbooking.hotel_reservation_eu.mapper")
@EnableScheduling
public class HotelReservationEuApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelReservationEuApplication.class, args);
	}

}
