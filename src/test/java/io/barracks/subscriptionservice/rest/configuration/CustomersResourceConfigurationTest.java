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

import com.cheddargetter.model.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.commons.util.Endpoint;
import io.barracks.subscriptionservice.rest.CustomersResource;
import io.barracks.subscriptionservice.rest.entity.*;
import io.barracks.subscriptionservice.security.UserAuthentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

import static io.barracks.subscriptionservice.utils.TestEntities.getTestCustomer;
import static io.barracks.subscriptionservice.utils.TestEntities.getTestUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CustomersResource.class, secure = false)
public class CustomersResourceConfigurationTest {
    private static final String baseUrl = "https://not.barracks.io";
    private static final Endpoint CREATE_CUSTOMER_ENDPOINT = Endpoint.from(HttpMethod.POST, "/customers", "method={method}");
    private static final Endpoint VERIFY_USER_ENDPOINT = Endpoint.from(HttpMethod.GET, "/customers/verify");

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private CustomersResource resource;

    @Value("classpath:io/barracks/subscriptionservice/rest/configuration/subscribe-with-paypal.json")
    private Resource subscribeWithPaypal;
    @Value("classpath:io/barracks/subscriptionservice/rest/configuration/subscribe-with-credit-card.json")
    private Resource subscribeWithCreditCard;
    @Value("classpath:io/barracks/subscriptionservice/rest/configuration/subscribe-with-no-payment.json")
    private Resource subscribeWithNoPayment;

    private UserAuthentication principal;

    @Before
    public void setUp() throws Exception {
        final User user = getTestUser();
        principal = new UserAuthentication(user);
    }

    @Test
    public void createCustomerWithPaypal_shouldCallSubscribePaypal_andReturnResult() throws Exception {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final UserSubscription<PaypalPaymentDetails> registration = mapper.readValue(subscribeWithPaypal.getInputStream(), new TypeReference<UserSubscription<PaypalPaymentDetails>>() {
        });
        final Customer customer = getTestCustomer();
        when(resource.subscribePaypal(principal, registration)).thenReturn(customer);

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI("paypal"))
                        .principal(principal)
                        .content(FileCopyUtils.copyToByteArray(subscribeWithPaypal.getInputStream()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(resource).subscribePaypal(principal, registration);
        result.andExpect(status().isOk());
    }

    @Test
    public void createCustomerWithCreditCard_whenCallSubscribeCreditCard_andReturnResult() throws Exception {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final UserSubscription<CreditCardPaymentDetails> registration = mapper.readValue(subscribeWithCreditCard.getInputStream(), new TypeReference<UserSubscription<CreditCardPaymentDetails>>() {
        });
        final Customer customer = getTestCustomer();
        when(resource.subscribeCreditCard(principal, registration)).thenReturn(customer);

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI("cc"))
                        .principal(principal)
                        .content(FileCopyUtils.copyToByteArray(subscribeWithCreditCard.getInputStream()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(resource).subscribeCreditCard(principal, registration);
        result.andExpect(status().isOk());
    }

    @Test
    public void createCustomerWithNoDetails_whenCallSubscribeNone_andReturnResult() throws Exception {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final UserSubscription<NoPaymentDetails> registration = mapper.readValue(subscribeWithNoPayment.getInputStream(), new TypeReference<UserSubscription<NoPaymentDetails>>() {
        });
        final Customer customer = getTestCustomer();
        when(resource.subscribeNone(principal, registration)).thenReturn(customer);

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI("none"))
                        .principal(principal)
                        .content(FileCopyUtils.copyToByteArray(subscribeWithPaypal.getInputStream()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(resource).subscribeNone(principal, registration);
        result.andExpect(status().isOk());
    }

    @Test
    public void createCustomerWithPaypal_whenInvalid_shouldReturnBadRequest() throws Exception {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI("paypal"))
                        .principal(principal)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verifyZeroInteractions(resource);
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void createCustomerWithCreditCard_whenInvalid_shouldReturnBadRequest() throws Exception {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI("cc"))
                        .principal(principal)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verifyZeroInteractions(resource);
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void verifyCustomer_whenSucceeds_shouldReturnOk() throws Exception {
        // Given
        final Endpoint endpoint = VERIFY_USER_ENDPOINT;
        final Customer customer = getTestCustomer();
        when(resource.verify(principal)).thenReturn(customer);

        // When
        final ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .request(endpoint.getMethod(), endpoint.withBase(baseUrl).getURI())
                        .principal(principal)
        );

        // Then
        verify(resource).verify(principal);
        result.andExpect(status().isOk());
    }
}
