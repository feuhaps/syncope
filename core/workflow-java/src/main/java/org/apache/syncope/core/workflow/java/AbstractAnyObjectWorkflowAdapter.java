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

import org.apache.syncope.common.lib.mod.AnyObjectMod;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.api.entity.anyobject.AnyObject;
import org.apache.syncope.core.provisioning.api.WorkflowResult;
import org.apache.syncope.core.provisioning.api.data.AnyObjectDataBinder;
import org.apache.syncope.core.workflow.api.AnyObjectWorkflowAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = { Throwable.class })
public abstract class AbstractAnyObjectWorkflowAdapter implements AnyObjectWorkflowAdapter {

    @Autowired
    protected AnyObjectDataBinder dataBinder;

    @Autowired
    protected AnyObjectDAO anyObjectDAO;

    @Autowired
    protected EntityFactory entityFactory;

    @Override
    public String getPrefix() {
        return null;
    }

    protected abstract WorkflowResult<Long> doUpdate(AnyObject anyObject, AnyObjectMod anyObjectMod);

    @Override
    public WorkflowResult<Long> update(final AnyObjectMod anyObjectMod) {
        return doUpdate(anyObjectDAO.authFind(anyObjectMod.getKey()), anyObjectMod);
    }

    protected abstract void doDelete(AnyObject anyObject);

    @Override
    public void delete(final Long anyObjectKey) {
        doDelete(anyObjectDAO.authFind(anyObjectKey));
    }
}
