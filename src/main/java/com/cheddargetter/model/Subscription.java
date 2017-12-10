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

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "gatewayToken",
        "gatewayAccount",
        "redirectUrl",
        "ccFirstName",
        "ccLastName",
        "ccCompany",
        "ccCountry",
        "ccAddress",
        "ccCity",
        "ccState",
        "ccZip",
        "ccType",
        "ccLastFour",
        "ccExpirationDate",
        "cancelType",
        "cancelReason",
        "canceledDatetime",
        "createdDatetime",
        "plans",
        "items",
        "invoices"
})
@EqualsAndHashCode
@ToString
public class Subscription {
    @XmlAttribute(name = "id")
    private String id;
    private String gatewayToken;
    private GatewayAccount gatewayAccount;
    private String redirectUrl;
    private String ccFirstName;
    private String ccLastName;
    private String ccCompany;
    private String ccCountry;
    private String ccAddress;
    private String ccCity;
    private String ccState;
    private String ccZip;
    private String ccType;
    private String ccLastFour;
    private String ccExpirationDate;
    private String cancelType;
    private String cancelReason;
    private String canceledDatetime;
    private String createdDatetime;
    @XmlElement(required = true)
    private Plans plans;
    private Items items;
    @XmlElement(required = true)
    private Invoices invoices;

    public Subscription() {
        this.plans = new Plans();
        this.items = new Items();
        this.invoices = new Invoices();
    }

    public Subscription(
            String id, String gatewayToken, GatewayAccount gatewayAccount, String redirectUrl,
            String ccFirstName, String ccLastName, String ccCompany,
            String ccCountry, String ccAddress, String ccCity, String ccState, String ccZip,
            String ccType, String ccLastFour, String ccExpirationDate,
            String canceledDatetime, String cancelType, String cancelReason, String createdDatetime,
            Plans plans, Items items, Invoices invoices) {
        this.id = id;
        this.gatewayToken = gatewayToken;
        this.gatewayAccount = gatewayAccount;
        this.redirectUrl = redirectUrl;
        this.ccFirstName = ccFirstName;
        this.ccLastName = ccLastName;
        this.ccCompany = ccCompany;
        this.ccCountry = ccCountry;
        this.ccAddress = ccAddress;
        this.ccCity = ccCity;
        this.ccState = ccState;
        this.ccZip = ccZip;
        this.ccType = ccType;
        this.ccLastFour = ccLastFour;
        this.ccExpirationDate = ccExpirationDate;
        this.cancelType = cancelType;
        this.cancelReason = cancelReason;
        this.canceledDatetime = canceledDatetime;
        this.createdDatetime = createdDatetime;
        this.plans = new Plans(plans.getPlans());
        this.items = new Items(items.getItems());
        this.invoices = new Invoices(invoices.getInvoices());
    }

    public String getId() {
        return id;
    }

    public String getGatewayToken() {
        return gatewayToken;
    }

    public GatewayAccount getGatewayAccount() {
        return gatewayAccount;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getCcFirstName() {
        return ccFirstName;
    }

    public String getCcLastName() {
        return ccLastName;
    }

    public String getCcCompany() {
        return ccCompany;
    }

    public String getCcCountry() {
        return ccCountry;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public String getCcCity() {
        return ccCity;
    }

    public String getCcState() {
        return ccState;
    }

    public String getCcZip() {
        return ccZip;
    }

    public String getCcType() {
        return ccType;
    }

    public String getCcLastFour() {
        return ccLastFour;
    }

    public String getCcExpirationDate() {
        return ccExpirationDate;
    }

    public String getCancelType() {
        return cancelType;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public String getCanceledDatetime() {
        return canceledDatetime;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public Plan getPlan() {
        return plans.getPlans().get(0);
    }

    public List<Item> getItems() {
        return items.getItems();
    }

    public List<Invoice> getInvoices() {
        return invoices.getInvoices();
    }
}
