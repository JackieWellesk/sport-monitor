package com.hegel;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.hegel.dao"})
public class SportMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportMonitorApplication.class, args);
    }

}

