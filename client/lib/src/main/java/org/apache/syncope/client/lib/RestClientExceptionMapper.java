/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.lib;

import java.security.AccessControlException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.ws.WebServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.apache.syncope.common.lib.SyncopeClientCompositeException;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.types.ClientExceptionType;
import org.apache.syncope.common.rest.api.RESTHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RestClientExceptionMapper implements ExceptionMapper<Exception>, ResponseExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(RestClientExceptionMapper.class);

    @Override
    public Response toResponse(final Exception exception) {
        throw new UnsupportedOperationException(
                "Call of toResponse() method is not expected in RestClientExceptionnMapper");
    }

    @Override
    public Exception fromResponse(final Response response) {
        final int statusCode = response.getStatus();
        Exception ex;

        SyncopeClientCompositeException scce = checkSyncopeClientCompositeException(response);
        if (scce != null) {
            // 1. Check for client (possibly composite) exception in HTTP header
            if (scce.getExceptions().size() == 1) {
                ex = scce.getExceptions().iterator().next();
            } else {
                ex = scce;
            }
        } else if (statusCode == Response.Status.UNAUTHORIZED.getStatusCode()) {
            // 2. Map SC_UNAUTHORIZED
            ex = new AccessControlException("Remote unauthorized exception");
        } else if (statusCode == Response.Status.BAD_REQUEST.getStatusCode()) {
            // 3. Map SC_BAD_REQUEST
            ex = new BadRequestException();
        } else {
            // 4. All other codes are mapped to runtime exception with HTTP code information
            ex = new WebServiceException(String.format("Remote exception with status code: %s",
                    Response.Status.fromStatusCode(statusCode).name()));
        }
        LOG.error("Exception thrown by REST methods: " + ex.getMessage(), ex);
        return ex;
    }

    private SyncopeClientCompositeException checkSyncopeClientCompositeException(final Response response) {
        List<String> exTypesInHeaders = response.getStringHeaders().get(RESTHeaders.ERROR_CODE);
        if (exTypesInHeaders == null) {
            LOG.debug("No " + RESTHeaders.ERROR_CODE + " provided");
            return null;
        }
        List<String> exInfos = response.getStringHeaders().get(RESTHeaders.ERROR_INFO);

        final SyncopeClientCompositeException compException = SyncopeClientException.buildComposite();

        Set<String> handledExceptions = new HashSet<>();
        for (String exTypeAsString : exTypesInHeaders) {
            ClientExceptionType exceptionType = null;
            try {
                exceptionType = ClientExceptionType.fromHeaderValue(exTypeAsString);
            } catch (IllegalArgumentException e) {
                LOG.error("Unexpected value of " + RESTHeaders.ERROR_CODE + ": " + exTypeAsString, e);
            }
            if (exceptionType != null) {
                handledExceptions.add(exTypeAsString);

                final SyncopeClientException clientException = SyncopeClientException.build(exceptionType);

                if (exInfos != null && !exInfos.isEmpty()) {
                    for (String element : exInfos) {
                        if (element.startsWith(exceptionType.getHeaderValue())) {
                            clientException.getElements().add(StringUtils.substringAfter(element, ":"));
                        }
                    }
                }
                compException.addException(clientException);
            }
        }

        exTypesInHeaders.removeAll(handledExceptions);
        if (!exTypesInHeaders.isEmpty()) {
            LOG.error("Unmanaged exceptions: " + exTypesInHeaders);
        }

        if (compException.hasExceptions()) {
            return compException;
        }

        return null;
    }
}
