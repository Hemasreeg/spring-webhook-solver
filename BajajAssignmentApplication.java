package com.bajaj.assignment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class BajajAssignmentApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajAssignmentApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> generateRequest = Map.of(
                    "name", "Gogudupalem Hemasree",
                    "regNo", "22BCE9996",
                    "email", "gogudupalemhemasree86@gmail.com"
            );

            ResponseEntity<Map> generateResponse =
                    restTemplate.postForEntity(generateUrl, generateRequest, Map.class);

            Map<String, Object> body = generateResponse.getBody();
            if (body == null) return;

            String webhookUrl = (String) body.get("webhookUrl");
            String accessToken = (String) body.get("accessToken");
            if (webhookUrl == null || accessToken == null) return;

            String finalQuery =
                    "SELECT d.DEPARTMENT_NAME, COUNT(e.EMP_ID) AS TOTAL_EMPLOYEES " +
                    "FROM EMPLOYEE e " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "GROUP BY d.DEPARTMENT_NAME " +
                    "ORDER BY TOTAL_EMPLOYEES DESC " +
                    "LIMIT 1;";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> sqlBody = Map.of("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> requestEntity =
                    new HttpEntity<>(sqlBody, headers);

            restTemplate.postForEntity(webhookUrl, requestEntity, String.class);

        } catch (Exception ignored) {
        }
    }
}
