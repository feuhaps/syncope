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
package org.apache.syncope.common.lib.to;

import org.apache.syncope.common.lib.AbstractBaseBean;

public class ProvisionTO extends AbstractBaseBean {

    private static final long serialVersionUID = 8298910216218007927L;

    private long key;

    private String anyType;

    private String objectClass;

    private String syncToken;

    private MappingTO mapping;

    public long getKey() {
        return key;
    }

    public void setKey(final long key) {
        this.key = key;
    }

    public String getAnyType() {
        return anyType;
    }

    public void setAnyType(final String anyType) {
        this.anyType = anyType;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(final String objectClass) {
        this.objectClass = objectClass;
    }

    public String getSyncToken() {
        return syncToken;
    }

    public void setSyncToken(final String syncToken) {
        this.syncToken = syncToken;
    }

    public MappingTO getMapping() {
        return mapping;
    }

    public void setMapping(final MappingTO mapping) {
        this.mapping = mapping;
    }

}
