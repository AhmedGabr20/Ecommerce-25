package com.gabr.ecommerce;

import org.camunda.bpm.client.spring.annotation.EnableExternalTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableExternalTaskClient(
        workerId = "${camunda.bpm.client.worker-id:ecommerce-worker}",
        baseUrl = "${camunda.bpm.client.base-url:http://localhost:8080/engine-rest}"
)
@EnableScheduling
//@EnableCaching
public class EcommerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApiApplication.class, args);
	}

}
