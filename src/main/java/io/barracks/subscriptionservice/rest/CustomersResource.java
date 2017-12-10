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

package io.barracks.subscriptionservice.rest;

import com.cheddargetter.model.Customer;
import io.barracks.subscriptionservice.manager.CustomersManager;
import io.barracks.subscriptionservice.rest.entity.CreditCardPaymentDetails;
import io.barracks.subscriptionservice.rest.entity.NoPaymentDetails;
import io.barracks.subscriptionservice.rest.entity.PaypalPaymentDetails;
import io.barracks.subscriptionservice.rest.entity.UserSubscription;
import io.barracks.subscriptionservice.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/customers")
public class CustomersResource {

    @Autowired
    private CustomersManager manager;

    @RequestMapping(
            method = RequestMethod.POST,
            params = "method=paypal"
    )
    public Customer subscribePaypal(Authentication authentication, @Valid @RequestBody UserSubscription<PaypalPaymentDetails> userSubscription) {
        return manager.subscribe(((UserAuthentication) authentication).getDetails(), userSubscription);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            params = "method=cc"
    )
    public Customer subscribeCreditCard(Authentication authentication, @Valid @RequestBody UserSubscription<CreditCardPaymentDetails> userSubscription) {
        return manager.subscribe(((UserAuthentication) authentication).getDetails(), userSubscription);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            params = "method=none"
    )
    public Customer subscribeNone(Authentication authentication, @Valid @RequestBody UserSubscription<NoPaymentDetails> userSubscription) {
        return manager.subscribe(((UserAuthentication) authentication).getDetails(), userSubscription);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/verify"
    )
    public Customer verify(Principal principal) {
        return manager.verify(principal.getName());
    }
}
