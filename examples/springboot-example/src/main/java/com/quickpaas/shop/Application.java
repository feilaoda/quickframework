package com.quickpaas.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.quickpaas.shop"})
public class Application {

    public static void main(String[] args) {
        System.out.println("QuickShop");
        SpringApplication.run(Application.class, args);
    }

}
