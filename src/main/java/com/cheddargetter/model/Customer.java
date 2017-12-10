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

package com.cheddargetter.model;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "firstName",
        "lastName",
        "company",
        "email",
        "notes",
        "gatewayToken",
        "isVatExempt",
        "vatNumber",
        "firstContactDatetime",
        "referer",
        "refererHost",
        "campaignSource",
        "campaignMedium",
        "campaignTerm",
        "campaignContent",
        "campaignName",
        "metaData",
        "createdDatetime",
        "modifiedDatetime",
        "subscriptions"
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Customer {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "code")
    private String code;
    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private String notes;
    private String gatewayToken;
    private boolean isVatExempt;
    private String vatNumber;
    private String firstContactDatetime;
    private String referer;
    private String refererHost;
    private String campaignSource;
    private String campaignMedium;
    private String campaignTerm;
    private String campaignContent;
    private String campaignName;
    private String metaData;
    private String createdDatetime;
    private String modifiedDatetime;
    @XmlElement(required = true)
    private Subscriptions subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions == null ? Collections.emptyList() : subscriptions.getSubscriptions();
    }

}
