package com.example.springbatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@SpringBootApplication
public class SpringBatchApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(SpringBatchApplication.class, args);
//    }

    public static void main(String[] args) throws IOException {
        TriggerJenkinsJob();


    }

    private static void TriggerJenkinsJob() throws IOException {
        URL url = new URL("http://localhost:8080/job/spring-batch/build?token=11d062d7023e11765b4d8ee867b67904f9"); // Jenkins URL localhost:8080, job named 'test'
        String user = "salahin";
        String pass = "11d062d7023e11765b4d8ee867b67904f9"; // password or API token
        String authStr = user + ":" + pass;
        String basicAuth = "Basic "+ Base64.getEncoder().encodeToString(authStr.getBytes("utf-8"));


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty ("Authorization", basicAuth);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        System.out.println("Step Tiger Jenkins Jobs: " + connection.getResponseCode());
        connection.disconnect();
    }
}