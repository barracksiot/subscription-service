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

package io.barracks.subscriptionservice.manager;

import com.cheddargetter.model.Customer;
import com.cheddargetter.model.Subscription;
import io.barracks.commons.exceptions.BarracksServiceClientException;
import io.barracks.subscriptionservice.client.AuthorizationServiceClient;
import io.barracks.subscriptionservice.client.CheddarGetterClient;
import io.barracks.subscriptionservice.client.DeviceServiceClient;
import io.barracks.subscriptionservice.client.DeviceServiceV2Client;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;
import io.barracks.subscriptionservice.model.DataSet;
import io.barracks.subscriptionservice.rest.entity.User;
import io.barracks.subscriptionservice.rest.entity.UserSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CustomersManager {
    private final Logger logger = LoggerFactory.getLogger(CustomersManager.class);
    private final CheddarGetterClient cheddarGetterClient;
    private final AuthorizationServiceClient authorizationServiceClient;
    private final DeviceServiceClient deviceServiceClient;
    private final DeviceServiceV2Client deviceServiceV2Client;

    @Autowired
    public CustomersManager(
            AuthorizationServiceClient authorizationServiceClient,
            CheddarGetterClient cheddarGetterClient,
            DeviceServiceClient deviceServiceClient,
            DeviceServiceV2Client deviceServiceV2Client) {
        this.authorizationServiceClient = authorizationServiceClient;
        this.cheddarGetterClient = cheddarGetterClient;
        this.deviceServiceClient = deviceServiceClient;
        this.deviceServiceV2Client = deviceServiceV2Client;
    }

    public Customer subscribe(User user, UserSubscription subscription) {
        Customer customer = null;
        try {
            customer = cheddarGetterClient.getCustomerData(user.getId());
        } catch (BarracksServiceClientException clientException) {
            if (clientException.getCause().getStatusCode() != HttpStatus.NOT_FOUND) {
                throw clientException;
            }
        }
        if (customer == null) {
            customer = cheddarGetterClient.createCustomer(user, subscription);
        } else {
            customer = cheddarGetterClient.editCustomer(user, subscription);
        }
        updateCustomerStatus(user.getId(), customer);
        return customer;
    }

    public void updateTrackedItems() {
        getDeviceCountPerUser().forEach(
                (userId, deviceCount) -> {
                    try {
                        cheddarGetterClient.setTrackedItem(userId, "DEVICES", deviceCount.doubleValue());
                    } catch (CheddarGetterClientException e) {
                        logger.error("Failed to set devices count (" + deviceCount + ") for " + userId, e);
                    }
                }
        );
    }

    private Map<String, BigDecimal> getDeviceCountPerUser() {
        final DataSet counts = deviceServiceClient.getDeviceCountPerUser();
        final DataSet countsV2 = deviceServiceV2Client.getDeviceCountPerUser();
        final Map<String, BigDecimal> result = counts.getValues();
        countsV2.getValues().forEach((k, v) -> result.merge(k, v, BigDecimal::add));
        return result;
    }

    public Customer verify(String userId) {
        Customer customer = cheddarGetterClient.getCustomerData(userId);
        updateCustomerStatus(userId, customer);
        return customer;
    }

    private void updateCustomerStatus(String userId, Customer customer) {
        Subscription current = customer.getSubscriptions().get(0);
        String cancelType = current.getCancelType();
        switch (cancelType) {
            case "":
                authorizationServiceClient.setStatus(userId, "active");
                break;
            default: // unknown, declined, expired, paypal-wait, customer
                authorizationServiceClient.setStatus(userId, "payment_pending");
                break;
        }
    }
}
