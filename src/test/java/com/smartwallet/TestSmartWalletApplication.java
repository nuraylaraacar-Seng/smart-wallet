package com.smartwallet;

import org.springframework.boot.SpringApplication;

public class TestSmartWalletApplication {

    public static void main(String[] args) {

        SpringApplication.from(SmartWalletApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
