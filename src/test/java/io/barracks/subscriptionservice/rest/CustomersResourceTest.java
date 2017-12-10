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
import io.barracks.subscriptionservice.rest.entity.*;
import io.barracks.subscriptionservice.security.UserAuthentication;
import io.barracks.subscriptionservice.utils.UserSubscriptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static io.barracks.subscriptionservice.utils.TestEntities.getTestCustomer;
import static io.barracks.subscriptionservice.utils.TestEntities.getTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomersResourceTest {
    @Mock
    private CustomersManager manager;

    @InjectMocks
    private CustomersResource resource;

    private UserAuthentication principal;

    @Before
    public void setUp() throws Exception {
        final User user = getTestUser();
        principal = new UserAuthentication(user);
    }

    @Test
    public void subscribePaypal_shouldCallManager_andReturnResult() {
        // Given
        final Customer expected = getTestCustomer();
        final UserSubscription<PaypalPaymentDetails> subscription = UserSubscriptionUtils.getPaypalSubscription();
        doReturn(expected).when(manager).subscribe(principal.getDetails(), subscription);

        // When
        final Customer result = resource.subscribePaypal(principal, subscription);

        // Then
        verify(manager).subscribe(principal.getDetails(), subscription);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void subscribeCc_shouldCallManager_andReturnResult() {
        // Given
        final Customer expected = getTestCustomer();
        final UserSubscription<CreditCardPaymentDetails> subscription = UserSubscriptionUtils.getCreditCardSubscription();
        doReturn(expected).when(manager).subscribe(principal.getDetails(), subscription);

        // When
        final Customer result = resource.subscribeCreditCard(principal, subscription);

        // Then
        verify(manager).subscribe(principal.getDetails(), subscription);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void subscribeNone_shouldCallManager_andReturnResult() {
        // Given
        final Customer expected = getTestCustomer();
        final UserSubscription<NoPaymentDetails> subscription = UserSubscriptionUtils.getNoPaymentSubscription();
        doReturn(expected).when(manager).subscribe(principal.getDetails(), subscription);

        // When
        final Customer result = resource.subscribeNone(principal, subscription);

        // Then
        verify(manager).subscribe(principal.getDetails(), subscription);
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void verifyCustomer_whenSucceeds_shouldReturnOk() throws Exception {
        // Given
        final Customer expected = getTestCustomer();
        when(manager.verify(principal.getName())).thenReturn(expected);

        // When
        final Customer result = resource.verify(principal);

        // Then
        verify(manager).verify(principal.getName());
        assertThat(result).isEqualTo(expected);
    }
}