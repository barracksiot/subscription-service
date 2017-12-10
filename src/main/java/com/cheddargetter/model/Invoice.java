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
        "number",
        "type",
        "vatRate",
        "billingDatetime",
        "paidTransactionId",
        "createdDatetime",
        "charges",
        "transactions"
})
@EqualsAndHashCode
@ToString
public class Invoice {

    private String number;
    private String type;
    private String vatRate;
    private String billingDatetime;
    private String paidTransactionId;
    private String createdDatetime;
    private Charges charges;
    private Transactions transactions;
    @XmlAttribute(name = "id")
    private String id;

    public Invoice() {
    }

    public Invoice(String id, String number, String type, String vatRate, String billingDatetime, String paidTransactionId, String createdDatetime, Charges charges, Transactions transactions) {
        this.number = number;
        this.type = type;
        this.vatRate = vatRate;
        this.billingDatetime = billingDatetime;
        this.paidTransactionId = paidTransactionId;
        this.createdDatetime = createdDatetime;
        this.charges = charges;
        this.transactions = transactions;
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getVatRate() {
        return vatRate;
    }

    public String getBillingDatetime() {
        return billingDatetime;
    }

    public String getPaidTransactionId() {
        return paidTransactionId;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public Charges getCharges() {
        return charges;
    }

    public Transactions getTransactions() {
        return transactions;
    }

    public String getId() {
        return id;
    }

}
