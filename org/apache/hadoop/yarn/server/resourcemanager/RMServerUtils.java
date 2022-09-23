// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.logging.Log;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.exceptions.InvalidContainerReleaseException;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.exceptions.InvalidResourceBlacklistRequestException;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeState;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;

public class RMServerUtils
{
    public static final ApplicationResourceUsageReport DUMMY_APPLICATION_RESOURCE_USAGE_REPORT;
    
    public static List<RMNode> queryRMNodes(final RMContext context, final EnumSet<NodeState> acceptedStates) {
        final ArrayList<RMNode> results = new ArrayList<RMNode>();
        if (acceptedStates.contains(NodeState.NEW) || acceptedStates.contains(NodeState.RUNNING) || acceptedStates.contains(NodeState.UNHEALTHY)) {
            for (final RMNode rmNode : context.getRMNodes().values()) {
                if (acceptedStates.contains(rmNode.getState())) {
                    results.add(rmNode);
                }
            }
        }
        if (acceptedStates.contains(NodeState.DECOMMISSIONED) || acceptedStates.contains(NodeState.LOST) || acceptedStates.contains(NodeState.REBOOTED)) {
            for (final RMNode rmNode : context.getInactiveRMNodes().values()) {
                if (acceptedStates.contains(rmNode.getState())) {
                    results.add(rmNode);
                }
            }
        }
        return results;
    }
    
    public static void validateResourceRequests(final List<ResourceRequest> ask, final Resource maximumResource, final String queueName, final YarnScheduler scheduler) throws InvalidResourceRequestException {
        for (final ResourceRequest resReq : ask) {
            SchedulerUtils.validateResourceRequest(resReq, maximumResource, queueName, scheduler);
        }
    }
    
    public static void validateBlacklistRequest(final ResourceBlacklistRequest blacklistRequest) throws InvalidResourceBlacklistRequestException {
        if (blacklistRequest != null) {
            final List<String> plus = blacklistRequest.getBlacklistAdditions();
            if (plus != null && plus.contains("*")) {
                throw new InvalidResourceBlacklistRequestException("Cannot add * to the blacklist!");
            }
        }
    }
    
    public static void validateContainerReleaseRequest(final List<ContainerId> containerReleaseList, final ApplicationAttemptId appAttemptId) throws InvalidContainerReleaseException {
        for (final ContainerId cId : containerReleaseList) {
            if (!appAttemptId.equals(cId.getApplicationAttemptId())) {
                throw new InvalidContainerReleaseException("Cannot release container : " + cId.toString() + " not belonging to this application attempt : " + appAttemptId);
            }
        }
    }
    
    public static UserGroupInformation verifyAccess(final AccessControlList acl, final String method, final Log LOG) throws IOException {
        return verifyAccess(acl, method, "AdminService", LOG);
    }
    
    public static UserGroupInformation verifyAccess(final AccessControlList acl, final String method, final String module, final Log LOG) throws IOException {
        UserGroupInformation user;
        try {
            user = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ioe) {
            LOG.warn("Couldn't get current user", ioe);
            RMAuditLogger.logFailure("UNKNOWN", method, acl.toString(), "AdminService", "Couldn't get current user");
            throw ioe;
        }
        if (!acl.isUserAllowed(user)) {
            LOG.warn("User " + user.getShortUserName() + " doesn't have permission" + " to call '" + method + "'");
            RMAuditLogger.logFailure(user.getShortUserName(), method, acl.toString(), module, "Unauthorized user");
            throw new AccessControlException("User " + user.getShortUserName() + " doesn't have permission" + " to call '" + method + "'");
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(method + " invoked by user " + user.getShortUserName());
        }
        return user;
    }
    
    public static YarnApplicationState createApplicationState(final RMAppState rmAppState) {
        switch (rmAppState) {
            case NEW: {
                return YarnApplicationState.NEW;
            }
            case NEW_SAVING: {
                return YarnApplicationState.NEW_SAVING;
            }
            case SUBMITTED: {
                return YarnApplicationState.SUBMITTED;
            }
            case ACCEPTED: {
                return YarnApplicationState.ACCEPTED;
            }
            case RUNNING: {
                return YarnApplicationState.RUNNING;
            }
            case FINISHING:
            case FINISHED: {
                return YarnApplicationState.FINISHED;
            }
            case KILLED: {
                return YarnApplicationState.KILLED;
            }
            case FAILED: {
                return YarnApplicationState.FAILED;
            }
            default: {
                throw new YarnRuntimeException("Unknown state passed!");
            }
        }
    }
    
    public static YarnApplicationAttemptState createApplicationAttemptState(final RMAppAttemptState rmAppAttemptState) {
        switch (rmAppAttemptState) {
            case NEW: {
                return YarnApplicationAttemptState.NEW;
            }
            case SUBMITTED: {
                return YarnApplicationAttemptState.SUBMITTED;
            }
            case SCHEDULED: {
                return YarnApplicationAttemptState.SCHEDULED;
            }
            case ALLOCATED: {
                return YarnApplicationAttemptState.ALLOCATED;
            }
            case LAUNCHED: {
                return YarnApplicationAttemptState.LAUNCHED;
            }
            case ALLOCATED_SAVING:
            case LAUNCHED_UNMANAGED_SAVING: {
                return YarnApplicationAttemptState.ALLOCATED_SAVING;
            }
            case RUNNING: {
                return YarnApplicationAttemptState.RUNNING;
            }
            case FINISHING: {
                return YarnApplicationAttemptState.FINISHING;
            }
            case FINISHED: {
                return YarnApplicationAttemptState.FINISHED;
            }
            case KILLED: {
                return YarnApplicationAttemptState.KILLED;
            }
            case FAILED: {
                return YarnApplicationAttemptState.FAILED;
            }
            default: {
                throw new YarnRuntimeException("Unknown state passed!");
            }
        }
    }
    
    public static void processRMProxyUsersConf(final Configuration conf) {
        final Map<String, String> rmProxyUsers = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : conf) {
            final String propName = entry.getKey();
            if (propName.startsWith("yarn.resourcemanager.proxyuser.")) {
                rmProxyUsers.put("hadoop.proxyuser." + propName.substring("yarn.resourcemanager.proxyuser.".length()), entry.getValue());
            }
        }
        for (final Map.Entry<String, String> entry : rmProxyUsers.entrySet()) {
            conf.set(entry.getKey(), entry.getValue());
        }
    }
    
    static {
        DUMMY_APPLICATION_RESOURCE_USAGE_REPORT = BuilderUtils.newApplicationResourceUsageReport(-1, -1, Resources.createResource(-1, -1), Resources.createResource(-1, -1), Resources.createResource(-1, -1), 0L, 0L);
    }
}
