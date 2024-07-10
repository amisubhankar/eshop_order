package com.eshop.order.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

public class OrderUtil {

    public static Long getUserIdFromToken(String header) {
        //Splitting up JWT token
        String[] chunks = header.substring(7).split("\\.");

        //Decode 2nd chunk i.e. Payload portion
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        //Deserialize the payload and getting the key "sub"
        String email;
        try {
            email = new ObjectMapper().readTree(payload).get("sub").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", header);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long> response = restTemplate.exchange(
                "http://localhost:8081/user/"+email,
                HttpMethod.GET, requestEntity, Long.class);

        return response.getBody();

    }
}
