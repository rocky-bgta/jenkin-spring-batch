package com.example.springbatch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private static final String remoteUrl;

    static {
        //remoteUrl = "http://localhost:8080/job/spring-batch/build?token=";
        remoteUrl = "http://localhost:8080/job/spring-batch/buildWithParameters?branch=tuli&token";
    }

    public JobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Step One Execute successfully");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Step Two Executed successfully");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((stepContribution, chunkContext) -> {
                    triggerJenkinsJob("test-batch");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }


    private static void triggerJenkinsJob(String token) throws IOException {
        URL url = new URL(remoteUrl+token); // Jenkins URL localhost:8080, job named 'test'
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
