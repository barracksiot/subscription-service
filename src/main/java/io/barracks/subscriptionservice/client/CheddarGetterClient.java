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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class CheddarGetterClient {
    static final Endpoint GET_PLANS_ENDPOINT = Endpoint.from(HttpMethod.GET, "/plans/get/productCode/{productCode}/");
    static final Endpoint GET_PLAN_ENDPOINT = Endpoint.from(HttpMethod.GET, "/plans/get/productCode/{productCode}/code/{code}/");
    static final Endpoint SET_TRACKED_ITEM_ENDPOINT = Endpoint.from(HttpMethod.POST, "/customers/set-item-quantity/productCode/{productCode}/code/{code}/itemCode/{itemCode}/");
    static final Endpoint GET_CUSTOMER_ENDPOINT = Endpoint.from(HttpMethod.GET, "/customers/get/productCode/{productCode}/code/{code}/");
    static final Endpoint CREATE_CUSTOMER_ENDPOINT = Endpoint.from(HttpMethod.POST, "/customers/new/productCode/{productCode}/");
    static final Endpoint EDIT_CUSTOMER_ENDPOINT = Endpoint.from(HttpMethod.POST, "/customers/edit/productCode/{productCode}/code/{code}/");

    private final String serviceBaseUrl;
    private final String productCode;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(CheddarGetterClient.class);

    @Autowired
    public CheddarGetterClient(
            @Value("${com.cheddargetter.base_url}") String serviceBaseUrl,
            @Value("${com.cheddargetter.product_code}") String productCode,
            @Value("${com.cheddargetter.password}") String credentials,
            RestTemplateBuilder restTemplateBuilder) {
        this.serviceBaseUrl = serviceBaseUrl;
        this.productCode = productCode;
        this.restTemplate = restTemplateBuilder
                .additionalInterceptors(new BasicAuthenticationInterceptor(credentials))
                .build();
    }

    public Plans getPlans() {
        try {
            return restTemplate.exchange(
                    GET_PLANS_ENDPOINT.withBase(serviceBaseUrl).getRequestEntity(productCode),
                    Plans.class
            ).getBody();
        } catch (HttpStatusCodeException e) {
            throw new CheddarGetterClientException(e);
        }
    }

    public Plan getPlan(String code) {
        try {
            return restTemplate.exchange(
                    GET_PLAN_ENDPOINT.withBase(serviceBaseUrl).getRequestEntity(productCode, code),
                    Plans.class
            ).getBody().getPlans().get(0);
        } catch (HttpStatusCodeException e) {
            throw new CheddarGetterClientException(e);
        }
    }

    public Customer setTrackedItem(String userId, String item, double count) {
        try {
            final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("quantity", String.format("%.4f", count));
            return restTemplate.exchange(
                    SET_TRACKED_ITEM_ENDPOINT.withBase(serviceBaseUrl).body(parameters).getRequestEntity(productCode, userId, item),
                    Customers.class
            ).getBody().getCustomers().get(0);
        } catch (HttpStatusCodeException e) {
            throw new CheddarGetterClientException(e);
        }
    }

    public Customer getCustomerData(String userId) {
        try {
            return restTemplate.exchange(
                    GET_CUSTOMER_ENDPOINT.withBase(serviceBaseUrl).getRequestEntity(productCode, userId),
                    Customers.class
            ).getBody().getCustomers().get(0);
        } catch (HttpStatusCodeException e) {
            throw new CheddarGetterClientException(e);
        }
    }

    public Customer createCustomer(User user, UserSubscription subscription) {
        return createOrUpdate(user, subscription, false);
    }

    public Customer editCustomer(User user, UserSubscription subscription) {
        return createOrUpdate(user, subscription, true);
    }

    Customer createOrUpdate(User user, UserSubscription subscription, boolean update) {
        MultiValueMap<String, String> parameters = mapUserSubscription(user, subscription);
        RequestEntity requestEntity = update ?
                EDIT_CUSTOMER_ENDPOINT.withBase(serviceBaseUrl).body(parameters).getRequestEntity(productCode, user.getId()) :
                CREATE_CUSTOMER_ENDPOINT.withBase(serviceBaseUrl).body(parameters).getRequestEntity(productCode);
        try {
            return restTemplate.exchange(
                    requestEntity,
                    Customers.class
            ).getBody().getCustomers().get(0);
        } catch (HttpStatusCodeException e) {
            logger.warn("Request to '{}' with '{}' failed with status '{}' and message '{}'", requestEntity.getUrl(), parameters, e.getStatusCode(), e.getResponseBodyAsString());
            throw new CheddarGetterClientException(e);
        }
    }

    private MultiValueMap<String, String> mapUserSubscription(User user, UserSubscription subscription) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", user.getId()); // TODO Max length : 255
        parameters.add("firstName", user.getFirstName()); // TODO Max length : 40
        parameters.add("lastName", user.getLastName()); // TODO Max length : 40
        parameters.add("email", user.getEmail()); // TODO valid
        parameters.add("company", user.getCompany()); // TODO Max length : 60
        parameters.add("notes", user.getPhone()); // TODO Max length : 255
        PaymentDetails details = subscription.getPaymentDetails();
        parameters.add("subscription[method]", details.getMethod());
        parameters.add("subscription[planCode]", subscription.getPlan());
        if (details instanceof PaypalPaymentDetails) {
            PaypalPaymentDetails paypalPaymentDetails = (PaypalPaymentDetails) details;
            parameters.add("subscription[returnUrl]", paypalPaymentDetails.getReturnUrl().toString());
            parameters.add("subscription[cancelUrl]", paypalPaymentDetails.getCancelUrl().toString());
        } else if (details instanceof CreditCardPaymentDetails) {
            CreditCardPaymentDetails creditCardPaymentDetails = (CreditCardPaymentDetails) details;
            parameters.add("subscription[ccNumber]", creditCardPaymentDetails.getNumber());
            parameters.add("subscription[ccExpiration]", creditCardPaymentDetails.getExpiration());
            parameters.add("subscription[ccCardCode]", creditCardPaymentDetails.getCode());
            parameters.add("subscription[ccFirstName]", creditCardPaymentDetails.getFirstName()); // TODO Max length : 40
            parameters.add("subscription[ccLastName]", creditCardPaymentDetails.getLastName()); // TODO Max length : 40
            parameters.add("subscription[ccAddress]", creditCardPaymentDetails.getAddress());
            parameters.add("subscription[ccCity]", creditCardPaymentDetails.getCity());
            parameters.add("subscription[ccZip]", creditCardPaymentDetails.getZip());
            parameters.add("subscription[ccState]", creditCardPaymentDetails.getState());
            parameters.add("subscription[ccCountry]", creditCardPaymentDetails.getCountry());
            parameters.add("subscription[ccCompany]", creditCardPaymentDetails.getCompany());
        }
        return parameters;
    }
}
