/*
 * MIT License
 *
 * Copyright (c) 2017 Barracks Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.barracks.subscriptionservice.client;

import io.barracks.commons.util.Endpoint;
import io.barracks.subscriptionservice.client.exception.AuthorizationServiceClientException;
import io.barracks.subscriptionservice.rest.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthorizationServiceClient {
    static final Endpoint GET_USER_BY_ID_ENDPOINT = Endpoint.from(HttpMethod.GET, "/users/{userId}");
    static final Endpoint GET_USER_BY_TOKEN_ENDPOINT = Endpoint.from(HttpMethod.GET, "/me");
    static final Endpoint SET_USER_STATUS_ENDPOINT = Endpoint.from(HttpMethod.PUT, "/users/{userId}/status/{status}");

    private final String serviceBaseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthorizationServiceClient(@Value("${io.barracks.authorizationservice.base_url}") String serviceBaseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.serviceBaseUrl = serviceBaseUrl;
    }

    public User getUserById(String userId) {
        try {
            return restTemplate.exchange(
                    GET_USER_BY_ID_ENDPOINT.withBase(serviceBaseUrl).getRequestEntity(userId),
                    User.class
            ).getBody();
        } catch (HttpStatusCodeException e) {
            throw new AuthorizationServiceClientException(e);
        }
    }

    public void setStatus(String userId, String status) {
        try {
            restTemplate.exchange(
                    SET_USER_STATUS_ENDPOINT.withBase(serviceBaseUrl).getRequestEntity(userId, status),
                    Void.class
            );
        } catch (HttpStatusCodeException e) {
            throw new AuthorizationServiceClientException(e);
        }
    }

    public User requestUserFromToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        ResponseEntity<User> responseEntity = restTemplate.exchange(
                GET_USER_BY_TOKEN_ENDPOINT.withBase(serviceBaseUrl).headers(headers).getRequestEntity(),
                User.class
        );
        return responseEntity.getBody();
    }
}
