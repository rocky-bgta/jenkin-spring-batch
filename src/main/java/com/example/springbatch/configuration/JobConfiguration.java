package com.example.springbatch.configuration;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final String remoteUrl;
    private static final String jobName;
    private static final String jobToken;

    @Value( "${userId}" )
    private String userId;

    @Value("${jenkinsToken}")
    private String jenkinsToken ;

    static {
        remoteUrl = "http://localhost:8080";
        jobName = "spring-batch";
        jobToken = "test-batch";
       //"http://172.30.81.70:8080/job/DOS-1907/buildWithParameters?parameter1=tuli&parameter2=rocky&token=dos-1907
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
        Map<String,String> jobParameter = new HashMap<>();
        jobParameter.put("branch","DOS");
        return stepBuilderFactory.get("step3")
                .tasklet((stepContribution, chunkContext) -> {
                    triggerJenkinsJob(buildUrl(remoteUrl,jobName,jobToken,jobParameter));
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

    private URL buildUrl(String host, String jobName, String jobToken, Map<String,String> parameters)
            throws URISyntaxException, MalformedURLException {
        String buildUrl;
        StringBuilder builder = new StringBuilder();
        buildUrl = builder.append(host)
                .append("/job")
                .append("/")
                .append(jobName)
                .append("/")
                .append("buildWithParameters").toString();

        URIBuilder uriBuilder = new URIBuilder(buildUrl);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            uriBuilder.addParameter(entry.getKey(),entry.getValue());
        }
        uriBuilder.addParameter("token",jobToken);
        URL url = uriBuilder.build().toURL();
        return url;
    }


    private void triggerJenkinsJob(URL url) throws IOException {
        String authStr = userId + ":" + jenkinsToken;
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
