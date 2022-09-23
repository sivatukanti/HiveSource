// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import java.util.Iterator;
import org.apache.hadoop.yarn.webapp.BadRequestException;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.server.webapp.dao.ContainersInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptsInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppInfo;
import javax.ws.rs.PathParam;
import java.util.Set;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Collections;
import org.apache.hadoop.yarn.server.webapp.dao.AppsInfo;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import javax.ws.rs.Path;
import com.google.inject.Singleton;
import org.apache.hadoop.yarn.server.webapp.WebServices;

@Singleton
@Path("/ws/v1/applicationhistory")
public class AHSWebServices extends WebServices
{
    @Inject
    public AHSWebServices(final ApplicationContext appContext) {
        super(appContext);
    }
    
    @GET
    @Produces({ "application/json", "application/xml" })
    public AppsInfo get(@Context final HttpServletRequest req, @Context final HttpServletResponse res) {
        return this.getApps(req, res, null, Collections.emptySet(), null, null, null, null, null, null, null, null, Collections.emptySet());
    }
    
    @GET
    @Path("/apps")
    @Produces({ "application/json", "application/xml" })
    @Override
    public AppsInfo getApps(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @QueryParam("state") final String stateQuery, @QueryParam("states") final Set<String> statesQuery, @QueryParam("finalStatus") final String finalStatusQuery, @QueryParam("user") final String userQuery, @QueryParam("queue") final String queueQuery, @QueryParam("limit") final String count, @QueryParam("startedTimeBegin") final String startedBegin, @QueryParam("startedTimeEnd") final String startedEnd, @QueryParam("finishedTimeBegin") final String finishBegin, @QueryParam("finishedTimeEnd") final String finishEnd, @QueryParam("applicationTypes") final Set<String> applicationTypes) {
        this.init(res);
        validateStates(stateQuery, statesQuery);
        return super.getApps(req, res, stateQuery, statesQuery, finalStatusQuery, userQuery, queueQuery, count, startedBegin, startedEnd, finishBegin, finishEnd, applicationTypes);
    }
    
    @GET
    @Path("/apps/{appid}")
    @Produces({ "application/json", "application/xml" })
    @Override
    public AppInfo getApp(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("appid") final String appId) {
        this.init(res);
        return super.getApp(req, res, appId);
    }
    
    @GET
    @Path("/apps/{appid}/appattempts")
    @Produces({ "application/json", "application/xml" })
    @Override
    public AppAttemptsInfo getAppAttempts(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("appid") final String appId) {
        this.init(res);
        return super.getAppAttempts(req, res, appId);
    }
    
    @GET
    @Path("/apps/{appid}/appattempts/{appattemptid}")
    @Produces({ "application/json", "application/xml" })
    @Override
    public AppAttemptInfo getAppAttempt(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("appid") final String appId, @PathParam("appattemptid") final String appAttemptId) {
        this.init(res);
        return super.getAppAttempt(req, res, appId, appAttemptId);
    }
    
    @GET
    @Path("/apps/{appid}/appattempts/{appattemptid}/containers")
    @Produces({ "application/json", "application/xml" })
    @Override
    public ContainersInfo getContainers(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("appid") final String appId, @PathParam("appattemptid") final String appAttemptId) {
        this.init(res);
        return super.getContainers(req, res, appId, appAttemptId);
    }
    
    @GET
    @Path("/apps/{appid}/appattempts/{appattemptid}/containers/{containerid}")
    @Produces({ "application/json", "application/xml" })
    @Override
    public ContainerInfo getContainer(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("appid") final String appId, @PathParam("appattemptid") final String appAttemptId, @PathParam("containerid") final String containerId) {
        this.init(res);
        return super.getContainer(req, res, appId, appAttemptId, containerId);
    }
    
    private static void validateStates(final String stateQuery, final Set<String> statesQuery) {
        if (stateQuery != null && !stateQuery.isEmpty()) {
            statesQuery.add(stateQuery);
        }
        final Set<String> appStates = WebServices.parseQueries(statesQuery, true);
        for (final String appState : appStates) {
            switch (YarnApplicationState.valueOf(appState.toUpperCase())) {
                case FINISHED:
                case FAILED:
                case KILLED: {
                    continue;
                }
                default: {
                    throw new BadRequestException("Invalid application-state " + appState + " specified. It should be a final state");
                }
            }
        }
    }
}
