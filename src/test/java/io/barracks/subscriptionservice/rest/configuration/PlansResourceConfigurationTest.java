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

package io.barracks.subscriptionservice.rest.configuration;

import com.cheddargetter.model.Plans;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.commons.util.Endpoint;
import io.barracks.subscriptionservice.rest.PlansResource;
import io.barracks.subscriptionservice.utils.PlansUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlansResource.class, secure = false)
public class PlansResourceConfigurationTest {
    private static final String baseUrl = "https://not.barracks.io";
    private static final Endpoint GET_PLANS_ENDPOINT = Endpoint.from(HttpMethod.GET, "/plans");

    @MockBean
    private PlansResource plansResource;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void getPlans_shouldCallGetPlans_andReturnResult() throws Exception {
        // Given
        final Endpoint endpoint = GET_PLANS_ENDPOINT;
        final Plans plans = PlansUtils.getPlans();
        doReturn(plans).when(plansResource).getPlans();

        // When
        final ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI()));

        // Then
        verify(plansResource).getPlans();
        result.andExpect(status().isOk()).andExpect(content().json(mapper.writeValueAsString(plans)));
    }
}
