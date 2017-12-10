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

import com.cheddargetter.model.Customer;
import com.cheddargetter.model.Customers;
import com.cheddargetter.model.Plan;
import com.cheddargetter.model.Plans;
import io.barracks.commons.util.Endpoint;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;
import io.barracks.subscriptionservice.rest.entity.*;
import io.barracks.subscriptionservice.utils.XmlObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.util.UUID;

import static io.barracks.subscriptionservice.client.CheddarGetterClient.*;
import static io.barracks.subscriptionservice.utils.TestEntities.getTestUser;
import static io.barracks.subscriptionservice.utils.TestEntities.getTestUserSubscription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(CheddarGetterClient.class)
public class CheddarGetterClientTest {
    @Value("${com.cheddargetter.base_url}")
    private String baseUrl;
    @Value("${com.cheddargetter.product_code}")
    private String productCode;
    @Value("${com.cheddargetter.password}")
    private String CREDENTIALS;
    @SpyBean
    private CheddarGetterClient client;
    @Autowired
    private MockRestServiceServer mockServer;
    private XmlObjectMapper mapper = new XmlObjectMapper();

    @Value("classpath:io/barracks/subscriptionservice/client/user.xml")
    private Resource user;
    @Value("classpath:io/barracks/subscriptionservice/client/plans.xml")
    private Resource plans;
    @Value("classpath:io/barracks/subscriptionservice/client/plan.xml")
    private Resource plan;

    private String AUTH;

    private static RequestMatcher queryContains(String key, String value) {
        return request -> {
            MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
            UriComponents components = UriComponentsBuilder.fromUriString("?" + mockRequest.getBodyAsString()).build();
            String encodedKey = URLEncoder.encode(key, "UTF-8");
            String encodedValue = value == null ? null : URLEncoder.encode(value, "UTF-8");
            assertTrue("Undefined key '" + key + "'", components.getQueryParams().containsKey(encodedKey));
            assertTrue("'" + key + "' does not contain '" + value + "'", value == null || components.getQueryParams().get(encodedKey).contains(encodedValue));
        };
    }

    @Before
    public void setup() {
        this.AUTH = "Basic " + CREDENTIALS;
    }

    @Test
    public void getPlans_whenSucceeds_shouldReturnTheListOfPlans() {
        // Given
        final Endpoint endpoint = GET_PLANS_ENDPOINT;
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(header("Authorization", AUTH))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(plans)
                );

        // When
        Plans plans = client.getPlans();

        // Then
        mockServer.verify();
        assertEquals(plans.getPlans().size(), 5);
    }

    @Test
    public void getPlans_whenFails_shouldForwardError() {
        // Given
        final Endpoint endpoint = GET_PLANS_ENDPOINT;
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class)
                .isThrownBy(() -> client.getPlans());
        mockServer.verify();
    }

    @Test
    public void getPlan_whenSucceeds_shouldReturnTheListOfPlans() {
        // Given
        final Endpoint endpoint = GET_PLAN_ENDPOINT;
        final String planCode = "DEVELOPER";
        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(header("Authorization", AUTH))
                .andExpect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, planCode)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(plan)
                );

        // When
        Plan plans = client.getPlan(planCode);

        // Then
        mockServer.verify();
        assertEquals(plans.getCode(), planCode);
    }

    @Test
    public void getPlan_whenFails_shouldForwardError() {
        // Given
        final Endpoint endpoint = GET_PLAN_ENDPOINT;
        final String planCode = "DEVELOPER";
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, planCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class)
                .isThrownBy(() -> client.getPlan(planCode));
        mockServer.verify();
    }

    @Test
    public void createCustomer_shouldCallCreateOrUpdateWithFalse() {
        // Given
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        doReturn(null).when(client).createOrUpdate(user, subscription, false);

        // When
        final Customer customer = client.createCustomer(user, subscription);

        // Then
        verify(client).createOrUpdate(user, subscription, false);
        assertThat(customer).isNull();
    }

    @Test
    public void editCustomer_shouldCallCreateOrUpdateWithTrue() {
        // Given
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        doReturn(null).when(client).createOrUpdate(user, subscription, true);

        // When
        final Customer customer = client.editCustomer(user, subscription);

        // Then
        verify(client).createOrUpdate(user, subscription, true);
        assertThat(customer).isNull();
    }

    @Test
    public void createCustomerWithPaypal_whenSucceeds_shouldReturnFirstCustomer() {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        final PaypalPaymentDetails paymentDetails = (PaypalPaymentDetails) subscription.getPaymentDetails();
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(queryContains("code", user.getId()))
                .andExpect(queryContains("firstName", user.getFirstName()))
                .andExpect(queryContains("lastName", user.getLastName()))
                .andExpect(queryContains("email", user.getEmail()))
                .andExpect(queryContains("company", user.getCompany()))
                .andExpect(queryContains("notes", user.getPhone()))
                .andExpect(queryContains("subscription[planCode]", subscription.getPlan()))
                .andExpect(queryContains("subscription[method]", "paypal"))
                .andExpect(queryContains("subscription[returnUrl]", paymentDetails.getReturnUrl().toString()))
                .andExpect(queryContains("subscription[cancelUrl]", paymentDetails.getCancelUrl().toString()))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(this.user)
                );

        // When
        Customer response = client.createOrUpdate(user, subscription, false);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void createCustomerWithCreditCard_whenSucceeds_shouldReturnFirstCustomer() {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.CC);
        final CreditCardPaymentDetails paymentDetails = (CreditCardPaymentDetails) subscription.getPaymentDetails();

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(queryContains("code", user.getId()))
                .andExpect(queryContains("firstName", user.getFirstName()))
                .andExpect(queryContains("lastName", user.getLastName()))
                .andExpect(queryContains("email", user.getEmail()))
                .andExpect(queryContains("company", user.getCompany()))
                .andExpect(queryContains("notes", user.getPhone()))
                .andExpect(queryContains("subscription[planCode]", subscription.getPlan()))
                .andExpect(queryContains("subscription[method]", "cc"))
                .andExpect(queryContains("subscription[ccCardCode]", paymentDetails.getCode()))
                .andExpect(queryContains("subscription[ccNumber]", paymentDetails.getNumber()))
                .andExpect(queryContains("subscription[ccExpiration]", paymentDetails.getExpiration()))
                .andExpect(queryContains("subscription[ccFirstName]", paymentDetails.getFirstName()))
                .andExpect(queryContains("subscription[ccLastName]", paymentDetails.getLastName()))
                .andExpect(queryContains("subscription[ccCompany]", paymentDetails.getCompany()))
                .andExpect(queryContains("subscription[ccCountry]", paymentDetails.getCountry()))
                .andExpect(queryContains("subscription[ccAddress]", paymentDetails.getAddress()))
                .andExpect(queryContains("subscription[ccCity]", paymentDetails.getCity()))
                .andExpect(queryContains("subscription[ccState]", paymentDetails.getState()))
                .andExpect(queryContains("subscription[ccZip]", paymentDetails.getZip()))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(this.user)
                );

        // When
        Customer response = client.createOrUpdate(user, subscription, false);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void createCustomerWithoutDetails_whenSucceeds_shouldReturnCustomer() {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.NONE);

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(queryContains("code", user.getId()))
                .andExpect(queryContains("firstName", user.getFirstName()))
                .andExpect(queryContains("lastName", user.getLastName()))
                .andExpect(queryContains("email", user.getEmail()))
                .andExpect(queryContains("company", user.getCompany()))
                .andExpect(queryContains("notes", user.getPhone()))
                .andExpect(queryContains("subscription[planCode]", subscription.getPlan()))
                .andExpect(queryContains("subscription[method]", ""))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(this.user)
                );

        // When
        Customer response = client.createOrUpdate(user, subscription, false);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void updateCustomerWithPaypal_whenSucceeds_shouldReturnCustomer() {
        // Given
        final Endpoint endpoint = EDIT_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        final PaypalPaymentDetails paymentDetails = (PaypalPaymentDetails) subscription.getPaymentDetails();

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, user.getId())))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(queryContains("code", user.getId()))
                .andExpect(queryContains("firstName", user.getFirstName()))
                .andExpect(queryContains("lastName", user.getLastName()))
                .andExpect(queryContains("email", user.getEmail()))
                .andExpect(queryContains("company", user.getCompany()))
                .andExpect(queryContains("notes", user.getPhone()))
                .andExpect(queryContains("subscription[planCode]", subscription.getPlan()))
                .andExpect(queryContains("subscription[method]", "paypal"))
                .andExpect(queryContains("subscription[returnUrl]", paymentDetails.getReturnUrl().toString()))
                .andExpect(queryContains("subscription[cancelUrl]", paymentDetails.getCancelUrl().toString()))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(this.user)
                );

        // When
        Customer response = client.createOrUpdate(user, subscription, true);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void updateCustomerWithCreditCard_whenSucceeds_shouldReturnCustomer() {
        // Given
        final Endpoint endpoint = EDIT_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.CC);
        final CreditCardPaymentDetails paymentDetails = (CreditCardPaymentDetails) subscription.getPaymentDetails();

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, user.getId())))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(queryContains("code", user.getId()))
                .andExpect(queryContains("firstName", user.getFirstName()))
                .andExpect(queryContains("lastName", user.getLastName()))
                .andExpect(queryContains("email", user.getEmail()))
                .andExpect(queryContains("company", user.getCompany()))
                .andExpect(queryContains("notes", user.getPhone()))
                .andExpect(queryContains("subscription[planCode]", subscription.getPlan()))
                .andExpect(queryContains("subscription[method]", "cc"))
                .andExpect(queryContains("subscription[ccCardCode]", paymentDetails.getCode()))
                .andExpect(queryContains("subscription[ccNumber]", paymentDetails.getNumber()))
                .andExpect(queryContains("subscription[ccExpiration]", paymentDetails.getExpiration()))
                .andExpect(queryContains("subscription[ccFirstName]", paymentDetails.getFirstName()))
                .andExpect(queryContains("subscription[ccLastName]", paymentDetails.getLastName()))
                .andExpect(queryContains("subscription[ccCompany]", paymentDetails.getCompany()))
                .andExpect(queryContains("subscription[ccCountry]", paymentDetails.getCountry()))
                .andExpect(queryContains("subscription[ccAddress]", paymentDetails.getAddress()))
                .andExpect(queryContains("subscription[ccCity]", paymentDetails.getCity()))
                .andExpect(queryContains("subscription[ccState]", paymentDetails.getState()))
                .andExpect(queryContains("subscription[ccZip]", paymentDetails.getZip()))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(this.user)
                );

        // When
        Customer response = client.createOrUpdate(user, subscription, true);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void createCustomer_whenFails_shouldThrowException() {
        // Given
        final Endpoint endpoint = CREATE_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class)
                .isThrownBy(() -> client.createOrUpdate(user, subscription, false));
        mockServer.verify();
    }

    @Test
    public void updateCustomer_whenFails_shouldThrowException() {
        // Given
        final Endpoint endpoint = EDIT_CUSTOMER_ENDPOINT;
        final User user = getTestUser();
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, user.getId())))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class)
                .isThrownBy(() -> client.createOrUpdate(user, subscription, true));
        mockServer.verify();
    }

    @Test
    public void getCustomer_whenSucceed_shouldReturnCustomer() {
        // Given
        final Endpoint endpoint = GET_CUSTOMER_ENDPOINT;
        final String userId = UUID.randomUUID().toString();

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, userId)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(user)
                );

        // When
        Customer response = client.getCustomerData(userId);

        // Then
        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    public void getCustomer_whenFails_shouldThrowException() {
        // Given
        final Endpoint endpoint = GET_CUSTOMER_ENDPOINT;
        final String userId = UUID.randomUUID().toString();

        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, userId)))
                .andExpect(header("Authorization", AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class).isThrownBy(() -> client.getCustomerData(userId));
        mockServer.verify();
    }

    @Test
    public void setTrackedItem_whenSucceeds_shouldReturnCustomer() throws Exception {
        // Given
        final Endpoint endpoint = SET_TRACKED_ITEM_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        final String item = UUID.randomUUID().toString();
        final double count = 42.4242f;
        final MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>() {{
            add("quantity", String.format("%.4f", count));
        }};
        final Customer expected = ((Customers) mapper.getUnmarshaller(Customers.class).unmarshal(user.getFile())).getCustomers().get(0);
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, userId, item)))
                .andExpect(header(HttpHeaders.AUTHORIZATION, AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(content().formData(content))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_XML).body(user));

        // When
        final Customer result = client.setTrackedItem(userId, item, count);

        // Then
        mockServer.verify();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void setTrackedItem_whenFails_shouldThrowException() throws Exception {
        // Given
        final Endpoint endpoint = SET_TRACKED_ITEM_ENDPOINT;
        final String userId = UUID.randomUUID().toString();
        final String item = UUID.randomUUID().toString();
        final double count = 42.4242f;
        final MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>() {{
            add("quantity", String.format("%.4f", count));
        }};
        mockServer.expect(requestTo(endpoint.withBase(baseUrl).getURI(productCode, userId, item)))
                .andExpect(header(HttpHeaders.AUTHORIZATION, AUTH))
                .andExpect(method(endpoint.getMethod()))
                .andExpect(content().formData(content))
                .andRespond(withStatus(HttpStatus.PRECONDITION_FAILED));

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class).isThrownBy(() -> client.setTrackedItem(userId, item, count));
        mockServer.verify();
    }
}
