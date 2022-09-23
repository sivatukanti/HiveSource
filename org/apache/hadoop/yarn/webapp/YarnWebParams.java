// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public interface YarnWebParams
{
    public static final String NM_NODENAME = "nm.id";
    public static final String APPLICATION_ID = "app.id";
    public static final String APPLICATION_ATTEMPT_ID = "appattempt.id";
    public static final String CONTAINER_ID = "container.id";
    public static final String CONTAINER_LOG_TYPE = "log.type";
    public static final String ENTITY_STRING = "entity.string";
    public static final String APP_OWNER = "app.owner";
    public static final String APP_STATE = "app.state";
    public static final String QUEUE_NAME = "queue.name";
    public static final String NODE_STATE = "node.state";
}
