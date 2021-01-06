package com.kb.leggid;

import com.kb.leggid.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
// Active Swagger et Springfox dans l'application
@Import(SwaggerConfiguration.class)
public class LeggidApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeggidApplication.class, args);
	}

}
