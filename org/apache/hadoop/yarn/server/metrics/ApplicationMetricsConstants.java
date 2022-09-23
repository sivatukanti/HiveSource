// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.metrics;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationMetricsConstants
{
    public static final String ENTITY_TYPE = "YARN_APPLICATION";
    public static final String CREATED_EVENT_TYPE = "YARN_APPLICATION_CREATED";
    public static final String FINISHED_EVENT_TYPE = "YARN_APPLICATION_FINISHED";
    public static final String ACLS_UPDATED_EVENT_TYPE = "YARN_APPLICATION_ACLS_UPDATED";
    public static final String NAME_ENTITY_INFO = "YARN_APPLICATION_NAME";
    public static final String TYPE_ENTITY_INFO = "YARN_APPLICATION_TYPE";
    public static final String USER_ENTITY_INFO = "YARN_APPLICATION_USER";
    public static final String QUEUE_ENTITY_INFO = "YARN_APPLICATION_QUEUE";
    public static final String SUBMITTED_TIME_ENTITY_INFO = "YARN_APPLICATION_SUBMITTED_TIME";
    public static final String APP_VIEW_ACLS_ENTITY_INFO = "YARN_APPLICATION_VIEW_ACLS";
    public static final String DIAGNOSTICS_INFO_EVENT_INFO = "YARN_APPLICATION_DIAGNOSTICS_INFO";
    public static final String FINAL_STATUS_EVENT_INFO = "YARN_APPLICATION_FINAL_STATUS";
    public static final String STATE_EVENT_INFO = "YARN_APPLICATION_STATE";
    public static final String LATEST_APP_ATTEMPT_EVENT_INFO = "YARN_APPLICATION_LATEST_APP_ATTEMPT";
}
