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

package io.barracks.subscriptionservice.rest.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class CreditCardPaymentDetails extends PaymentDetails {
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    private final String company;
    private final String country;
    private final String address;
    private final String city;
    private final String zip;
    private final String state;
    @NotBlank
    private final String number;
    @NotBlank
    private final String expiration;
    @NotBlank
    private final String code;

    @JsonCreator
    public static CreditCardPaymentDetails fromJson(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("company") String company,
            @JsonProperty("country") String country,
            @JsonProperty("address") String address,
            @JsonProperty("city") String city,
            @JsonProperty("zip") String zip,
            @JsonProperty("state") String state,
            @JsonProperty("number") String number,
            @JsonProperty("expiration") String expiration,
            @JsonProperty("code") String code
    ) {
        return new CreditCardPaymentDetails(firstName, lastName, company, country, address, city, zip, state, number, expiration, code);
    }

    @Override
    PaymentMethod getPaymentMethod() {
        return PaymentMethod.CC;
    }
}
