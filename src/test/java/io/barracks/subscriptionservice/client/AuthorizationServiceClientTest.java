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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.commons.util.Endpoint;
import io.barracks.subscriptionservice.client.exception.AuthorizationServiceClientException;
import io.barracks.subscriptionservice.rest.entity.User;
import net.minidev.json.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.UUID;

import static io.barracks.subscriptionservice.client.AuthorizationServiceClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(AuthorizationServiceClient.class)
public class AuthorizationServiceClientTest {
    @Autowired
    private AuthorizationServiceClient client;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.barracks.authorizationservice.base_url}")
    private String baseUrl;

    @Value("classpath:io/barracks/subscriptionservice/client/user.json")
    private Resource user;

    @Test
    public void getUserById_whenSucceeds_shouldReturnThatUser() throws IOException, ParseException {
        // Given
        final Endpoint endpoint = GET_USER_BY_ID_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        final User expected = objectMapper.readValue(user.getInputStream(), User.class);
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(userId)))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(user));

        // When
        final User result = client.getUserById(userId);

        // Then
        mockServer.verify();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getUserById_whenFails_shouldThrowException() {
        // Given
        final Endpoint endpoint = GET_USER_BY_ID_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(AuthorizationServiceClientException.class)
                .isThrownBy(() -> client.getUserById(userId));
        mockServer.verify();
    }

    @Test
    public void requestUserFromToken_whenSucceeds_shouldReturnThatUser() throws IOException, ParseException {
        // Given
        final Endpoint endpoint = GET_USER_BY_TOKEN_ENDPOINT;
        final String token = UUID.randomUUID().toString();
        final User expected = objectMapper.readValue(user.getInputStream(), User.class);
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI()))
                .andExpect(header("X-Auth-Token", token))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(user));

        // When
        final User result = client.requestUserFromToken(token);

        // Then
        mockServer.verify();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void requestUserFromToken_whenFails_shouldThrowException() {
        // Given
        final Endpoint endpoint = GET_USER_BY_TOKEN_ENDPOINT;
        final String token = UUID.randomUUID().toString();
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI()))
                .andExpect(header("X-Auth-Token", token))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // Then When
        assertThatExceptionOfType(HttpStatusCodeException.class)
                .isThrownBy(() -> client.requestUserFromToken(token));
        mockServer.verify();
    }

    @Test
    public void setStatus_whenSuccess_shouldNotThrowException() {
        // Given
        final Endpoint endpoint = SET_USER_STATUS_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        final String status = "VERIFIED";
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(userId, status)))
                .andRespond(withStatus(HttpStatus.OK));

        // When
        client.setStatus(userId, status);

        // Then
        mockServer.verify();
    }

    @Test
    public void setStatus_whenFailure_shouldThrowException() {
        // Given
        final Endpoint endpoint = SET_USER_STATUS_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        final String status = "VERIFIED";
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(userId, status)))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // Then / When
        assertThatExceptionOfType(AuthorizationServiceClientException.class)
                .isThrownBy(() -> client.setStatus(userId, status));
        mockServer.verify();
    }
}
