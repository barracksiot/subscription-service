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

import io.barracks.subscriptionservice.rest.entity.CreditCardPaymentDetails;
import io.barracks.subscriptionservice.rest.entity.NoPaymentDetails;
import io.barracks.subscriptionservice.rest.entity.PaypalPaymentDetails;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentDetailsUtils {
    public static PaypalPaymentDetails getPaypalPaymentDetails() {
        final PaypalPaymentDetails details = PaypalPaymentDetails.builder()
                .cancelUrl(URI.create("https://not.barracks.io/" + UUID.randomUUID().toString()))
                .returnUrl(URI.create("https://not.barracks.io/" + UUID.randomUUID().toString()))
                .build();
        assertThat(details).hasNoNullFieldsOrProperties();
        return details;
    }

    public static CreditCardPaymentDetails getCreditCardPaymentDetails() {
        final CreditCardPaymentDetails details = CreditCardPaymentDetails.builder()
                .address(UUID.randomUUID().toString())
                .city(UUID.randomUUID().toString())
                .code(UUID.randomUUID().toString())
                .company(UUID.randomUUID().toString())
                .country(UUID.randomUUID().toString())
                .expiration(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .number(UUID.randomUUID().toString())
                .state(UUID.randomUUID().toString())
                .zip(UUID.randomUUID().toString())
                .build();
        assertThat(details).hasNoNullFieldsOrProperties();
        return details;
    }

    public static NoPaymentDetails getNoPaymentDetails() {
        final NoPaymentDetails details = NoPaymentDetails.builder().build();
        assertThat(details).hasNoNullFieldsOrProperties();
        return details;
    }
}
