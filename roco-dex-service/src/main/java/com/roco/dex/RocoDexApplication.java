package com.roco.dex;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.roco.dex.mapper")
@EnableCaching
public class RocoDexApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocoDexApplication.class, args);
    }
}
