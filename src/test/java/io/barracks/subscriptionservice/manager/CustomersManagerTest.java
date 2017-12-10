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

package io.barracks.subscriptionservice.manager;

import com.cheddargetter.model.Customer;
import com.cheddargetter.model.Customers;
import io.barracks.commons.exceptions.BarracksServiceClientException;
import io.barracks.commons.test.AnyBarracksClientException;
import io.barracks.subscriptionservice.client.AuthorizationServiceClient;
import io.barracks.subscriptionservice.client.CheddarGetterClient;
import io.barracks.subscriptionservice.client.DeviceServiceClient;
import io.barracks.subscriptionservice.client.DeviceServiceV2Client;
import io.barracks.subscriptionservice.client.exception.AuthorizationServiceClientException;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;
import io.barracks.subscriptionservice.model.DataSet;
import io.barracks.subscriptionservice.rest.entity.PaymentMethod;
import io.barracks.subscriptionservice.rest.entity.User;
import io.barracks.subscriptionservice.rest.entity.UserSubscription;
import io.barracks.subscriptionservice.utils.DataSetUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static io.barracks.subscriptionservice.utils.TestEntities.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomersManagerTest {
    @Mock
    private CheddarGetterClient cheddarGetterClient;

    @Mock
    private AuthorizationServiceClient authorizationServiceClient;

    @Mock
    private DeviceServiceClient deviceServiceClient;

    @Mock
    private DeviceServiceV2Client deviceServiceV2Client;

    @InjectMocks
    private CustomersManager manager;

    @Test
    public void subscribe_whenUserDoesNotExists_shouldCreate() throws Exception {
        // Given
        final String status = "payment_pending";
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        final User user = getTestUser();
        final Customer customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalpending").getCustomers().get(0);
        doThrow(AnyBarracksClientException.from(HttpStatus.NOT_FOUND)).when(cheddarGetterClient).getCustomerData(user.getId());
        when(cheddarGetterClient.createCustomer(user, subscription)).thenReturn(customer);

        // When
        Customer response = manager.subscribe(user, subscription);

        // Then
        verify(cheddarGetterClient).createCustomer(user, subscription);
        verify(authorizationServiceClient).setStatus(user.getId(), status);
        assertEquals(customer, response);
    }

    @Test
    public void subscribe_whenUserExists_shouldEdit() throws Exception {
        // Given
        final String status = "payment_pending";
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        final User user = getTestUser();
        final Customer customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalpending").getCustomers().get(0);
        doReturn(customer).when(cheddarGetterClient).getCustomerData(user.getId());
        when(cheddarGetterClient.editCustomer(user, subscription)).thenReturn(customer);

        // When
        Customer response = manager.subscribe(user, subscription);

        // Then
        verify(cheddarGetterClient).editCustomer(user, subscription);
        verify(authorizationServiceClient).setStatus(user.getId(), status);
        assertEquals(customer, response);

    }

    @Test
    public void subscribe_whenGettingCustomerDataFails_shouldThrowException() {
        // Given
        final UserSubscription subscription = getTestUserSubscription(PaymentMethod.PAYPAL);
        final User user = getTestUser();
        doThrow(AnyBarracksClientException.from(HttpStatus.INTERNAL_SERVER_ERROR)).when(cheddarGetterClient).getCustomerData(user.getId());

        // Then When
        assertThatExceptionOfType(BarracksServiceClientException.class)
                .isThrownBy(() -> manager.subscribe(user, subscription));
        verify(cheddarGetterClient).getCustomerData(user.getId());
    }

    @Test
    public void verify_whenPaymentApproved_shouldCallClient() throws IOException, JAXBException {
        // Given
        final String userId = UUID.randomUUID().toString();
        final String activeStatus = "active";
        final Customers customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalapproved");
        when(cheddarGetterClient.getCustomerData(userId)).thenReturn(customer.getCustomers().get(0));

        // When
        Customer result = manager.verify(userId);

        // Then
        assertNotNull(result);
        verify(authorizationServiceClient).setStatus(userId, activeStatus);
        verify(cheddarGetterClient).getCustomerData(userId);
    }

    @Test
    public void verify_whenPaymentPending_shouldCallClient() throws IOException, JAXBException {
        // Given
        final String userId = UUID.randomUUID().toString();
        final String pendingStatus = "payment_pending";
        final Customers customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalpending");
        when(cheddarGetterClient.getCustomerData(userId)).thenReturn(customer.getCustomers().get(0));

        // When
        Customer result = manager.verify(userId);

        // Then
        assertNotNull(result);
        verify(authorizationServiceClient).setStatus(userId, pendingStatus);
        verify(cheddarGetterClient).getCustomerData(userId);
    }

    @Test
    public void verify_whenPaymentCanceled_shouldCallClient() throws IOException, JAXBException {
        // Given
        final String userId = UUID.randomUUID().toString();
        final String pendingStatus = "payment_pending";
        final Customers customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalcanceled");
        when(cheddarGetterClient.getCustomerData(userId)).thenReturn(customer.getCustomers().get(0));

        // When
        Customer result = manager.verify(userId);

        // Then
        assertNotNull(result);
        verify(authorizationServiceClient).setStatus(userId, pendingStatus);
        verify(cheddarGetterClient).getCustomerData(userId);
    }

    @Test
    public void verify_whenCheddarClientFails_shouldThrowExecption() throws IOException, JAXBException {
        // Given
        final String userId = UUID.randomUUID().toString();
        doThrow(CheddarGetterClientException.class).when(cheddarGetterClient).getCustomerData(userId);

        // Then When
        assertThatExceptionOfType(CheddarGetterClientException.class).isThrownBy(() -> manager.verify(userId));
        verify(cheddarGetterClient).getCustomerData(userId);
    }

    @Test
    public void verify_whenAuthorizationClientFails_shouldThrowExecption() throws IOException, JAXBException {
        // Given
        final String userId = UUID.randomUUID().toString();
        final String activeStatus = "active";
        final Customers customer = getTestCustomers(getClass(), "getCustomer", "paying", "paypalapproved");
        when(cheddarGetterClient.getCustomerData(userId)).thenReturn(customer.getCustomers().get(0));
        doThrow(AuthorizationServiceClientException.class).when(authorizationServiceClient).setStatus(userId, activeStatus);

        // Then When
        assertThatExceptionOfType(AuthorizationServiceClientException.class).isThrownBy(() -> manager.verify(userId));
        verify(cheddarGetterClient).getCustomerData(userId);
        verify(authorizationServiceClient).setStatus(userId, activeStatus);
    }

    @Test
    public void updateTrackedItems_withV1Stats_andCallClient() {
        // Given
        final DataSet dataSet = DataSetUtils.getDataSet();
        doReturn(dataSet).when(deviceServiceClient).getDeviceCountPerUser();
        doReturn(DataSet.builder().build()).when(deviceServiceV2Client).getDeviceCountPerUser();

        // When
        manager.updateTrackedItems();

        // Then
        verify(deviceServiceClient).getDeviceCountPerUser();
        verify(deviceServiceV2Client).getDeviceCountPerUser();
        dataSet.getValues().forEach((userId, deviceCount) -> verify(cheddarGetterClient).setTrackedItem(userId, "DEVICES", deviceCount.doubleValue()));
    }

    @Test
    public void updateTrackedItems_withV2Stats_andCallClient() {
        // Given
        final DataSet dataSet = DataSetUtils.getDataSet();
        doReturn(DataSet.builder().build()).when(deviceServiceClient).getDeviceCountPerUser();
        doReturn(dataSet).when(deviceServiceV2Client).getDeviceCountPerUser();

        // When
        manager.updateTrackedItems();

        // Then
        verify(deviceServiceClient).getDeviceCountPerUser();
        verify(deviceServiceV2Client).getDeviceCountPerUser();
        dataSet.getValues().forEach((userId, deviceCount) -> verify(cheddarGetterClient).setTrackedItem(userId, "DEVICES", deviceCount.doubleValue()));
    }

    @Test
    public void updateTrackedItems_withV1V2Stats_andCallClient() {
        // Given
        final DataSet dataSetV1 = DataSet.builder()
                .value("a", BigDecimal.ONE)
                .value("b", BigDecimal.ONE)
                .build();
        final DataSet dataSetV2 = DataSet.builder()
                .value("b", BigDecimal.ONE)
                .value("c", BigDecimal.ONE)
                .build();
        final DataSet expected = DataSet.builder()
                .value("a", BigDecimal.ONE)
                .value("b", BigDecimal.valueOf(2))
                .value("c", BigDecimal.ONE)
                .build();
        doReturn(dataSetV1).when(deviceServiceClient).getDeviceCountPerUser();
        doReturn(dataSetV2).when(deviceServiceV2Client).getDeviceCountPerUser();

        // When
        manager.updateTrackedItems();

        // Then
        verify(deviceServiceClient).getDeviceCountPerUser();
        expected.getValues().forEach((userId, deviceCount) -> verify(cheddarGetterClient).setTrackedItem(userId, "DEVICES", deviceCount.doubleValue()));
    }

    @Test
    public void updateTrackedItems_shouldIgnoreException_andCallClient() {
        // Given
        final DataSet dataSetV1 = DataSetUtils.getDataSet();
        final DataSet dataSetV2 = DataSetUtils.getDataSet();
        doReturn(dataSetV1).when(deviceServiceClient).getDeviceCountPerUser();
        doReturn(dataSetV2).when(deviceServiceV2Client).getDeviceCountPerUser();
        doThrow(CheddarGetterClientException.class).when(cheddarGetterClient).setTrackedItem(anyString(), anyString(), anyDouble());

        // When
        manager.updateTrackedItems();

        // Then
        verify(deviceServiceClient).getDeviceCountPerUser();
        verify(deviceServiceV2Client).getDeviceCountPerUser();
        dataSetV2.getValues().forEach((userId, deviceCount) -> verify(cheddarGetterClient).setTrackedItem(userId, "DEVICES", deviceCount.doubleValue()));
    }
}
