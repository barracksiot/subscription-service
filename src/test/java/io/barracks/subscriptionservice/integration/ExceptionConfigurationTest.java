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

package io.barracks.subscriptionservice.integration;

import io.barracks.commons.test.AnyBarracksClientException;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;
import io.barracks.subscriptionservice.config.ExceptionConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ExceptionConfigurationTest.TestExceptionApp.class, ExceptionConfigurationTest.ExceptionThrowingResource.class, ExceptionConfig.class}
)
public class ExceptionConfigurationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void getAnyException_shouldReturnFormattedException() throws Exception {
        // When
        final ResponseEntity<Void> response = testRestTemplate.exchange("/exception/any", HttpMethod.GET, null, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void getCheddarGetterClientException_shouldReturnFormattedException() throws Exception {
        // When
        final ResponseEntity<Void> response = testRestTemplate.exchange("/exception/cheddargetter", HttpMethod.GET, null, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    @SpringBootApplication
    @EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
    public static class TestExceptionApp {
    }

    @RestController
    @RequestMapping(path = "/exception")
    public static class ExceptionThrowingResource {
        @RequestMapping(method = RequestMethod.GET, path = "/any")
        public Object getAnyException() {
            throw AnyBarracksClientException.from(HttpStatus.BAD_REQUEST);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/cheddargetter")
        public Object getCheddarGetterException() {
            throw new CheddarGetterClientException(new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED));
        }
    }
}
