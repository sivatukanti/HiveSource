// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.metrics;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ContainerMetricsConstants
{
    public static final String ENTITY_TYPE = "YARN_CONTAINER";
    public static final String CREATED_EVENT_TYPE = "YARN_CONTAINER_CREATED";
    public static final String FINISHED_EVENT_TYPE = "YARN_CONTAINER_FINISHED";
    public static final String PARENT_PRIMARIY_FILTER = "YARN_CONTAINER_PARENT";
    public static final String ALLOCATED_MEMORY_ENTITY_INFO = "YARN_CONTAINER_ALLOCATED_MEMORY";
    public static final String ALLOCATED_VCORE_ENTITY_INFO = "YARN_CONTAINER_ALLOCATED_VCORE";
    public static final String ALLOCATED_HOST_ENTITY_INFO = "YARN_CONTAINER_ALLOCATED_HOST";
    public static final String ALLOCATED_PORT_ENTITY_INFO = "YARN_CONTAINER_ALLOCATED_PORT";
    public static final String ALLOCATED_PRIORITY_ENTITY_INFO = "YARN_CONTAINER_ALLOCATED_PRIORITY";
    public static final String DIAGNOSTICS_INFO_EVENT_INFO = "YARN_CONTAINER_DIAGNOSTICS_INFO";
    public static final String EXIT_STATUS_EVENT_INFO = "YARN_CONTAINER_EXIT_STATUS";
    public static final String STATE_EVENT_INFO = "YARN_CONTAINER_STATE";
}
