// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.metrics;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AppAttemptMetricsConstants
{
    public static final String ENTITY_TYPE = "YARN_APPLICATION_ATTEMPT";
    public static final String REGISTERED_EVENT_TYPE = "YARN_APPLICATION_ATTEMPT_REGISTERED";
    public static final String FINISHED_EVENT_TYPE = "YARN_APPLICATION_ATTEMPT_FINISHED";
    public static final String PARENT_PRIMARY_FILTER = "YARN_APPLICATION_ATTEMPT_PARENT";
    public static final String TRACKING_URL_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_TRACKING_URL";
    public static final String ORIGINAL_TRACKING_URL_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL";
    public static final String HOST_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_HOST";
    public static final String RPC_PORT_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_RPC_PORT";
    public static final String MASTER_CONTAINER_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_MASTER_CONTAINER";
    public static final String DIAGNOSTICS_INFO_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_DIAGNOSTICS_INFO";
    public static final String FINAL_STATUS_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_FINAL_STATUS";
    public static final String STATE_EVENT_INFO = "YARN_APPLICATION_ATTEMPT_STATE";
}
