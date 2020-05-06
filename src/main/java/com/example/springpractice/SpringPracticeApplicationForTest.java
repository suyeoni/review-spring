package com.example.springpractice;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class SpringPracticeApplicationForTest {

    public static void main(String[] args) {
        // public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable
        // try-with-resources -> no need to finally block for close
        /*
        * ConfigurableApplicationContext c = SpringApplication.run(SpringPracticeApplicationForTest.class, args);
        try { } finally {
            c.close();
        }
        * */
        try (ConfigurableApplicationContext c = SpringApplication.run(SpringPracticeApplicationForTest.class, args)) {

        } catch (Exception e) {

        }
    }

    // bean으로 command line runner 선언
    @Bean
    public CommandLineRunner commandLineRunner() {
        // @FunctionalInterface public interface CommandLineRunner
        return args -> {
            System.out.println("run()...");
        };

        /*
        * return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

            }
        }
        * */
    }

    @Bean
    public ApplicationRunner applicationRunner() {

        return new ApplicationRunner() {
            // ApplicationArguments를 그대로 가져 올 수 있다!! 물론 람다로도 가능
            /*return (args) -> {
                System.out.println(args);
            };*/
            @Override
            public void run(ApplicationArguments args) throws Exception {
                System.out.println(args);
            }
        };
    }

    @RestController
    static class MyController {

        @RequestMapping("/hello")
        public String hello() {
            return "hello";
        }

        @RequestMapping("/hello-mono")
        public Mono<String> helloMono() {
            return Mono.just("hello");
        }

        @RequestMapping("/hello-mono-uppercase-async")
        public Mono<String> helloMonoAsync() {
            return Mono.just("hello")
                       .map(String::toUpperCase)
                       .publishOn(Schedulers.newSingle("mono-thread"))
                       .log();
        }

        @RequestMapping("/hello-completable-future")
        public CompletableFuture<String> helloCompletableFuture() {
            return CompletableFuture.supplyAsync(() -> "hello")
                                    .thenApply(String::toUpperCase);

        }
    }
}
