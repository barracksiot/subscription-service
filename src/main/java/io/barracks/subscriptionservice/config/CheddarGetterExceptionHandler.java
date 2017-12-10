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

package io.barracks.subscriptionservice.config;

import com.cheddargetter.model.Error;
import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import io.barracks.commons.configuration.BarracksServiceClientExceptionHandler;
import io.barracks.subscriptionservice.client.exception.CheddarGetterClientException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class CheddarGetterExceptionHandler extends BarracksServiceClientExceptionHandler<CheddarGetterClientException> {

    @Override
    public ErrorMessage createBody(CheddarGetterClientException ex, HttpServletRequest req) {
        ErrorMessage message = super.createBody(ex, req);
        message.setStatus(ex.getCause().getStatusCode());
        Optional<Error> errorOptional = ex.getError();
        if (errorOptional.isPresent()) {
            Error error = errorOptional.get();
            message.setTitle(error.getValue());
            message.setDetail(error.getAuxCode());
        } else {
            message.setTitle("An error occurred while processing your subscription.");
        }
        return message;
    }
}
