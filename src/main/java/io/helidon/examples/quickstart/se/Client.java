package io.helidon.examples.quickstart.se;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import io.helidon.common.context.ExecutorException;
import io.helidon.common.media.type.MediaType;
import io.helidon.examples.User;
import io.helidon.http.HttpException;
import io.helidon.http.HttpMediaType;
import io.helidon.http.media.jsonp.JsonpSupport;
import io.helidon.webclient.api.HttpClientResponse;
import io.helidon.webclient.api.WebClient;
import io.helidon.webclient.api.WebClientServiceRequest;
import io.helidon.webclient.api.WebClientServiceResponse;
import io.helidon.webserver.http.ServerRequest;
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
