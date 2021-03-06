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
package org.apache.syncope.client.console.rest;

import java.util.List;
import org.apache.syncope.client.console.SyncopeConsoleSession;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.common.lib.to.AbstractTaskTO;
import org.apache.syncope.common.lib.to.BulkAction;
import org.apache.syncope.common.lib.to.BulkActionResult;
import org.apache.syncope.common.lib.to.NotificationTaskTO;
import org.apache.syncope.common.lib.to.PropagationTaskTO;
import org.apache.syncope.common.lib.to.PushTaskTO;
import org.apache.syncope.common.lib.to.SchedTaskTO;
import org.apache.syncope.common.lib.to.SyncTaskTO;
import org.apache.syncope.common.lib.types.TaskType;
import org.apache.syncope.common.rest.api.service.TaskService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.stereotype.Component;

/**
 * Console client for invoking Rest Tasks services.
 */
@Component
public class TaskRestClient extends BaseRestClient implements ExecutionRestClient {

    private static final long serialVersionUID = 6284485820911028843L;

    public List<String> getJobClasses() {
        return SyncopeConsoleSession.get().getSyncopeTO().getTaskJobs();
    }

    public List<String> getSyncActionsClasses() {
        return SyncopeConsoleSession.get().getSyncopeTO().getSyncActions();
    }

    public List<String> getPushActionsClasses() {
        return SyncopeConsoleSession.get().getSyncopeTO().getPushActions();
    }

    /**
     * Return the number of tasks.
     *
     * @param kind of task (propagation, sched, sync).
     * @return number of stored tasks.
     */
    public int count(final String kind) {
        return getService(TaskService.class).
                list(TaskType.fromString(kind), SyncopeClient.getListQueryBuilder().page(1).size(1).build()).
                getTotalCount();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractTaskTO> List<T> list(final Class<T> reference,
            final int page, final int size, final SortParam<String> sort) {

        return (List<T>) getService(TaskService.class).
                list(getTaskType(reference), SyncopeClient.getListQueryBuilder().page(page).size(size).
                        orderBy(toOrderBy(sort)).build()).
                getResult();
    }

    private TaskType getTaskType(final Class<?> reference) {
        TaskType result = null;
        if (PropagationTaskTO.class.equals(reference)) {
            result = TaskType.PROPAGATION;
        } else if (NotificationTaskTO.class.equals(reference)) {
            result = TaskType.NOTIFICATION;
        } else if (SchedTaskTO.class.equals(reference)) {
            result = TaskType.SCHEDULED;
        } else if (SyncTaskTO.class.equals(reference)) {
            result = TaskType.SYNCHRONIZATION;
        } else if (PushTaskTO.class.equals(reference)) {
            result = TaskType.PUSH;
        }
        return result;
    }

    public PropagationTaskTO readPropagationTask(final Long taskId) {
        return getService(TaskService.class).read(taskId);
    }

    public NotificationTaskTO readNotificationTask(final Long taskId) {
        return getService(TaskService.class).read(taskId);
    }

    public <T extends SchedTaskTO> T readSchedTask(final Class<T> reference, final Long taskId) {
        return getService(TaskService.class).read(taskId);
    }

    public void delete(final Long taskId, final Class<? extends AbstractTaskTO> taskToClass) {
        getService(TaskService.class).delete(taskId);
    }

    @Override
    public void startExecution(final long taskId) {
        startExecution(taskId, false);
    }

    public void startExecution(final long taskId, final boolean dryRun) {
        getService(TaskService.class).execute(taskId, dryRun);
    }

    @Override
    public void deleteExecution(final long taskExecId) {
        getService(TaskService.class).deleteExecution(taskExecId);
    }

    public void createSyncTask(final SyncTaskTO taskTO) {
        getService(TaskService.class).create(taskTO);
    }

    public void createSchedTask(final SchedTaskTO taskTO) {
        getService(TaskService.class).create(taskTO);
    }

    public void updateSchedTask(final SchedTaskTO taskTO) {
        getService(TaskService.class).update(taskTO);
    }

    public void updateSyncTask(final SyncTaskTO taskTO) {
        getService(TaskService.class).update(taskTO);
    }

    public BulkActionResult bulkAction(final BulkAction action) {
        return getService(TaskService.class).bulk(action);
    }
}
