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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "parentId",
        "gatewayToken",
        "amount",
        "memo",
        "response",
        "responseReason",
        "transactedDatetime",
        "createdDatetime",
        "gatewayAccount"
})
@EqualsAndHashCode
@ToString
public class Transaction {

    private String parentId;
    private String gatewayToken;
    private String amount;
    private String memo;
    private String response;
    private String responseReason;
    private String transactedDatetime;
    private String createdDatetime;
    private GatewayAccount gatewayAccount;
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "code")
    private String code;

    public Transaction() {
    }

    public Transaction(
            String id, String code, String parentId, String gatewayToken, String amount, String memo, String response,
            String responseReason, String transactedDatetime, String createdDatetime,
            GatewayAccount gatewayAccount) {
        this.parentId = parentId;
        this.gatewayToken = gatewayToken;
        this.amount = amount;
        this.memo = memo;
        this.response = response;
        this.responseReason = responseReason;
        this.transactedDatetime = transactedDatetime;
        this.createdDatetime = createdDatetime;
        this.gatewayAccount = gatewayAccount;
        this.id = id;
        this.code = code;
    }

    public String getParentId() {
        return parentId;
    }

    public String getGatewayToken() {
        return gatewayToken;
    }

    public String getAmount() {
        return amount;
    }

    public String getMemo() {
        return memo;
    }

    public String getResponse() {
        return response;
    }

    public String getResponseReason() {
        return responseReason;
    }

    public String getTransactedDatetime() {
        return transactedDatetime;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public GatewayAccount getGatewayAccount() {
        return gatewayAccount;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

}
