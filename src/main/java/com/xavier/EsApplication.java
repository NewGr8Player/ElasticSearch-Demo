package com.xavier;

import com.xavier.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于canal实时传输数据到es中
 *
 * @author NewGr8Player
 */
@EnableCanalClient
@SpringBootApplication
public class EsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}
}
