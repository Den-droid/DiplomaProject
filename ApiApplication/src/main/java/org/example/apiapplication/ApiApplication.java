package org.example.apiapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);

//        try {
//            testScholarExtraction();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static void testScholarExtraction() throws IOException {
    }
}
