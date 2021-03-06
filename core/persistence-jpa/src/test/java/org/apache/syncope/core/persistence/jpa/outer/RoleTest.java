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
package org.apache.syncope.core.persistence.jpa.outer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.Entitlement;
import org.apache.syncope.core.persistence.api.dao.AnyTypeClassDAO;
import org.apache.syncope.core.persistence.api.dao.PlainSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.RealmDAO;
import org.apache.syncope.core.persistence.api.dao.RoleDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.user.DynRoleMembership;
import org.apache.syncope.core.persistence.api.entity.Role;
import org.apache.syncope.core.persistence.api.entity.user.UPlainAttr;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.jpa.AbstractTest;
import org.apache.syncope.core.persistence.jpa.entity.user.JPADynRoleMembership;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoleTest extends AbstractTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private RealmDAO realmDAO;

    @Autowired
    private PlainSchemaDAO plainSchemaDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AnyTypeClassDAO anyTypeClassDAO;

    /**
     * Static copy of {@link org.apache.syncope.core.persistence.jpa.dao.JPAUserDAO} method with same signature:
     * required for avoiding creating new transaction - good for general use case but bad for the way how
     * this test class is architected.
     */
    private Collection<Role> findDynRoleMemberships(final User user) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT e.role FROM " + JPADynRoleMembership.class.getSimpleName()
                + " e WHERE :user MEMBER OF e.users", Role.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    @Test
    public void dynMembership() {
        // 0. create user matching the condition below
        User user = entityFactory.newEntity(User.class);
        user.setUsername("username");
        user.setRealm(realmDAO.find("/even/two"));
        user.add(anyTypeClassDAO.find("other"));

        UPlainAttr attr = entityFactory.newEntity(UPlainAttr.class);
        attr.setOwner(user);
        attr.setSchema(plainSchemaDAO.find("cool"));
        attr.add("true", anyUtilsFactory.getInstance(AnyTypeKind.USER));
        user.add(attr);

        user = userDAO.save(user);
        Long newUserKey = user.getKey();
        assertNotNull(newUserKey);

        // 1. create role with dynamic membership
        Role role = entityFactory.newEntity(Role.class);
        role.setName("new");
        role.addRealm(realmDAO.getRoot());
        role.addRealm(realmDAO.find("/even/two"));
        role.getEntitlements().add(Entitlement.LOG_LIST);
        role.getEntitlements().add(Entitlement.LOG_SET_LEVEL);

        DynRoleMembership dynMembership = entityFactory.newEntity(DynRoleMembership.class);
        dynMembership.setFIQLCond("cool==true");
        dynMembership.setRole(role);

        role.setDynMembership(dynMembership);

        Role actual = roleDAO.save(role);
        assertNotNull(actual);

        roleDAO.flush();

        // 2. verify that dynamic membership is there
        actual = roleDAO.find(actual.getKey());
        assertNotNull(actual);
        assertNotNull(actual.getDynMembership());
        assertNotNull(actual.getDynMembership().getKey());
        assertEquals(actual, actual.getDynMembership().getRole());

        // 3. verify that expected users have the created role dynamically assigned
        assertEquals(2, actual.getDynMembership().getMembers().size());
        assertEquals(new HashSet<>(Arrays.asList(4L, newUserKey)),
                CollectionUtils.collect(actual.getDynMembership().getMembers(), new Transformer<User, Long>() {

                    @Override
                    public Long transform(final User input) {
                        return input.getKey();
                    }
                }, new HashSet<Long>()));

        user = userDAO.find(4L);
        assertNotNull(user);
        Collection<Role> dynRoleMemberships = findDynRoleMemberships(user);
        assertEquals(1, dynRoleMemberships.size());
        assertTrue(dynRoleMemberships.contains(actual.getDynMembership().getRole()));

        // 4. delete the new user and verify that dynamic membership was updated
        userDAO.delete(newUserKey);

        userDAO.flush();

        actual = roleDAO.find(actual.getKey());
        assertEquals(1, actual.getDynMembership().getMembers().size());
        assertEquals(4L, actual.getDynMembership().getMembers().get(0).getKey(), 0);

        // 5. delete role and verify that dynamic membership was also removed
        Long dynMembershipKey = actual.getDynMembership().getKey();

        roleDAO.delete(actual);

        roleDAO.flush();

        assertNull(entityManager.find(JPADynRoleMembership.class, dynMembershipKey));

        dynRoleMemberships = findDynRoleMemberships(user);
        assertTrue(dynRoleMemberships.isEmpty());
    }
}
