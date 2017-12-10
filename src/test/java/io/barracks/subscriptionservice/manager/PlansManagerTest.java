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

import com.cheddargetter.model.Plans;
import io.barracks.subscriptionservice.client.CheddarGetterClient;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlansManagerTest {
    @Mock
    CheddarGetterClient client;

    @InjectMocks
    PlansManager manager;

    @Before
    public void setUp() {

    }

    @Test
    public void getPlans_whenSucceeds_shouldReturnPlans() {
        // Given
        Plans plans = new Plans();
        when(client.getPlans()).thenReturn(plans);

        // When
        Plans results = manager.getPlans();

        // Then
        verify(client).getPlans();
        assertEquals(plans, results);
    }

    @Test
    public void getPlans_whenClientThrowsException_shouldThrowException() {
        when(client.getPlans()).thenThrow(new CheddarGetterClientException(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)));
        assertThatExceptionOfType(CheddarGetterClientException.class).isThrownBy(() -> manager.getPlans());
        verify(client).getPlans();
    }
}
