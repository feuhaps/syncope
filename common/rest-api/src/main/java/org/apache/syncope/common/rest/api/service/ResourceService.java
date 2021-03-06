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
package org.apache.syncope.common.rest.api.service;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.model.wadl.Description;
import org.apache.cxf.jaxrs.model.wadl.Descriptions;
import org.apache.cxf.jaxrs.model.wadl.DocTarget;
import org.apache.syncope.common.lib.to.BulkAction;
import org.apache.syncope.common.lib.to.BulkActionResult;
import org.apache.syncope.common.lib.to.ConnObjectTO;
import org.apache.syncope.common.lib.to.ResourceTO;
import org.apache.syncope.common.lib.types.ResourceDeassociationActionType;
import org.apache.syncope.common.lib.wrap.AnyKey;
import org.apache.syncope.common.lib.wrap.BooleanWrap;

/**
 * REST operations for external resources.
 */
@Path("resources")
public interface ResourceService extends JAXRSService {

    /**
     * Returns connector object from the external resource, for the given type and key.
     *
     * @param key Name of resource to read connector object from
     * @param anyTypeKey any object type
     * @param anyKey any object key
     * @return connector object from the external resource, for the given type and key
     */
    @GET
    @Path("{key}/{anyTypeKey}/{anyKey}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    ConnObjectTO readConnObject(@NotNull @PathParam("key") String key,
            @NotNull @PathParam("anyTypeKey") String anyTypeKey, @NotNull @PathParam("anyKey") Long anyKey);

    /**
     * Returns the resource with matching name.
     *
     * @param key Name of resource to be read
     * @return resource with matching name
     */
    @GET
    @Path("{key}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    ResourceTO read(@NotNull @PathParam("key") String key);

    /**
     * Returns a list of all resources.
     *
     * @return list of all resources
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<ResourceTO> list();

    /**
     * Creates a new resource.
     *
     * @param resourceTO Resource to be created
     * @return <tt>Response</tt> object featuring <tt>Location</tt> header of created resource
     */
    @Descriptions({
        @Description(target = DocTarget.RESPONSE,
                value = "Featuring <tt>Location</tt> header of created resource")
    })
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    Response create(@NotNull ResourceTO resourceTO);

    /**
     * Updates the resource matching the given name.
     *
     * @param resourceTO resource to be stored
     */
    @PUT
    @Path("{key}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    void update(@NotNull ResourceTO resourceTO);

    /**
     * Deletes the resource matching the given name.
     *
     * @param key name of resource to be deleted
     */
    @DELETE
    @Path("{key}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    void delete(@NotNull @PathParam("key") String key);

    /**
     * Checks whether the connection to resource could be established.
     *
     * @param resourceTO resource to be checked
     * @return true if connection to resource could be established
     */
    @POST
    @Path("check")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    BooleanWrap check(@NotNull ResourceTO resourceTO);

    /**
     * De-associate any objects from the given resource.
     *
     * @param key name of resource
     * @param anyTypeKey any object kind
     * @param type resource de-association action type
     * @param keys any object keys against which the bulk action will be performed
     * @return <tt>Response</tt> object featuring {@link BulkActionResult} as <tt>Entity</tt>
     */
    @Descriptions({
        @Description(target = DocTarget.RESPONSE,
                value = "Featuring <tt>BulkActionResult</tt> as <tt>Entity</tt>")
    })
    @POST
    @Path("{key}/bulkDeassociation/{anyTypeKey}/{type}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    BulkActionResult bulkDeassociation(@NotNull @PathParam("key") String key,
            @NotNull @PathParam("anyTypeKey") String anyTypeKey,
            @NotNull @PathParam("type") ResourceDeassociationActionType type, @NotNull List<AnyKey> keys);

    /**
     * Executes the provided bulk action.
     *
     * @param bulkAction list of resource names against which the bulk action will be performed
     * @return Bulk action result
     */
    @POST
    @Path("bulk")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    BulkActionResult bulk(@NotNull BulkAction bulkAction);
}
