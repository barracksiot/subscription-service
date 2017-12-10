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
import io.barracks.subscriptionservice.client.exception.DeviceServiceClientException;
import io.barracks.subscriptionservice.model.DataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static io.barracks.subscriptionservice.client.DeviceServiceClient.GET_DEVICE_COUNT_PER_USER_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(DeviceServiceClient.class)
public class DeviceServiceClientTest {
    @Autowired
    private DeviceServiceClient client;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockRestServiceServer mockServer;
    @Value("${io.barracks.deviceservice.base_url}")
    private String baseUrl;

    @Value("classpath:io/barracks/subscriptionservice/client/device-count.json")
    private Resource deviceCount;

    @Test
    public void getDeviceCountPerUser_whenSucceeds_shouldReturnDataSet() throws Exception {
        // Given
        final Endpoint endpoint = GET_DEVICE_COUNT_PER_USER_ENDPOINT;
        final DataSet expected = mapper.readValue(deviceCount.getInputStream(), DataSet.class);
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI()))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withSuccess().body(deviceCount).contentType(MediaType.APPLICATION_JSON));

        // When
        final DataSet result = client.getDeviceCountPerUser();

        // Then
        mockServer.verify();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getDeviceCountPerUser_whenFails_shouldThrowException() throws Exception {
        // Given
        final Endpoint endpoint = GET_DEVICE_COUNT_PER_USER_ENDPOINT;
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI()))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withBadRequest());

        // Then
        assertThatExceptionOfType(DeviceServiceClientException.class).isThrownBy(() -> client.getDeviceCountPerUser());
        mockServer.verify();
    }
}
