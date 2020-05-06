package com.example.springpractice;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.springpractice.spel.ExpressionService;
import com.example.springpractice.spel.MyLog;

@SpringBootApplication
public class SpelTestApplication {

    public static void main(String[] args) throws InterruptedException {
        try (ConfigurableApplicationContext c = SpringApplication.run(SpelTestApplication.class, args)) { }
//        Thread.sleep(Long.MAX_VALUE);
    }

    @Autowired
    ExpressionService service;

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                MyLog log = new MyLog(System.currentTimeMillis(), "abcde", "ABC",
                                      "user123", new HashMap<String, Object>() {
                    {
                        put("customKey", 30);
                    }
                });

                service.eval(log, "update(#field3, 'mytestkey', 12345)");
                service.eval(log, "#field2=='ABC'? incr(#field3, #field1 + '-hi', #customKey)"
                                  + " : false");
            }
        };
    }

}
