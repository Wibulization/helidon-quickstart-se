package io.helidon.examples.quickstart.se;

import io.helidon.common.context.ExecutorException;

import io.helidon.webclient.api.HttpClientResponse;
import io.helidon.webclient.api.WebClient;

import jakarta.json.JsonObject;

public class Client {
        public static void main(String[] args) throws ExecutorException, InterruptedException {
                // Create a WebClient instance
                WebClient webClient = WebClient.builder()
                                .build();

                // Use WebClient to send an HTTP POST request to the route defined in the
                // WebServer
                HttpClientResponse res = webClient.post()
                                .uri("http://localhost:8080/user/postUser") // Adjust the URI as needed
                                .submit(JsonObject.EMPTY_JSON_OBJECT);

                System.out.println("Server respond " + res.status());
                String content = res.as(String.class);
                System.out.println("Content : " + content);

                webClient.get()
                                .uri("http://localhost:8080/user/getUser").request(String.class);
        }
}
