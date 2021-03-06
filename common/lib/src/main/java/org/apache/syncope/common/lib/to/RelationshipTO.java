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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.syncope.common.lib.AbstractBaseBean;

@XmlRootElement(name = "relationship")
@XmlType
public class RelationshipTO extends AbstractBaseBean {

    private static final long serialVersionUID = 360672942026613929L;

    private String relationshipType;

    private String leftType;

    private long leftKey;

    private String rightType;

    private long rightKey;

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(final String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getLeftType() {
        return leftType;
    }

    public void setLeftType(final String leftType) {
        this.leftType = leftType;
    }

    public long getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(final long leftKey) {
        this.leftKey = leftKey;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(final String rightType) {
        this.rightType = rightType;
    }

    public long getRightKey() {
        return rightKey;
    }

    public void setRightKey(final long rightKey) {
        this.rightKey = rightKey;
    }

}
