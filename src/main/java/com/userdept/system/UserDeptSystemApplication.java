package com.userdept.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.userdept.system.mapper")
public class UserDeptSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDeptSystemApplication.class, args);
    }
}
