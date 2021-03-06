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

package io.barracks.subscriptionservice.client.exception;

import com.cheddargetter.model.Error;
import io.barracks.commons.exceptions.BarracksServiceClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Optional;

@Slf4j
public class CheddarGetterClientException extends BarracksServiceClientException {
    private final Error error;

    public CheddarGetterClientException(HttpStatusCodeException cause) {
        super(cause);
        Error error = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Error.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            error = (Error) unmarshaller.unmarshal(new StringReader(cause.getResponseBodyAsString()));
        } catch (JAXBException e) {
            log.warn("Failed to parse " + cause.getResponseBodyAsString());
        }
        this.error = error;
    }

    public Optional<Error> getError() {
        return Optional.ofNullable(error);
    }
}
