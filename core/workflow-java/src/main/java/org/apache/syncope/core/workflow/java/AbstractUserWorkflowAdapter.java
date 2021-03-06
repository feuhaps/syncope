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
package org.apache.syncope.core.workflow.java;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.common.lib.mod.UserMod;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.WorkflowResult;
import org.apache.syncope.core.provisioning.api.data.UserDataBinder;
import org.apache.syncope.core.workflow.api.UserWorkflowAdapter;
import org.identityconnectors.common.Base64;
import org.identityconnectors.common.security.EncryptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = { Throwable.class })
public abstract class AbstractUserWorkflowAdapter implements UserWorkflowAdapter {

    @Autowired
    protected UserDataBinder dataBinder;

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected EntityFactory entityFactory;

    public static String encrypt(final String clear) {
        byte[] encryptedBytes = EncryptorFactory.getInstance().getDefaultEncryptor().encrypt(clear.getBytes());
        return Base64.encode(encryptedBytes);
    }

    public static String decrypt(final String crypted) {
        byte[] decryptedBytes = EncryptorFactory.getInstance().getDefaultEncryptor().decrypt(Base64.decode(crypted));
        return new String(decryptedBytes);
    }

    @Override
    public String getPrefix() {
        return null;
    }

    protected abstract WorkflowResult<Long> doActivate(User user, String token);

    @Override
    public WorkflowResult<Long> activate(final Long userKey, final String token) {
        return doActivate(userDAO.authFind(userKey), token);
    }

    protected abstract WorkflowResult<Pair<UserMod, Boolean>> doUpdate(User user, UserMod userMod);

    @Override
    public WorkflowResult<Pair<UserMod, Boolean>> update(final UserMod userMod) {
        return doUpdate(userDAO.authFind(userMod.getKey()), userMod);
    }

    protected abstract WorkflowResult<Long> doSuspend(User user);

    @Override
    public WorkflowResult<Long> suspend(final Long userKey) {
        return suspend(userDAO.authFind(userKey));
    }

    @Override
    public WorkflowResult<Long> suspend(final User user) {
        // set suspended flag
        user.setSuspended(Boolean.TRUE);

        return doSuspend(user);
    }

    protected abstract WorkflowResult<Long> doReactivate(User user);

    @Override
    public WorkflowResult<Long> reactivate(final Long userKey) {
        final User user = userDAO.authFind(userKey);

        // reset failed logins
        user.setFailedLogins(0);

        // reset suspended flag
        user.setSuspended(Boolean.FALSE);

        return doReactivate(user);
    }

    protected abstract void doRequestPasswordReset(User user);

    @Override
    public void requestPasswordReset(final Long userKey) {
        doRequestPasswordReset(userDAO.authFind(userKey));
    }

    protected abstract void doConfirmPasswordReset(User user, String token, String password);

    @Override
    public void confirmPasswordReset(final Long userKey, final String token, final String password) {
        doConfirmPasswordReset(userDAO.authFind(userKey), token, password);
    }

    protected abstract void doDelete(User user);

    @Override
    public void delete(final Long userKey) {
        doDelete(userDAO.authFind(userKey));
    }
}
