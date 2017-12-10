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

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorMarshallingTest {
    @Test
    public void parseError_shouldReturnCorrectValues() throws Exception {
        // Given
        JAXBContext context = JAXBContext.newInstance(Error.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        ClassPathResource resource = new ClassPathResource(getClass().getSimpleName() + "-error.xml", getClass());

        // When
        Error error = (Error) unmarshaller.unmarshal(resource.getInputStream());

        // Then
        assertThat(error.getId()).isEqualTo("33647467");
        assertThat(error.getCode()).isEqualTo("422");
        assertThat(error.getAuxCode()).isEqualTo("6006");
        assertThat(error.getValue()).isEqualTo("The transaction was declined - contact support");
    }
}
