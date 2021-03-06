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
package org.apache.syncope.core.persistence.jpa.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyUtils;
import org.apache.syncope.core.persistence.api.entity.PlainAttr;
import org.apache.syncope.core.persistence.api.entity.PlainAttrUniqueValue;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.jpa.validation.entity.PlainAttrCheck;

@MappedSuperclass
@PlainAttrCheck
public abstract class AbstractPlainAttr<O extends Any<?, ?, ?>>
        extends AbstractAttr<PlainSchema, O> implements PlainAttr<O> {

    private static final long serialVersionUID = -9115431608821806124L;

    @ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "schema_name")
    protected JPAPlainSchema schema;

    @Override
    public PlainSchema getSchema() {
        return schema;
    }

    @Override
    public void setSchema(final PlainSchema schema) {
        checkType(schema, JPAPlainSchema.class);
        this.schema = (JPAPlainSchema) schema;
        checkSchema(this.schema);
    }

    protected abstract boolean addForMultiValue(PlainAttrValue attrValue);

    @Override
    public void add(final String value, final PlainAttrValue attrValue) {
        attrValue.setAttr(this);
        getSchema().getValidator().validate(value, attrValue);

        if (getSchema().isUniqueConstraint()) {
            setUniqueValue((PlainAttrUniqueValue) attrValue);
        } else {
            if (!getSchema().isMultivalue()) {
                getValues().clear();
            }
            addForMultiValue(attrValue);
        }
    }

    @Override
    public void add(final String value, final AnyUtils anyUtils) {
        PlainAttrValue attrValue;
        if (getSchema().isUniqueConstraint()) {
            attrValue = anyUtils.newPlainAttrUniqueValue();
            ((PlainAttrUniqueValue) attrValue).setSchema(getSchema());
        } else {
            attrValue = anyUtils.newPlainAttrValue();
        }

        add(value, attrValue);
    }

    @Override
    public List<String> getValuesAsStrings() {
        List<String> result;
        if (getUniqueValue() == null) {
            result = CollectionUtils.collect(getValues(), new Transformer<PlainAttrValue, String>() {

                @Override
                public String transform(final PlainAttrValue input) {
                    return input.getValueAsString();
                }
            }, new ArrayList<String>());
        } else {
            result = Collections.singletonList(getUniqueValue().getValueAsString());
        }

        return Collections.unmodifiableList(result);
    }

}
