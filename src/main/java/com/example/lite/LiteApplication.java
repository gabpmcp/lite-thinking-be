package com.example.lite;

import com.example.lite.service.JwtService;
import com.example.lite.util.Utils;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class LiteApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();  // Cargar las variables del archivo .env
		var result = dotenv.entries().stream().map(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
			return entry;
		}).collect(Collectors.toList());
		System.out.println(result);
		SpringApplication.run(LiteApplication.class, args);
	}

}
