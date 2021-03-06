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
package org.apache.syncope.core.persistence.jpa.entity.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.api.entity.DerAttr;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.VirAttr;
import org.apache.syncope.core.persistence.api.entity.VirSchema;
import org.apache.syncope.core.persistence.api.entity.conf.CPlainAttr;
import org.apache.syncope.core.persistence.api.entity.conf.Conf;
import org.apache.syncope.core.persistence.api.entity.resource.ExternalResource;
import org.apache.syncope.core.persistence.jpa.entity.AbstractAnnotatedEntity;

@Entity
@Table(name = JPAConf.TABLE)
@Cacheable
public class JPAConf extends AbstractAnnotatedEntity<Long> implements Conf {

    private static final long serialVersionUID = 7671699609879382195L;

    public static final String TABLE = "SyncopeConf";

    @Id
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    @Valid
    private List<JPACPlainAttr> plainAttrs = new ArrayList<>();

    @Override
    public Long getKey() {
        return id;
    }

    @Override
    public void setKey(final Long key) {
        this.id = key;
    }

    @Override
    public boolean add(final CPlainAttr attr) {
        checkType(attr, JPACPlainAttr.class);
        return plainAttrs.add((JPACPlainAttr) attr);
    }

    @Override
    public boolean remove(final CPlainAttr attr) {
        checkType(attr, JPACPlainAttr.class);
        return plainAttrs.remove((JPACPlainAttr) attr);
    }

    @Override
    public CPlainAttr getPlainAttr(final String plainSchemaName) {
        return CollectionUtils.find(plainAttrs, new Predicate<CPlainAttr>() {

            @Override
            public boolean evaluate(final CPlainAttr plainAttr) {
                return plainAttr != null && plainAttr.getSchema() != null
                        && plainSchemaName.equals(plainAttr.getSchema().getKey());
            }
        });
    }

    @Override
    public List<? extends CPlainAttr> getPlainAttrs() {
        return plainAttrs;
    }

    @Override
    public boolean add(final DerAttr<?> attr) {
        return false;
    }

    @Override
    public boolean remove(final DerAttr<?> derAttr) {
        return false;
    }

    @Override
    public DerAttr<?> getDerAttr(final String derSchemaName) {
        return null;
    }

    @Override
    public List<? extends DerAttr<?>> getDerAttrs() {
        return Collections.emptyList();
    }

    @Override
    public boolean add(final VirAttr<?> attr) {
        return false;
    }

    @Override
    public boolean remove(final VirAttr<?> virAttr) {
        return false;
    }

    @Override
    public VirAttr<?> getVirAttr(final String virSchemaName) {
        return null;
    }

    @Override
    public List<? extends VirAttr<?>> getVirAttrs() {
        return Collections.emptyList();
    }

    @Override
    public boolean add(final ExternalResource resource) {
        return false;
    }

    @Override
    public boolean remove(final ExternalResource resource) {
        return false;
    }

    @Override
    public List<String> getResourceNames() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends ExternalResource> getResources() {
        return Collections.emptyList();
    }

    @Override
    public boolean add(final AnyTypeClass auxClass) {
        return false;
    }

    @Override
    public boolean remove(final AnyTypeClass auxClass) {
        return false;
    }

    @Override
    public List<? extends AnyTypeClass> getAuxClasses() {
        return Collections.emptyList();
    }

    @Override
    public String getWorkflowId() {
        return null;
    }

    @Override
    public void setWorkflowId(final String workflowId) {
        // nothing to do
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void setStatus(final String status) {
        // nothing to do
    }

    @Override
    public Realm getRealm() {
        return null;
    }

    @Override
    public void setRealm(final Realm realm) {
        // nothing to do
    }

    @Override
    public AnyType getType() {
        return null;
    }

    @Override
    public void setType(final AnyType type) {
        // nothing to do
    }

    @Override
    public Set<PlainSchema> getAllowedPlainSchemas() {
        return Collections.emptySet();
    }

    @Override
    public Set<DerSchema> getAllowedDerSchemas() {
        return Collections.emptySet();
    }

    @Override
    public Set<VirSchema> getAllowedVirSchemas() {
        return Collections.emptySet();
    }
}
