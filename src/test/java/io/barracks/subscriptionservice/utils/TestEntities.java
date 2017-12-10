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

package io.barracks.subscriptionservice.utils;

import com.cheddargetter.model.Customer;
import com.cheddargetter.model.Customers;
import com.cheddargetter.model.Subscriptions;
import io.barracks.subscriptionservice.rest.entity.*;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

public class TestEntities {

    public static User getTestUser() {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .email("test@cheddargetter.com")
                .phone("+12345678900")
                .firstName("Toto")
                .lastName("Tata")
                .company("ACME")
                .build();
    }

    public static UserSubscription getTestUserSubscription(PaymentMethod method) {
        return UserSubscription.builder()
                .plan("FREE")
                .paymentDetails(getTestPaymentDetails(method))
                .build();
    }

    public static PaymentDetails getTestPaymentDetails(PaymentMethod method) {
        switch (method) {
            case CC:
                return CreditCardPaymentDetails.builder()
                        .firstName("Cheddar")
                        .lastName("Getter")
                        .company("Barracks")
                        .code(UUID.randomUUID().toString())
                        .build();
            case PAYPAL:
                return PaypalPaymentDetails.builder()
                        .cancelUrl(URI.create("http://barracks.io/registration/cancel"))
                        .returnUrl(URI.create("http://barracks.io/registration/success"))
                        .build();
            case NONE:
                return NoPaymentDetails.builder().build();
        }
        return null;
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .code(UUID.randomUUID().toString())
                .subscriptions(
                        Subscriptions.builder()
                                .subscriptions(Collections.emptyList()).build()
                )
                .build();
    }

    public static Customers getTestCustomers(Class clazz, String... params) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Customers.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName());
        for (String param : params) {
            sb.append("-");
            sb.append(param);
        }
        sb.append(".xml");
        return (Customers) unmarshaller.unmarshal(
                new ClassPathResource(sb.toString(), clazz).getInputStream()
        );
    }
}
