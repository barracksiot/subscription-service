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
        "name",
        "description",
        "isActive",
        "isFree",
        "trialDays",
        "billingFrequency",
        "billingFrequencyPer",
        "billingFrequencyUnit",
        "billingFrequencyQuantity",
        "setupChargeCode",
        "setupChargeAmount",
        "recurringChargeCode",
        "recurringChargeAmount",
        "createdDatetime",
        "items"
})
@EqualsAndHashCode
@ToString
public class Plan {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "code")
    private String code;
    private String name;
    private String description;
    private boolean isActive;
    private boolean isFree = false;
    private String trialDays;
    private String billingFrequency;
    private String billingFrequencyPer;
    private String billingFrequencyUnit;
    private String billingFrequencyQuantity;
    private String setupChargeCode;
    private String setupChargeAmount;
    private String recurringChargeCode;
    private String recurringChargeAmount;
    private String createdDatetime;
    @XmlElement(required = true)
    private PlanItems items;

    private Plan() {
        this.items = new PlanItems();
    }

    public Plan(
            String id, String code, String name, String description, boolean isActive, boolean isFree, String trialDays,
            String billingFrequency, String billingFrequencyPer, String billingFrequencyUnit,
            String billingFrequencyQuantity, String setupChargeCode, String setupChargeAmount,
            String recurringChargeCode, String recurringChargeAmount, String createdDatetime, List<PlanItem> items) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.isFree = isFree;
        this.trialDays = trialDays;
        this.billingFrequency = billingFrequency;
        this.billingFrequencyPer = billingFrequencyPer;
        this.billingFrequencyUnit = billingFrequencyUnit;
        this.billingFrequencyQuantity = billingFrequencyQuantity;
        this.setupChargeCode = setupChargeCode;
        this.setupChargeAmount = setupChargeAmount;
        this.recurringChargeCode = recurringChargeCode;
        this.recurringChargeAmount = recurringChargeAmount;
        this.createdDatetime = createdDatetime;
        this.items = new PlanItems(items);
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getTrialDays() {
        return trialDays;
    }

    public String getBillingFrequency() {
        return billingFrequency;
    }

    public String getBillingFrequencyPer() {
        return billingFrequencyPer;
    }

    public String getBillingFrequencyUnit() {
        return billingFrequencyUnit;
    }

    public String getBillingFrequencyQuantity() {
        return billingFrequencyQuantity;
    }

    public String getSetupChargeCode() {
        return setupChargeCode;
    }

    public String getSetupChargeAmount() {
        return setupChargeAmount;
    }

    public String getRecurringChargeCode() {
        return recurringChargeCode;
    }

    public String getRecurringChargeAmount() {
        return recurringChargeAmount;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public List<PlanItem> getItems() {
        return items.getItems();
    }

}
