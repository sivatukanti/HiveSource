// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp;

import javax.ws.rs.WebApplicationException;
import org.apache.hadoop.yarn.webapp.ForbiddenException;
import org.apache.hadoop.security.authorize.AuthorizationException;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import java.util.Arrays;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.HashSet;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.server.webapp.dao.ContainersInfo;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptsInfo;
import org.apache.hadoop.yarn.webapp.NotFoundException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Iterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.Collection;
import org.apache.hadoop.yarn.webapp.BadRequestException;
import org.apache.hadoop.yarn.server.webapp.dao.AppsInfo;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.yarn.server.api.ApplicationContext;

public class WebServices
{
    protected ApplicationContext appContext;
    
    public WebServices(final ApplicationContext appContext) {
        this.appContext = appContext;
    }
    
    public AppsInfo getApps(final HttpServletRequest req, final HttpServletResponse res, final String stateQuery, final Set<String> statesQuery, final String finalStatusQuery, final String userQuery, final String queueQuery, final String count, final String startedBegin, final String startedEnd, final String finishBegin, final String finishEnd, final Set<String> applicationTypes) {
        final UserGroupInformation callerUGI = getUser(req);
        long num = 0L;
        boolean checkCount = false;
        boolean checkStart = false;
        boolean checkEnd = false;
        boolean checkAppTypes = false;
        boolean checkAppStates = false;
        long countNum = 0L;
        long sBegin = 0L;
        long sEnd = Long.MAX_VALUE;
        long fBegin = 0L;
        long fEnd = Long.MAX_VALUE;
        if (count != null && !count.isEmpty()) {
            checkCount = true;
            countNum = Long.parseLong(count);
            if (countNum <= 0L) {
                throw new BadRequestException("limit value must be greater then 0");
            }
        }
        if (startedBegin != null && !startedBegin.isEmpty()) {
            checkStart = true;
            sBegin = Long.parseLong(startedBegin);
            if (sBegin < 0L) {
                throw new BadRequestException("startedTimeBegin must be greater than 0");
            }
        }
        if (startedEnd != null && !startedEnd.isEmpty()) {
            checkStart = true;
            sEnd = Long.parseLong(startedEnd);
            if (sEnd < 0L) {
                throw new BadRequestException("startedTimeEnd must be greater than 0");
            }
        }
        if (sBegin > sEnd) {
            throw new BadRequestException("startedTimeEnd must be greater than startTimeBegin");
        }
        if (finishBegin != null && !finishBegin.isEmpty()) {
            checkEnd = true;
            fBegin = Long.parseLong(finishBegin);
            if (fBegin < 0L) {
                throw new BadRequestException("finishTimeBegin must be greater than 0");
            }
        }
        if (finishEnd != null && !finishEnd.isEmpty()) {
            checkEnd = true;
            fEnd = Long.parseLong(finishEnd);
            if (fEnd < 0L) {
                throw new BadRequestException("finishTimeEnd must be greater than 0");
            }
        }
        if (fBegin > fEnd) {
            throw new BadRequestException("finishTimeEnd must be greater than finishTimeBegin");
        }
        final Set<String> appTypes = parseQueries(applicationTypes, false);
        if (!appTypes.isEmpty()) {
            checkAppTypes = true;
        }
        if (stateQuery != null && !stateQuery.isEmpty()) {
            statesQuery.add(stateQuery);
        }
        final Set<String> appStates = parseQueries(statesQuery, true);
        if (!appStates.isEmpty()) {
            checkAppStates = true;
        }
        final AppsInfo allApps = new AppsInfo();
        Collection<ApplicationReport> appReports = null;
        try {
            if (callerUGI == null) {
                appReports = this.appContext.getAllApplications().values();
            }
            else {
                appReports = callerUGI.doAs((PrivilegedExceptionAction<Collection<ApplicationReport>>)new PrivilegedExceptionAction<Collection<ApplicationReport>>() {
                    @Override
                    public Collection<ApplicationReport> run() throws Exception {
                        return WebServices.this.appContext.getAllApplications().values();
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        for (final ApplicationReport appReport : appReports) {
            if (checkCount && num == countNum) {
                break;
            }
            if (checkAppStates && !appStates.contains(appReport.getYarnApplicationState().toString().toLowerCase())) {
                continue;
            }
            if (finalStatusQuery != null && !finalStatusQuery.isEmpty()) {
                FinalApplicationStatus.valueOf(finalStatusQuery);
                if (!appReport.getFinalApplicationStatus().toString().equalsIgnoreCase(finalStatusQuery)) {
                    continue;
                }
            }
            if (userQuery != null && !userQuery.isEmpty() && !appReport.getUser().equals(userQuery)) {
                continue;
            }
            if (queueQuery != null && !queueQuery.isEmpty() && !appReport.getQueue().equals(queueQuery)) {
                continue;
            }
            if (checkAppTypes && !appTypes.contains(appReport.getApplicationType().trim().toLowerCase())) {
                continue;
            }
            if (checkStart) {
                if (appReport.getStartTime() < sBegin) {
                    continue;
                }
                if (appReport.getStartTime() > sEnd) {
                    continue;
                }
            }
            if (checkEnd) {
                if (appReport.getFinishTime() < fBegin) {
                    continue;
                }
                if (appReport.getFinishTime() > fEnd) {
                    continue;
                }
            }
            final AppInfo app = new AppInfo(appReport);
            allApps.add(app);
            ++num;
        }
        return allApps;
    }
    
    public AppInfo getApp(final HttpServletRequest req, final HttpServletResponse res, final String appId) {
        final UserGroupInformation callerUGI = getUser(req);
        final ApplicationId id = parseApplicationId(appId);
        ApplicationReport app = null;
        try {
            if (callerUGI == null) {
                app = this.appContext.getApplication(id);
            }
            else {
                app = callerUGI.doAs((PrivilegedExceptionAction<ApplicationReport>)new PrivilegedExceptionAction<ApplicationReport>() {
                    @Override
                    public ApplicationReport run() throws Exception {
                        return WebServices.this.appContext.getApplication(id);
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        if (app == null) {
            throw new NotFoundException("app with id: " + appId + " not found");
        }
        return new AppInfo(app);
    }
    
    public AppAttemptsInfo getAppAttempts(final HttpServletRequest req, final HttpServletResponse res, final String appId) {
        final UserGroupInformation callerUGI = getUser(req);
        final ApplicationId id = parseApplicationId(appId);
        Collection<ApplicationAttemptReport> appAttemptReports = null;
        try {
            if (callerUGI == null) {
                appAttemptReports = this.appContext.getApplicationAttempts(id).values();
            }
            else {
                appAttemptReports = callerUGI.doAs((PrivilegedExceptionAction<Collection<ApplicationAttemptReport>>)new PrivilegedExceptionAction<Collection<ApplicationAttemptReport>>() {
                    @Override
                    public Collection<ApplicationAttemptReport> run() throws Exception {
                        return WebServices.this.appContext.getApplicationAttempts(id).values();
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        final AppAttemptsInfo appAttemptsInfo = new AppAttemptsInfo();
        for (final ApplicationAttemptReport appAttemptReport : appAttemptReports) {
            final AppAttemptInfo appAttemptInfo = new AppAttemptInfo(appAttemptReport);
            appAttemptsInfo.add(appAttemptInfo);
        }
        return appAttemptsInfo;
    }
    
    public AppAttemptInfo getAppAttempt(final HttpServletRequest req, final HttpServletResponse res, final String appId, final String appAttemptId) {
        final UserGroupInformation callerUGI = getUser(req);
        final ApplicationId aid = parseApplicationId(appId);
        final ApplicationAttemptId aaid = parseApplicationAttemptId(appAttemptId);
        this.validateIds(aid, aaid, null);
        ApplicationAttemptReport appAttempt = null;
        try {
            if (callerUGI == null) {
                appAttempt = this.appContext.getApplicationAttempt(aaid);
            }
            else {
                appAttempt = callerUGI.doAs((PrivilegedExceptionAction<ApplicationAttemptReport>)new PrivilegedExceptionAction<ApplicationAttemptReport>() {
                    @Override
                    public ApplicationAttemptReport run() throws Exception {
                        return WebServices.this.appContext.getApplicationAttempt(aaid);
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        if (appAttempt == null) {
            throw new NotFoundException("app attempt with id: " + appAttemptId + " not found");
        }
        return new AppAttemptInfo(appAttempt);
    }
    
    public ContainersInfo getContainers(final HttpServletRequest req, final HttpServletResponse res, final String appId, final String appAttemptId) {
        final UserGroupInformation callerUGI = getUser(req);
        final ApplicationId aid = parseApplicationId(appId);
        final ApplicationAttemptId aaid = parseApplicationAttemptId(appAttemptId);
        this.validateIds(aid, aaid, null);
        Collection<ContainerReport> containerReports = null;
        try {
            if (callerUGI == null) {
                containerReports = this.appContext.getContainers(aaid).values();
            }
            else {
                containerReports = callerUGI.doAs((PrivilegedExceptionAction<Collection<ContainerReport>>)new PrivilegedExceptionAction<Collection<ContainerReport>>() {
                    @Override
                    public Collection<ContainerReport> run() throws Exception {
                        return WebServices.this.appContext.getContainers(aaid).values();
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        final ContainersInfo containersInfo = new ContainersInfo();
        for (final ContainerReport containerReport : containerReports) {
            final ContainerInfo containerInfo = new ContainerInfo(containerReport);
            containersInfo.add(containerInfo);
        }
        return containersInfo;
    }
    
    public ContainerInfo getContainer(final HttpServletRequest req, final HttpServletResponse res, final String appId, final String appAttemptId, final String containerId) {
        final UserGroupInformation callerUGI = getUser(req);
        final ApplicationId aid = parseApplicationId(appId);
        final ApplicationAttemptId aaid = parseApplicationAttemptId(appAttemptId);
        final ContainerId cid = parseContainerId(containerId);
        this.validateIds(aid, aaid, cid);
        ContainerReport container = null;
        try {
            if (callerUGI == null) {
                container = this.appContext.getContainer(cid);
            }
            else {
                container = callerUGI.doAs((PrivilegedExceptionAction<ContainerReport>)new PrivilegedExceptionAction<ContainerReport>() {
                    @Override
                    public ContainerReport run() throws Exception {
                        return WebServices.this.appContext.getContainer(cid);
                    }
                });
            }
        }
        catch (Exception e) {
            rewrapAndThrowException(e);
        }
        if (container == null) {
            throw new NotFoundException("container with id: " + containerId + " not found");
        }
        return new ContainerInfo(container);
    }
    
    protected void init(final HttpServletResponse response) {
        response.setContentType(null);
    }
    
    protected static Set<String> parseQueries(final Set<String> queries, final boolean isState) {
        final Set<String> params = new HashSet<String>();
        if (!queries.isEmpty()) {
            for (final String query : queries) {
                if (query != null && !query.trim().isEmpty()) {
                    final String[] arr$;
                    final String[] paramStrs = arr$ = query.split(",");
                    for (final String paramStr : arr$) {
                        if (paramStr != null && !paramStr.trim().isEmpty()) {
                            if (isState) {
                                try {
                                    YarnApplicationState.valueOf(paramStr.trim().toUpperCase());
                                }
                                catch (RuntimeException e) {
                                    final YarnApplicationState[] stateArray = YarnApplicationState.values();
                                    final String allAppStates = Arrays.toString(stateArray);
                                    throw new BadRequestException("Invalid application-state " + paramStr.trim() + " specified. It should be one of " + allAppStates);
                                }
                            }
                            params.add(paramStr.trim().toLowerCase());
                        }
                    }
                }
            }
        }
        return params;
    }
    
    protected static ApplicationId parseApplicationId(final String appId) {
        if (appId == null || appId.isEmpty()) {
            throw new NotFoundException("appId, " + appId + ", is empty or null");
        }
        final ApplicationId aid = ConverterUtils.toApplicationId(appId);
        if (aid == null) {
            throw new NotFoundException("appId is null");
        }
        return aid;
    }
    
    protected static ApplicationAttemptId parseApplicationAttemptId(final String appAttemptId) {
        if (appAttemptId == null || appAttemptId.isEmpty()) {
            throw new NotFoundException("appAttemptId, " + appAttemptId + ", is empty or null");
        }
        final ApplicationAttemptId aaid = ConverterUtils.toApplicationAttemptId(appAttemptId);
        if (aaid == null) {
            throw new NotFoundException("appAttemptId is null");
        }
        return aaid;
    }
    
    protected static ContainerId parseContainerId(final String containerId) {
        if (containerId == null || containerId.isEmpty()) {
            throw new NotFoundException("containerId, " + containerId + ", is empty or null");
        }
        final ContainerId cid = ConverterUtils.toContainerId(containerId);
        if (cid == null) {
            throw new NotFoundException("containerId is null");
        }
        return cid;
    }
    
    protected void validateIds(final ApplicationId appId, final ApplicationAttemptId appAttemptId, final ContainerId containerId) {
        if (!appAttemptId.getApplicationId().equals(appId)) {
            throw new NotFoundException("appId and appAttemptId don't match");
        }
        if (containerId != null && !containerId.getApplicationAttemptId().equals(appAttemptId)) {
            throw new NotFoundException("appAttemptId and containerId don't match");
        }
    }
    
    protected static UserGroupInformation getUser(final HttpServletRequest req) {
        final String remoteUser = req.getRemoteUser();
        UserGroupInformation callerUGI = null;
        if (remoteUser != null) {
            callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
        }
        return callerUGI;
    }
    
    private static void rewrapAndThrowException(final Exception e) {
        if (e instanceof UndeclaredThrowableException) {
            if (e.getCause() instanceof AuthorizationException) {
                throw new ForbiddenException(e.getCause());
            }
            throw new WebApplicationException(e.getCause());
        }
        else {
            if (e instanceof AuthorizationException) {
                throw new ForbiddenException(e);
            }
            throw new WebApplicationException(e);
        }
    }
}
