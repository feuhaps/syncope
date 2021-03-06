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
package org.apache.syncope.core.rest.cxf.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.syncope.common.lib.mod.StatusMod;
import org.apache.syncope.common.lib.mod.UserMod;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.rest.api.RESTHeaders;
import org.apache.syncope.common.rest.api.service.UserService;
import org.apache.syncope.core.logic.AbstractAnyLogic;
import org.apache.syncope.core.logic.UserLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractAnyService<UserTO, UserMod> implements UserService {

    @Autowired
    private UserLogic logic;

    @Override
    protected AbstractAnyLogic<UserTO, UserMod> getAnyLogic() {
        return logic;
    }

    @Override
    public Response getUsername(final Long key) {
        return Response.ok().header(HttpHeaders.ALLOW, OPTIONS_ALLOW).
                header(RESTHeaders.USERNAME, logic.getUsername(key)).
                build();
    }

    @Override
    public Response getUserKey(final String username) {
        return Response.ok().header(HttpHeaders.ALLOW, OPTIONS_ALLOW).
                header(RESTHeaders.USER_KEY, logic.getKey(username)).
                build();
    }

    @Override
    public Response create(final UserTO userTO, final boolean storePassword) {
        UserTO created = logic.create(userTO, storePassword);
        return createResponse(created.getKey(), created);
    }

    @Override
    public Response status(final Long key, final StatusMod statusMod) {
        UserTO user = logic.read(key);

        checkETag(user.getETagValue());

        statusMod.setKey(key);
        UserTO updated = logic.status(statusMod);
        return modificationResponse(updated);
    }
}
