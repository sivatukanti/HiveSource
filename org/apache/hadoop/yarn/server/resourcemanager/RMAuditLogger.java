// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import java.net.InetAddress;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.commons.logging.Log;

public class RMAuditLogger
{
    private static final Log LOG;
    
    static String createSuccessLog(final String user, final String operation, final String target, final ApplicationId appId, final ApplicationAttemptId attemptId, final ContainerId containerId) {
        final StringBuilder b = new StringBuilder();
        start(Keys.USER, user, b);
        addRemoteIP(b);
        add(Keys.OPERATION, operation, b);
        add(Keys.TARGET, target, b);
        add(Keys.RESULT, "SUCCESS", b);
        if (appId != null) {
            add(Keys.APPID, appId.toString(), b);
        }
        if (attemptId != null) {
            add(Keys.APPATTEMPTID, attemptId.toString(), b);
        }
        if (containerId != null) {
            add(Keys.CONTAINERID, containerId.toString(), b);
        }
        return b.toString();
    }
    
    public static void logSuccess(final String user, final String operation, final String target, final ApplicationId appId, final ContainerId containerId) {
        if (RMAuditLogger.LOG.isInfoEnabled()) {
            RMAuditLogger.LOG.info(createSuccessLog(user, operation, target, appId, null, containerId));
        }
    }
    
    public static void logSuccess(final String user, final String operation, final String target, final ApplicationId appId, final ApplicationAttemptId attemptId) {
        if (RMAuditLogger.LOG.isInfoEnabled()) {
            RMAuditLogger.LOG.info(createSuccessLog(user, operation, target, appId, attemptId, null));
        }
    }
    
    public static void logSuccess(final String user, final String operation, final String target, final ApplicationId appId) {
        if (RMAuditLogger.LOG.isInfoEnabled()) {
            RMAuditLogger.LOG.info(createSuccessLog(user, operation, target, appId, null, null));
        }
    }
    
    public static void logSuccess(final String user, final String operation, final String target) {
        if (RMAuditLogger.LOG.isInfoEnabled()) {
            RMAuditLogger.LOG.info(createSuccessLog(user, operation, target, null, null, null));
        }
    }
    
    static String createFailureLog(final String user, final String operation, final String perm, final String target, final String description, final ApplicationId appId, final ApplicationAttemptId attemptId, final ContainerId containerId) {
        final StringBuilder b = new StringBuilder();
        start(Keys.USER, user, b);
        addRemoteIP(b);
        add(Keys.OPERATION, operation, b);
        add(Keys.TARGET, target, b);
        add(Keys.RESULT, "FAILURE", b);
        add(Keys.DESCRIPTION, description, b);
        add(Keys.PERMISSIONS, perm, b);
        if (appId != null) {
            add(Keys.APPID, appId.toString(), b);
        }
        if (attemptId != null) {
            add(Keys.APPATTEMPTID, attemptId.toString(), b);
        }
        if (containerId != null) {
            add(Keys.CONTAINERID, containerId.toString(), b);
        }
        return b.toString();
    }
    
    public static void logFailure(final String user, final String operation, final String perm, final String target, final String description, final ApplicationId appId, final ContainerId containerId) {
        if (RMAuditLogger.LOG.isWarnEnabled()) {
            RMAuditLogger.LOG.warn(createFailureLog(user, operation, perm, target, description, appId, null, containerId));
        }
    }
    
    public static void logFailure(final String user, final String operation, final String perm, final String target, final String description, final ApplicationId appId, final ApplicationAttemptId attemptId) {
        if (RMAuditLogger.LOG.isWarnEnabled()) {
            RMAuditLogger.LOG.warn(createFailureLog(user, operation, perm, target, description, appId, attemptId, null));
        }
    }
    
    public static void logFailure(final String user, final String operation, final String perm, final String target, final String description, final ApplicationId appId) {
        if (RMAuditLogger.LOG.isWarnEnabled()) {
            RMAuditLogger.LOG.warn(createFailureLog(user, operation, perm, target, description, appId, null, null));
        }
    }
    
    public static void logFailure(final String user, final String operation, final String perm, final String target, final String description) {
        if (RMAuditLogger.LOG.isWarnEnabled()) {
            RMAuditLogger.LOG.warn(createFailureLog(user, operation, perm, target, description, null, null, null));
        }
    }
    
    static void addRemoteIP(final StringBuilder b) {
        final InetAddress ip = Server.getRemoteIp();
        if (ip != null) {
            add(Keys.IP, ip.getHostAddress(), b);
        }
    }
    
    static void start(final Keys key, final String value, final StringBuilder b) {
        b.append(key.name()).append("=").append(value);
    }
    
    static void add(final Keys key, final String value, final StringBuilder b) {
        b.append('\t').append(key.name()).append("=").append(value);
    }
    
    static {
        LOG = LogFactory.getLog(RMAuditLogger.class);
    }
    
    enum Keys
    {
        USER, 
        OPERATION, 
        TARGET, 
        RESULT, 
        IP, 
        PERMISSIONS, 
        DESCRIPTION, 
        APPID, 
        APPATTEMPTID, 
        CONTAINERID;
    }
    
    public static class AuditConstants
    {
        static final String SUCCESS = "SUCCESS";
        static final String FAILURE = "FAILURE";
        static final String KEY_VAL_SEPARATOR = "=";
        static final char PAIR_SEPARATOR = '\t';
        public static final String KILL_APP_REQUEST = "Kill Application Request";
        public static final String SUBMIT_APP_REQUEST = "Submit Application Request";
        public static final String MOVE_APP_REQUEST = "Move Application Request";
        public static final String FINISH_SUCCESS_APP = "Application Finished - Succeeded";
        public static final String FINISH_FAILED_APP = "Application Finished - Failed";
        public static final String FINISH_KILLED_APP = "Application Finished - Killed";
        public static final String REGISTER_AM = "Register App Master";
        public static final String AM_ALLOCATE = "App Master Heartbeats";
        public static final String UNREGISTER_AM = "Unregister App Master";
        public static final String ALLOC_CONTAINER = "AM Allocated Container";
        public static final String RELEASE_CONTAINER = "AM Released Container";
        public static final String UNAUTHORIZED_USER = "Unauthorized user";
        public static final String SUBMIT_RESERVATION_REQUEST = "Submit Reservation Request";
        public static final String UPDATE_RESERVATION_REQUEST = "Update Reservation Request";
        public static final String DELETE_RESERVATION_REQUEST = "Delete Reservation Request";
    }
}
