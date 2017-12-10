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
        "type",
        "quantity",
        "eachAmount",
        "description",
        "createdDatetime"
})
@EqualsAndHashCode
@ToString
public class Charge {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "code")
    private String code;
    private String type;
    private String quantity;
    private String eachAmount;
    private String description;
    private String createdDatetime;

    public Charge() {
    }

    public Charge(String id, String code, String type, String quantity, String eachAmount, String description, String createdDatetime) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.quantity = quantity;
        this.eachAmount = eachAmount;
        this.description = description;
        this.createdDatetime = createdDatetime;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getEachAmount() {
        return eachAmount;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

}
