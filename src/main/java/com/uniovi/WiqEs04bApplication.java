package com.uniovi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class WiqEs04bApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiqEs04bApplication.class, args);
    }

}
